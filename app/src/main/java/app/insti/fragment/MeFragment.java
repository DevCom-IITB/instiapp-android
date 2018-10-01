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

public class MeFragment extends Fragment {

    static String uID, sID;
    Activity activity;
    RecyclerView recyclerViewMe;
    ComplaintsRecyclerViewAdapter meListAdapter;
    TextView error_message_me;
    SwipeRefreshLayout swipeContainer;
    private static String TAG = MeFragment.class.getSimpleName();
    private boolean isCalled = false;

    public static MeFragment getInstance(String sessionID, String userID) {
        sID = sessionID;
        uID = userID;
        return new MeFragment();
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
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        recyclerViewMe = view.findViewById(R.id.recyclerViewMe);
        meListAdapter = new ComplaintsRecyclerViewAdapter(getActivity(), sID, uID);
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
            RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
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