package app.insti.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.insti.R;
import app.insti.adapter.ComplaintsAdapter;

public class RelevantComplaintsFragment extends Fragment {

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
        RecyclerView recyclerViewRelevantComplaints = (RecyclerView) view.findViewById(R.id.recyclerViewRelevantComplaints);
        ComplaintsAdapter relevantComplaintsAdapter = new ComplaintsAdapter(getActivity(), sID, uID, ""); //Change userProfileUrl to the current user Profile Pic
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
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
}
