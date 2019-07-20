package app.insti.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.adapter.ComplaintsAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Venter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintsHomeFragment extends Fragment {

    private ComplaintsAdapter homeListAdapter;
    private SwipeRefreshLayout swipeContainer;

    public static String TAG = ComplaintsHomeFragment.class.getSimpleName();
    private boolean isCalled = false;
    private TextView error_message_home;
    private static String uID, uProfileUrl;
    private boolean networkBusy = false;
    private int currentIndex = 0;
    private List<Venter.Complaint> complaints;

    public static ComplaintsHomeFragment getInstance(String userID, String userProfileUrl) {
        uID = userID;
        uProfileUrl = userProfileUrl;
        return new ComplaintsHomeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                if (complaints == null) {
                    callServerToGetNearbyComplaints();
                    swipeContainer.setRefreshing(true);
                } else {
                    initialiseRecyclerView(complaints);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaints_home, container, false);
        RecyclerView recyclerViewHome = view.findViewById(R.id.recyclerViewHome);
        homeListAdapter = new ComplaintsAdapter(getActivity(), uID, uProfileUrl);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        error_message_home = view.findViewById(R.id.error_message_home);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerViewHome.setLayoutManager(llm);
        recyclerViewHome.setHasFixedSize(true);
        recyclerViewHome.setAdapter(homeListAdapter);

        recyclerViewHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (networkBusy || currentIndex == -1) return;

                if (!recyclerView.canScrollVertically(1)) {
                    networkBusy = true;
                    swipeContainer.setRefreshing(true);
                    RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                    retrofitInterface.getAllComplaints(Utils.getSessionIDHeader(), currentIndex, 5).enqueue(new Callback<List<Venter.Complaint>>() {
                        @Override
                        public void onResponse(Call<List<Venter.Complaint>> call, Response<List<Venter.Complaint>> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null && !response.body().isEmpty()) {
                                    complaints.addAll(response.body());
                                    initialiseRecyclerView(complaints);
                                    currentIndex += 5;
                                } else {
                                    currentIndex = -1;
                                }
                            }
                            networkBusy = false;
                            swipeContainer.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Call<List<Venter.Complaint>> call, Throwable t) {
                            networkBusy = false;
                            swipeContainer.setRefreshing(false);
                        }
                    });

                }
            }
        });

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
            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
            retrofitInterface.getAllComplaints(Utils.getSessionIDHeader(), 0, 5).enqueue(new Callback<List<Venter.Complaint>>() {
                @Override
                public void onResponse(@NonNull Call<List<Venter.Complaint>> call, @NonNull Response<List<Venter.Complaint>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && !(response.body().isEmpty())) {
                            complaints = response.body();
                            currentIndex = complaints.size();
                            initialiseRecyclerView(complaints);
                        } else {
                            error_message_home.setVisibility(View.VISIBLE);
                            error_message_home.setText(getString(R.string.no_complaints));
                        }
                    }
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(@NonNull Call<List<Venter.Complaint>> call, @NonNull Throwable t) {
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

