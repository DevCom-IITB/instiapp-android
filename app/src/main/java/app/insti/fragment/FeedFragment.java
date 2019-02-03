package app.insti.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.insti.ActivityBuffer;
import app.insti.R;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.adapter.FeedAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Event;
import app.insti.api.response.NewsFeedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends BaseFragment {

    private RecyclerView feedRecyclerView;
    private SwipeRefreshLayout feedSwipeRefreshLayout;
    private FloatingActionButton fab;
    LinearLayoutManager mLayoutManager;
    private int index = -1, top = -1;
    private FeedAdapter feedAdapter = null;

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
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_feed);

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
        fab = getView().findViewById(R.id.fab);

        // Initialize the feed
        if (Utils.eventCache == null || Utils.eventCache.isEmpty()) {
            updateFeed();
        } else {
            displayEvents(Utils.eventCache);
        }
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
        if(index != -1) {
            mLayoutManager.scrollToPositionWithOffset( index, top);
        }
    }

    private void updateFeed() {

        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getNewsFeed(Utils.getSessionIDHeader()).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    Utils.eventCache.setList(response.body().getEvents());
                    displayEvents(Utils.eventCache);
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

    /** Initialize the add event fab if the user has permission */
    private void initFab() {
        if (((MainActivity) getActivity()).createEventAccess()) {
            fab.show();
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
    }

    private void displayEvents(final List<Event> events) {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;

        /* Initialize */
        initFab();

        /* Make first event image big */
        if (events.size() >= 1) {
            events.get(0).setEventBigImage(true);
        }

        // Initialize adapter
        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter(events, this);
        } else {
            feedAdapter.setList(events);
            feedAdapter.notifyDataSetChanged();
        }

        // Initialize RecyclerView if necessary
        if (feedRecyclerView.getAdapter() != feedAdapter) {
            initRecyclerView();
        }

        View view = getActivity().findViewById(R.id.loadingPanel);
        if (view != null)
            view.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
            @Override
            public void run(Activity pActivity) {
                try {
                    feedRecyclerView.setAdapter(feedAdapter);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
