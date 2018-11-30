package app.insti.fragment;


import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.insti.R;
import app.insti.UpdatableList;
import app.insti.Utils;
import app.insti.adapter.NotificationsAdapter;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Notification;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends BottomSheetDialogFragment {

    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter = null;

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

        /* Show cached notifications */
        if (Utils.notificationCache != null) {
            showNotifications(Utils.notificationCache);
        } else {
            Utils.notificationCache = new UpdatableList<>();
        }

        /* Update notifications */
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getNotifications(Utils.getSessionIDHeader()).enqueue(new EmptyCallback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    Utils.notificationCache.setList(response.body());
                    showNotifications(Utils.notificationCache);
                }
            }
        });
    }

    private void showNotifications(final List<Notification> notifications) {
        /* Check if activity is done with */
        if (getActivity() == null || getView() == null) return;

        /* Hide loader */
        getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        /* Initialize */
        if (notificationsAdapter == null) {
            notificationsAdapter = new NotificationsAdapter(notifications, this);
            notificationsRecyclerView = (RecyclerView) getView().findViewById(R.id.notifications_recycler_view);
            notificationsRecyclerView.setAdapter(notificationsAdapter);
            notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            notificationsAdapter.setList(notifications);
            notificationsAdapter.notifyDataSetChanged();
        }
    }
}
