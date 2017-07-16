package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.NotificationsAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.model.AppNotification;
import in.ac.iitb.gymkhana.iitbapp.api.model.NotificationsResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {

    RecyclerView notificationsRecyclerView;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        String notificationsResponseJson = bundle.getString(Constants.NOTIFICATIONS_RESPONSE_JSON);
        NotificationsResponse notificationsResponse = new Gson().fromJson(notificationsResponseJson, NotificationsResponse.class);
        showNotifications(notificationsResponse);
    }

    private void showNotifications(NotificationsResponse notificationsResponse) {
        List<AppNotification> notifications = notificationsResponse.getNotifications();
        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //TODO: What to do?
            }
        });
        notificationsRecyclerView = (RecyclerView) getActivity().findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setAdapter(notificationsAdapter);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
