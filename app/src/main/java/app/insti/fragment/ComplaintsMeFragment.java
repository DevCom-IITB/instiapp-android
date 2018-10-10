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

public class ComplaintsMeFragment extends Fragment {

    private static String uID, sID, uProfileUrl;
    private Activity activity;
    RecyclerView recyclerViewMe;
    private ComplaintsAdapter meListAdapter;
    private TextView error_message_me;
    private SwipeRefreshLayout swipeContainer;
    private static String TAG = ComplaintsMeFragment.class.getSimpleName();
    private boolean isCalled = false;

    public static ComplaintsMeFragment getInstance(String sessionID, String userID, String userProfileUrl) {
        sID = sessionID;
        uID = userID;
        uProfileUrl = userProfileUrl;
        return new ComplaintsMeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                getMeItems();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_complaints_me, container, false);
        recyclerViewMe = view.findViewById(R.id.recyclerViewMe);
        meListAdapter = new ComplaintsAdapter(getActivity(), sID, uID, uProfileUrl);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        error_message_me = view.findViewById(R.id.error_message_me);

        LinearLayoutManager llm = new LinearLayoutManager(activity);
        recyclerViewMe.setLayoutManager(llm);
        recyclerViewMe.setHasFixedSize(true);
        recyclerViewMe.setAdapter(meListAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMeItems();
            }
        });
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        if (!isCalled) {
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    getMeItems();
                }
            });
            isCalled = true;
        }
        return view;
    }

    private void getMeItems() {
        callServerToGetMyComplaints();
    }

    private void callServerToGetMyComplaints() {
        try {
            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
            retrofitInterface.getUserComplaints("sessionid=" + sID).enqueue(new Callback<List<Venter.Complaint>>() {
                @Override
                public void onResponse(@NonNull Call<List<Venter.Complaint>> call, @NonNull Response<List<Venter.Complaint>> response) {
                    if (response.body() != null && !(response.body().isEmpty())) {
                        Log.i(TAG, "response.body != null");
                        Log.i(TAG, "response: " + response.body());
                        initialiseRecyclerView(response.body());
                        swipeContainer.setRefreshing(false);
                    } else {
                        error_message_me.setVisibility(View.VISIBLE);
                        error_message_me.setText(getString(R.string.no_complaints));
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Venter.Complaint>> call, @NonNull Throwable t) {
                    Log.i(TAG, "failure" + t.toString());
                    swipeContainer.setRefreshing(false);
                    error_message_me.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            swipeContainer.setRefreshing(false);
        }
    }

    private void initialiseRecyclerView(List<Venter.Complaint> list) {
        meListAdapter.setcomplaintList(list);
        meListAdapter.notifyDataSetChanged();
    }
}
