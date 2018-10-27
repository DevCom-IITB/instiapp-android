package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Event;
import app.insti.api.model.Notification;
import app.insti.api.model.PlacementBlogPost;
import app.insti.fragment.NewsFragment;
import app.insti.fragment.PlacementBlogFragment;
import app.insti.fragment.TrainingBlogFragment;

public class NotificationsAdapter extends CardAdapter<Notification> {
    public NotificationsAdapter(List<Notification> notifications, Fragment fragment) {
        super(notifications, fragment);
    }

    @Override
    public void onClick(Notification notification, FragmentActivity fragmentActivity) {
        /* Mark notification read */
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        String sessId = Utils.getSessionIDHeader();
        retrofitInterface.markNotificationRead(sessId, notification.getNotificationId().toString()).enqueue(new EmptyCallback<Void>());

        /* Open event */
        if (notification.isEvent()) {
            Gson gson = new Gson();
            Event event = gson.fromJson(gson.toJson(notification.getNotificationActor()), Event.class) ;
            Utils.openEventFragment(event, fragmentActivity);
        } else if (notification.isNews()) {
            NewsFragment newsFragment = new NewsFragment();
            Utils.updateFragment(newsFragment, fragmentActivity);
        } else if (notification.isBlogPost()) {
            Gson gson = new Gson();
            PlacementBlogPost post = gson.fromJson(gson.toJson(notification.getNotificationActor()), PlacementBlogPost.class);
            if (post.getLink().contains("training")) {
                Utils.updateFragment(new TrainingBlogFragment(), fragmentActivity);
            } else {
                Utils.updateFragment(new PlacementBlogFragment(), fragmentActivity);
            }
        }
    }

    @Override
    public int getAvatarPlaceholder() {
        return R.drawable.lotus_sq;
    }
}
