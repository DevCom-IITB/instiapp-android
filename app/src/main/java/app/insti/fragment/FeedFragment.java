package app.insti.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.List;

import app.insti.ActivityBuffer;
import app.insti.Constants;
import app.insti.ItemClickListener;
import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.FeedAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.model.NewsFeedResponse;
import app.insti.data.AppDatabase;
import app.insti.data.Event;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends BaseFragment {

    private RecyclerView feedRecyclerView;
    private SwipeRefreshLayout feedSwipeRefreshLayout;
    private AppDatabase appDatabase;
    private FloatingActionButton fab;
    private boolean freshEventsDisplayed = false;
    LinearLayoutManager mLayoutManager;
    public static int index = -1, top = -1;
    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Feed");
        feedRecyclerView = view.findViewById(R.id.feed_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        feedRecyclerView.setLayoutManager(mLayoutManager);
        feedSwipeRefreshLayout = view.findViewById(R.id.feed_swipe_refresh_layout);
        feedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appDatabase = AppDatabase.getAppDatabase(getContext());
        //new showEventsFromDB().execute();
        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        updateFeed();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        index = mLayoutManager.findFirstVisibleItemPosition();
        View v = feedRecyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - feedRecyclerView.getPaddingTop());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(index != -1)
        {
            mLayoutManager.scrollToPositionWithOffset( index, top);
        }
    }

    private void updateFeed() {

        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.getNewsFeed("sessionid=" + getArguments().getString(Constants.SESSION_ID)).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    NewsFeedResponse newsFeedResponse = response.body();
                    List<Event> events = newsFeedResponse.getEvents();
                    freshEventsDisplayed = true;
                    displayEvents(events);
                }
                //Server Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                //Network Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void displayEvents(final List<Event> events) {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;

        if (((MainActivity) getActivity()).createEventAccess()) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddEventFragment addEventFragment = new AddEventFragment();
                    Bundle bundle = new Bundle();
                    addEventFragment.setArguments(bundle);
                    ((MainActivity) getActivity()).updateFragment(addEventFragment);
                }
            });
            feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) fab.hide();
                    else if (dy < 0) fab.show();
                }
            });
        }

        /* Make first event image big */
        if (events.size() > 1) {
            events.get(0).setEventBigImage(true);
        }

        final FeedAdapter feedAdapter = new FeedAdapter(events, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String eventJson = new Gson().toJson(events.get(position));
                Bundle bundle = getArguments();
                if (bundle == null)
                    bundle = new Bundle();
                bundle.putString(Constants.EVENT_JSON, eventJson);
                EventFragment eventFragment = new EventFragment();
                eventFragment.setArguments(bundle);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                transaction.addToBackStack(eventFragment.getTag()).commit();
            }
        });
        getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
            @Override
            public void run(Activity pActivity) {
                try {
                    feedRecyclerView.setAdapter(feedAdapter);
                    //feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        View view = getActivity().findViewById(R.id.loadingPanel);
        if (view != null)
            view.setVisibility(View.GONE);
    }
}
