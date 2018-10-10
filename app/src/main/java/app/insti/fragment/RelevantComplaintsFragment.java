package app.insti.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.insti.R;
import app.insti.adapter.ComplaintsAdapter;
import app.insti.api.model.Venter;

public class RelevantComplaintsFragment extends Fragment {

    private Activity activity;
    private ComplaintsAdapter relevantComplaintsAdapter;
    private RecyclerView recyclerViewRelevantComplaints;
    private SwipeRefreshLayout swipeContainer;
    private boolean isCalled = false;
    private static String sID, uID;

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
        TextView error_message_relevant_complaints = view.findViewById(R.id.error_message_relevant_complaints);

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
        //Get Relevant Complaints from Server
    }

    private void initialiseRecyclerView(List<Venter.Complaint> list) {
        relevantComplaintsAdapter.setcomplaintList(list);
        relevantComplaintsAdapter.notifyDataSetChanged();
    }
}
