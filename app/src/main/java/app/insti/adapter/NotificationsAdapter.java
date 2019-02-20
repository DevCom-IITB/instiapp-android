package app.insti.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Event;
import app.insti.api.model.NewsArticle;
import app.insti.api.model.Notification;
import app.insti.api.model.PlacementBlogPost;
import app.insti.fragment.NewsFragment;
import app.insti.fragment.NotificationsFragment;
import app.insti.fragment.PlacementBlogFragment;
import app.insti.fragment.TrainingBlogFragment;
import app.insti.notifications.NotificationId;
import me.leolin.shortcutbadger.ShortcutBadger;

public class NotificationsAdapter extends CardAdapter<Notification> {
    private NotificationsFragment notificationsFragment;

    public NotificationsAdapter(List<Notification> notifications, Fragment fragment) {
        super(notifications, fragment);
        notificationsFragment = (NotificationsFragment) fragment;
    }

    @Override
    public void onClick(Notification notification, FragmentActivity fragmentActivity) {
        /* Mark notification read */
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        String sessId = Utils.getSessionIDHeader();
        retrofitInterface.markNotificationRead(sessId, notification.getNotificationId().toString()).enqueue(new EmptyCallback<Void>());
        ShortcutBadger.applyCount(fragmentActivity.getApplicationContext(), NotificationId.decrementAndGetCurrentCount());

        /* Close the bottom sheet */
        notificationsFragment.dismiss();

        Gson gson = Utils.gson;

        /* Open event */
        if (notification.isEvent()) {
            Event event = gson.fromJson(gson.toJson(notification.getNotificationActor()), Event.class) ;
            Utils.openEventFragment(event, fragmentActivity);
        } else if (notification.isNews()) {
            NewsFragment newsFragment = new NewsFragment();
            NewsArticle newsArticle = gson.fromJson(gson.toJson(notification.getNotificationActor()), NewsArticle.class) ;
            newsFragment.withId(newsArticle.getId());
            Utils.updateFragment(newsFragment, fragmentActivity);
        } else if (notification.isBlogPost()) {
            PlacementBlogPost post = gson.fromJson(gson.toJson(notification.getNotificationActor()), PlacementBlogPost.class);
            Fragment fragment;
            if (post.getLink().contains("training")) {
                fragment = (new TrainingBlogFragment()).withId(post.getId());
            } else {
                fragment = (new PlacementBlogFragment()).withId(post.getId());
            }
            Utils.updateFragment(fragment, fragmentActivity);
        }
    }

    @Override
    public int getAvatarPlaceholder(Notification notification) {
        return R.drawable.lotus_sq;
    }
}
