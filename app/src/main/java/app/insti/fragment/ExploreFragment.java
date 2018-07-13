package app.insti.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import app.insti.Constants;
import app.insti.ItemClickListener;
import app.insti.MainActivity;
import app.insti.R;
import app.insti.adapter.BodyAdapter;
import app.insti.adapter.FeedAdapter;
import app.insti.adapter.UserAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.model.ExploreResponse;
import app.insti.data.Body;
import app.insti.data.Event;
import app.insti.data.User;
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
    private List<Body> allBodies = new ArrayList<>();
    private List<Body> bodies = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    private BodyAdapter bodyAdapter;
    private FeedAdapter eventsAdapter;
    private UserAdapter userAdapter;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
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

        // Get all bodies
        if (allBodies.size() == 0) {
            RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
            retrofitInterface.getAllBodies(sessionId).enqueue(new Callback<List<Body>>() {
                @Override
                public void onResponse(Call<List<Body>> call, Response<List<Body>> response) {
                    allBodies = response.body();
                    bodies = allBodies;
                    updateAdapters(bodies, new ArrayList<Event>(), new ArrayList<User>());
                }

                @Override
                public void onFailure(Call<List<Body>> call, Throwable t) {}
            });
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
                    bodies = allBodies;
                    updateAdapters(bodies, new ArrayList<Event>(), new ArrayList<User>());
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

    public void doSearch(String query) {
        if (getActivity() == null || getView() == null) return;

        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.search(sessionId, query).enqueue(new Callback<ExploreResponse>() {
            @Override
            public void onResponse(Call<ExploreResponse> call, Response<ExploreResponse> response) {
                // Get data
                bodies = response.body().getBodies();
                events = response.body().getEvents();
                users = response.body().getUsers();
                updateAdapters(bodies, events, users);
            }

            @Override
            public void onFailure(Call<ExploreResponse> call, Throwable t) {
                // Request failed
            }
        });
    }

    private void updateAdapters(List<Body> bodies, List<Event> events, List<User> users) {
        if (getActivity() == null || getView() == null) return;
        // Make spinner gone
        getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // Set adapters data
        bodyAdapter.setBodyList(bodies);
        eventsAdapter.setPosts(events);
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
        bodyAdapter = new BodyAdapter(bodies, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BODY_JSON, new Gson().toJson(bodies.get(position)));
                updateFragment(new BodyFragment(), bundle);
            }
        });
        bodiesRecyclerView.setAdapter(bodyAdapter);
        bodiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Events
        RecyclerView eventsRecyclerView = getView().findViewById(R.id.explore_event_recycler_view);
        eventsAdapter = new FeedAdapter(events, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Event event = events.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EVENT_JSON, new Gson().toJson(event));
                updateFragment(new EventFragment(), bundle);
            }
        });
        eventsRecyclerView.setAdapter(eventsAdapter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Users
        RecyclerView usersRecyclerView = getView().findViewById(R.id.explore_user_recycler_view);
        userAdapter = new UserAdapter(users, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                User user = users.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.USER_ID, user.getUserID());
                updateFragment(new ProfileFragment(), bundle);
            }
        });
        usersRecyclerView.setAdapter(userAdapter);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void updateFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.framelayout_for_fragment, fragment, fragment.getTag());
        ft.addToBackStack(fragment.getTag());
        ft.commit();
    }
}
