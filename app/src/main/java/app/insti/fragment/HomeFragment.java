package app.insti.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.ComplaintsRecyclerViewAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Venter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    Activity activity;
    ComplaintsRecyclerViewAdapter homeListAdapter;
    RecyclerView recyclerViewHome;
    private SwipeRefreshLayout swipeContainer;

    private static String TAG = HomeFragment.class.getSimpleName();
    private boolean isCalled = false;
    private TextView error_message_home;
    static String sID, uID;

    public static HomeFragment getInstance(String sessionID, String userID) {
        sID = sessionID;
        uID = userID;
        return new HomeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                callServerToGetNearbyComplaints();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewHome = (RecyclerView) view.findViewById(R.id.recyclerViewHome);
        homeListAdapter = new ComplaintsRecyclerViewAdapter(getActivity(), sID, uID);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        error_message_home = view.findViewById(R.id.error_message_home);

        LinearLayoutManager llm = new LinearLayoutManager(activity);
        recyclerViewHome.setLayoutManager(llm);
        recyclerViewHome.setHasFixedSize(true);

        recyclerViewHome.setAdapter(homeListAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callServerToGetNearbyComplaints();
            }
        });


        swipeContainer.setColorSchemeResources(R.color.colorPrimary);

        if (!isCalled) {
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    callServerToGetNearbyComplaints();
                }
            });
            isCalled = true;
        }
        return view;
    }

    private void callServerToGetNearbyComplaints() {
        try {
            RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
            retrofitInterface.getAllComplaints("sessionid=" + sID).enqueue(new Callback<List<Venter.Complaint>>() {
                @Override
                public void onResponse(@NonNull Call<List<Venter.Complaint>> call, @NonNull Response<List<Venter.Complaint>> response) {
                    if (response.body() != null && !(response.body().isEmpty())) {
                        Log.i(TAG, "response.body != null");
                        Log.i(TAG, "response: " + response.body());
                        initialiseRecyclerView(response.body());
                        swipeContainer.setRefreshing(false);
                    } else {
                        Log.i(TAG, "response.body is empty");
                        error_message_home.setVisibility(View.VISIBLE);
                        error_message_home.setText(getString(R.string.no_complaints));
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Venter.Complaint>> call, @NonNull Throwable t) {
                    Log.i(TAG, "failure" + t.toString());
                    swipeContainer.setRefreshing(false);
                    error_message_home.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            swipeContainer.setRefreshing(false);
        }
    }

    private void initialiseRecyclerView(List<Venter.Complaint> list) {
        homeListAdapter.setcomplaintList(list);
        homeListAdapter.notifyDataSetChanged();
    }
}
