package app.insti.fragment;


import android.app.Activity;
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
import app.insti.api.model.Event;
import app.insti.api.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends BaseFragment {

    private RecyclerView myEventsFeedRecyclerView;
    private SwipeRefreshLayout myEventsFeedSwipeRefreshLayout;
    private FloatingActionButton fab;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("My Events");

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEventFragment addEventFragment = new AddEventFragment();
                ((MainActivity) getActivity()).updateFragment(addEventFragment);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (((MainActivity) getActivity()).createEventAccess()) {
            fab.setVisibility(View.VISIBLE);
        }

        updateOnRefresh();

        myEventsFeedSwipeRefreshLayout = getActivity().findViewById(R.id.my_events_feed_swipe_refresh_layout);
        myEventsFeedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateOnRefresh();
                myEventsFeedSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateOnRefresh() {
        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.getUserMe(((MainActivity)getActivity()).getSessionIDHeader()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    List<Event> events = user.getUserGoingEvents();
                    events.addAll(user.getUserInterestedEvents());
                    displayEvents(events);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {}
        });
    }

    private void displayEvents(final List<Event> events) {
        /* Check if already destroyed */
        if (getActivity() == null || getView() == null) return;

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
                    myEventsFeedRecyclerView = getActivity().findViewById(R.id.my_events_feed_recycler_view);
                    myEventsFeedRecyclerView.setAdapter(feedAdapter);
                    myEventsFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }
}