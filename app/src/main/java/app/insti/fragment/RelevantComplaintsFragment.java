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
import app.insti.Utils;
import app.insti.adapter.ComplaintsAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Venter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelevantComplaintsFragment extends Fragment {

    Activity activity;
    ComplaintsAdapter relevantComplaintsAdapter;
    RecyclerView recyclerViewRelevantComplaints;
    private SwipeRefreshLayout swipeContainer;

    private static String TAG = RelevantComplaintsFragment.class.getSimpleName();
    private boolean isCalled = false;
    private TextView error_message_relevant_complaints;
    static String sID, uID;

    public static RelevantComplaintsFragment getInstance(String sessionID, String userID) {
        sID = sessionID;
        uID = userID;
        return new RelevantComplaintsFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                callServerToGetRelevantComplaints();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relevant_complaints, container, false);
        recyclerViewRelevantComplaints = (RecyclerView) view.findViewById(R.id.recyclerViewRelevantComplaints);
        relevantComplaintsAdapter = new ComplaintsAdapter(getActivity(), sID, uID, ""); //Change userProfileUrl to the current user Profile Pic
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        error_message_relevant_complaints = view.findViewById(R.id.error_message_relevant_complaints);

        LinearLayoutManager llm = new LinearLayoutManager(activity);
        recyclerViewRelevantComplaints.setLayoutManager(llm);
        recyclerViewRelevantComplaints.setHasFixedSize(true);

        recyclerViewRelevantComplaints.setAdapter(relevantComplaintsAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callServerToGetRelevantComplaints();
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorPrimary);

        if (!isCalled) {
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    callServerToGetRelevantComplaints();
                }
            });
            isCalled = true;
        }

        return view;
    }

    private void callServerToGetRelevantComplaints(){
        try {
            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
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
                        error_message_relevant_complaints.setVisibility(View.VISIBLE);
                        error_message_relevant_complaints.setText(getString(R.string.no_complaints));
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Venter.Complaint>> call, @NonNull Throwable t) {
                    Log.i(TAG, "failure" + t.toString());
                    swipeContainer.setRefreshing(false);
                    error_message_relevant_complaints.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            swipeContainer.setRefreshing(false);
        }
    }

    private void initialiseRecyclerView(List<Venter.Complaint> list) {
        relevantComplaintsAdapter.setcomplaintList(list);
        relevantComplaintsAdapter.notifyDataSetChanged();
    }
}