package app.insti.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.BodyAdapter;
import app.insti.adapter.FeedAdapter;
import app.insti.adapter.UserAdapter;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.User;
import app.insti.api.response.ExploreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    private String sessionId;
    private static List<Body> allBodies = new ArrayList<>();
    private static List<Body> bodies = new ArrayList<>();
    private static List<Event> events = new ArrayList<>();
    private static List<User> users = new ArrayList<>();

    private BodyAdapter bodyAdapter;
    private FeedAdapter eventsAdapter;
    private UserAdapter userAdapter;

    private String currentQuery = null;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize
        sessionId = ((MainActivity) getActivity()).getSessionIDHeader();
        initRecyclerViews();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Explore");

        // Get all bodies
        if (allBodies.size() == 0) {
            RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
            retrofitInterface.getAllBodies(sessionId).enqueue(new EmptyCallback<List<Body>>() {
                @Override
                public void onResponse(Call<List<Body>> call, Response<List<Body>> response) {
                    allBodies = response.body();
                    bodies = allBodies;
                    updateAdapters(allBodies, new ArrayList<Event>(), new ArrayList<User>());
                }
            });
        } else {
            updateAdapters(bodies, events, users);
            getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        // Search on text change in search
        final EditText searchEditText = getView().findViewById(R.id.explore_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchEditText.getText().length() >= 3) {
                    doSearch(searchEditText.getText().toString());
                } else if (searchEditText.getText().length() == 0) {
                    updateAdapters(allBodies, new ArrayList<Event>(), new ArrayList<User>());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    public void doSearch(final String query) {
        if (getActivity() == null || getView() == null) return;

        // Show loading spinner
        getView().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        // Set the lastest query
        currentQuery = query;

        // Make request
        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.search(sessionId, query).enqueue(new EmptyCallback<ExploreResponse>() {
            @Override
            public void onResponse(Call<ExploreResponse> call, Response<ExploreResponse> response) {
                // Check if we already have a new query pending
                if (!currentQuery.equals(query)) {
                    return;
                }

                // Get data
                bodies = response.body().getBodies();
                events = response.body().getEvents();
                users = response.body().getUsers();
                updateAdapters(bodies, events, users);
            }
        });
    }

    private void updateAdapters(List<Body> bodies, List<Event> events, List<User> users) {
        if (getActivity() == null || getView() == null) return;
        // Make spinner gone
        getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // Set adapters data
        bodyAdapter.setBodyList(bodies);
        eventsAdapter.setEvents(events);
        userAdapter.setUserList(users);

        // Notify all adapters
        bodyAdapter.notifyDataSetChanged();
        eventsAdapter.notifyDataSetChanged();
        userAdapter.notifyDataSetChanged();
    }

    public void initRecyclerViews() {
        if (getActivity() == null || getView() == null) return;
        // Bodies
        RecyclerView bodiesRecyclerView = getView().findViewById(R.id.explore_body_recycler_view);
        bodyAdapter = new BodyAdapter(bodies, this);
        bodiesRecyclerView.setAdapter(bodyAdapter);
        bodiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Events
        RecyclerView eventsRecyclerView = getView().findViewById(R.id.explore_event_recycler_view);
        eventsAdapter = new FeedAdapter(events, this);
        eventsRecyclerView.setAdapter(eventsAdapter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Users
        RecyclerView usersRecyclerView = getView().findViewById(R.id.explore_user_recycler_view);
        userAdapter = new UserAdapter(users, this);
        usersRecyclerView.setAdapter(userAdapter);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
