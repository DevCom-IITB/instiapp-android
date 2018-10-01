package app.insti.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.List;

import app.insti.Constants;
import app.insti.Utils;
import app.insti.api.model.Event;
import app.insti.interfaces.ItemClickListener;
import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.NotificationsAdapter;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Notification;
import app.insti.api.model.PlacementBlogPost;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends BaseFragment {

    RecyclerView notificationsRecyclerView;

    List<Notification> notifications;

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

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");

        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.getNotifications(((MainActivity) getActivity()).getSessionIDHeader()).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    notifications = response.body();
                    showNotifications(notifications);
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
            }
        });
    }

    private void showNotifications(final List<Notification> notifications) {
        /* Check if activity is done with */
        if (getActivity() == null) return;

        /* Hide loader */
        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                /* Get the notification */
                Notification notification = notifications.get(position);

                /* Mark notification read */
                RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
                String sessId = ((MainActivity) getActivity()).getSessionIDHeader();
                retrofitInterface.markNotificationRead(sessId, notification.getNotificationId().toString()).enqueue(new EmptyCallback<Void>());

                /* Open event */
                if (notification.getNotificationActorType().contains("event")) {
                    Gson gson = new Gson();
                    Event event = gson.fromJson(gson.toJson(notification.getNotificationActor()), Event.class) ;
                    Utils.openEventFragment(event, getActivity());
                } else if (notification.getNotificationActorType().contains("newsentry")) {
                    NewsFragment newsFragment = new NewsFragment();
                    Utils.updateFragment(newsFragment, getActivity());
                } else if (notification.getNotificationActorType().contains("blogentry")) {
                    Gson gson = new Gson();
                    PlacementBlogPost post = gson.fromJson(gson.toJson(notification.getNotificationActor()), PlacementBlogPost.class);
                    if (post.getLink().contains("training")) {
                        Utils.updateFragment(new TrainingBlogFragment(), getActivity());
                    } else {
                        Utils.updateFragment(new PlacementBlogFragment(), getActivity());
                    }
                }
            }
        });
        notificationsRecyclerView = (RecyclerView) getActivity().findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setAdapter(notificationsAdapter);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
