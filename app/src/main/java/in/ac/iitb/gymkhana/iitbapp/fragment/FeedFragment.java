package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.FeedAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.data.AppDatabase;
import in.ac.iitb.gymkhana.iitbapp.data.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.ac.iitb.gymkhana.iitbapp.SessionManager.SESSION_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView feedRecyclerView;
    private SwipeRefreshLayout feedSwipeRefreshLayout;
    private AppDatabase appDatabase;
    private FloatingActionButton fab;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_feed, container, false);

        fab=(FloatingActionButton) view.findViewById(R.id.fab);

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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        appDatabase= AppDatabase.getAppDatabase(getContext());
        final List<Event> events=appDatabase.dbDao().getAllEvents();
            FeedAdapter feedAdapter = new FeedAdapter(events, new ItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    String eventJson = new Gson().toJson(events.get(position));
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.EVENT_JSON, eventJson);
                    EventFragment eventFragment = new EventFragment();
                    eventFragment.setArguments(bundle);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                    transaction.commit();
                }
            });
            feedRecyclerView = (RecyclerView) getActivity().findViewById(R.id.feed_recycler_view);
            feedRecyclerView.setAdapter(feedAdapter);
            feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        }

        updateFeed();


        feedSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.feed_swipe_refresh_layout);
        feedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        });
    }

    private void updateFeed() {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getNewsFeed("sessionid=" + getArguments().getString(SESSION_ID)).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    NewsFeedResponse newsFeedResponse = response.body();
                    final List<Event> events = newsFeedResponse.getEvents();

                    FeedAdapter feedAdapter = new FeedAdapter(events, new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            String eventJson = new Gson().toJson(events.get(position));
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.EVENT_JSON, eventJson);
                            EventFragment eventFragment = new EventFragment();
                            eventFragment.setArguments(bundle);
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                            transaction.commit();
                        }
                    });
                    feedRecyclerView = (RecyclerView) getActivity().findViewById(R.id.feed_recycler_view);
                    feedRecyclerView.setAdapter(feedAdapter);
                    feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    appDatabase.dbDao().deleteEvents();
                    appDatabase.dbDao().insertEvents(events);
                    //Server Error
                    feedSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                //Network Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
