package app.insti.fragment;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.adapter.GenericAdapter;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.User;
import app.insti.api.response.ExploreResponse;
import app.insti.interfaces.CardInterface;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;

    private static List<Body> allBodies = new ArrayList<>();
    private static List<Body> bodies = new ArrayList<>();
    private static List<Event> events = new ArrayList<>();
    private static List<User> users = new ArrayList<>();

    private static List<CardInterface> cards = new ArrayList<>();

    private String sessionId;
    private GenericAdapter genericAdapter;

    private String currentQuery = null;
    private int index = -1, top = -1;

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
    public void onPause()
    {
        super.onPause();
        index = mLayoutManager.findFirstVisibleItemPosition();
        View v = recyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - recyclerView.getPaddingTop());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(index != -1) {
            mLayoutManager.scrollToPositionWithOffset( index, top);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize
        sessionId = Utils.getSessionIDHeader();
        initRecyclerView();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Explore");
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_explore);

        final EditText searchEditText = getView().findViewById(R.id.explore_search);

        // Get all bodies
        if (allBodies.size() == 0) {
            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
            retrofitInterface.getAllBodies(sessionId).enqueue(new EmptyCallback<List<Body>>() {
                @Override
                public void onResponse(Call<List<Body>> call, Response<List<Body>> response) {
                    allBodies = response.body();
                    bodies = allBodies;
                    updateAdapter(allBodies, new ArrayList<Event>(), new ArrayList<User>());
                }
            });
        } else {
            // Check if search box is not empty
            if (searchEditText.getText() != null && !searchEditText.getText().toString().equals("")) {
                updateAdapter(bodies, events, users);
            } else {
                updateAdapter(allBodies, new ArrayList<Event>(), new ArrayList<User>());
            }
            getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        // Close keyboard on search click
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    MainActivity.hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        // Search on text change in search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString();
                if (currentQuery.length() >= 3) {
                    doSearch(searchEditText.getText().toString());
                } else if (currentQuery.length() == 0) {
                    updateAdapter(allBodies, new ArrayList<Event>(), new ArrayList<User>());
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

        // Set the lastest query
        currentQuery = query;

        // Make request
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
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
                updateAdapter(bodies, events, users);
            }
        });
    }

    private void updateAdapter(List<Body> bodies, List<Event> events, List<User> users) {
        if (getActivity() == null || getView() == null) return;

        // Make spinner gone
        getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // Build cards
        cards.clear();
        cards.addAll(bodies);
        cards.addAll(events);
        cards.addAll(users);
        genericAdapter.setList(cards);

        // Notify adapter
        genericAdapter.notifyDataSetChanged();
    }

    public void initRecyclerView() {
        if (getActivity() == null || getView() == null) return;

        recyclerView = getView().findViewById(R.id.explore_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        genericAdapter = new GenericAdapter(cards, this);
        recyclerView.setAdapter(genericAdapter);
        recyclerView.setLayoutManager(mLayoutManager);
    }
}