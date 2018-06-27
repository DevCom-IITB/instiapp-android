package in.ac.iitb.gymkhana.iitbapp.fragment;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.ActivityBuffer;
import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.FeedAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.data.AppDatabase;
import in.ac.iitb.gymkhana.iitbapp.data.Event;
import in.ac.iitb.gymkhana.iitbapp.data.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends BaseFragment {

    private RecyclerView feedRecyclerView;
    private SwipeRefreshLayout feedSwipeRefreshLayout;
    private AppDatabase appDatabase;
    private FloatingActionButton fab;
    private User userData;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
//        if (userData.getUserGoingEvents().size() != 0){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddEventFragment addEventFragment = new AddEventFragment();
                    addEventFragment.setArguments(getArguments());
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.relative_layout, addEventFragment);
                    ft.addToBackStack("addEvent");
                    ft.commit();
                }
            });
//        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        appDatabase = AppDatabase.getAppDatabase(getContext());
        new showEventsFromDB().execute();

        updateFeed();

        feedSwipeRefreshLayout = getActivity().findViewById(R.id.feed_swipe_refresh_layout);
        feedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        });
    }

    private void updateFeed() {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getNewsFeed("sessionid=" + getArguments().getString(Constants.SESSION_ID)).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    NewsFeedResponse newsFeedResponse = response.body();
                    List<Event> events = newsFeedResponse.getEvents();
                    displayEvents(events);

                    new updateDatabase().execute(events);
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

    private class updateDatabase extends AsyncTask<List<Event>, Void, Integer> {
        @Override
        protected Integer doInBackground(List<Event>... events) {
            appDatabase.dbDao().deleteEvents();
            appDatabase.dbDao().insertEvents(events[0]);
            return 1;
        }
    }

    private class showEventsFromDB extends AsyncTask<String, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(String... events) {
            return appDatabase.dbDao().getAllEvents();
        }
        protected void onPostExecute(List<Event> result) {
            displayEvents(result);
        }
    }

    private void displayEvents(final List<Event> events) {
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
                transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                transaction.addToBackStack(eventFragment.getTag()).commit();
            }
        });
        getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
            @Override
            public void run(Activity pActivity) {
                try {
                    feedRecyclerView = getActivity().findViewById(R.id.feed_recycler_view);
                    feedRecyclerView.setAdapter(feedAdapter);
                    feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
