package app.insti.fragment;


import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import app.insti.R;
import app.insti.UpdatableList;
import app.insti.Utils;
import app.insti.adapter.NotificationsAdapter;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Notification;
import app.insti.notifications.NotificationId;
import me.leolin.shortcutbadger.ShortcutBadger;
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

                    NotificationId.setCurrentCount(Utils.notificationCache.size());
                    ShortcutBadger.applyCount(getContext().getApplicationContext(), NotificationId.getCurrentCount());
                }
            }
        });
    }

    private void showNotifications(@Nullable final List<Notification> notifications) {
        /* Check if activity is done with */
        if (getActivity() == null || getView() == null) return;

        /* Hide loader */
        getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        /* Check if there's nothing to show */
        TextView noNotifs = getView().findViewById(R.id.no_notifs);
        if (notifications == null || notifications.size() == 0) {
            noNotifs.setVisibility(View.VISIBLE);
            return;
        } else {
            noNotifs.setVisibility(View.GONE);
        }

        /* Initialize */
        if (notificationsAdapter == null) {
            notificationsAdapter = new NotificationsAdapter(notifications, this);
            notificationsRecyclerView = (RecyclerView) getView().findViewById(R.id.notifications_recycler_view);
            notificationsRecyclerView.setAdapter(notificationsAdapter);
            notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            notificationsRecyclerView.setItemAnimator(null);

            /* Handle swiping of notifications */
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        // Fade out the view when it is swiped out of the parent
                        final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                        viewHolder.itemView.setAlpha(alpha);
                        viewHolder.itemView.setTranslationX(dX);
                    } else {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //swiped position
                    final String id = Utils.notificationCache.get(position).getNotificationId().toString();
                    Utils.notificationCache.remove(position);
                    notificationsAdapter.notifyItemRemoved(position);
                    Utils.getRetrofitInterface().markNotificationDeleted(Utils.getSessionIDHeader(), id).enqueue(new EmptyCallback<Void>());
                    NotificationId.setCurrentCount(Utils.notificationCache.size());
                    ShortcutBadger.applyCount(getContext().getApplicationContext(), NotificationId.getCurrentCount());
                    if (Utils.notificationCache.size() == 0) showNotifications(null);
                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(notificationsRecyclerView);

        } else {
            notificationsAdapter.setList(notifications);
            notificationsAdapter.notifyDataSetChanged();
        }
    }
}
