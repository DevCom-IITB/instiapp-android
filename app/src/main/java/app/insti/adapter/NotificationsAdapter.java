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
import app.insti.api.model.NewsArticle;
import app.insti.api.model.Notification;
import app.insti.api.model.PlacementBlogPost;
import app.insti.fragment.NewsFragment;
import app.insti.fragment.PlacementBlogFragment;
import app.insti.fragment.TrainingBlogFragment;

public class NotificationsAdapter extends CardAdapter<Notification> {

    private final String TYPE_EVENT = "event";
    private final String TYPE_NEWSENTRY = "newsentry";
    private final String TYPE_BLOG = "blogentry";

    private Gson gson;

    public NotificationsAdapter(List<Notification> notifications, Fragment fragment) {
        super(notifications, fragment);
        gson = new Gson();
    }

    @Override
    public void onClick(Notification notification, FragmentActivity fragmentActivity) {
        /* Mark notification read */
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        String sessId = Utils.getSessionIDHeader();
        retrofitInterface.markNotificationRead(sessId, notification.getNotificationId().toString()).enqueue(new EmptyCallback<Void>());

        /* Open event */
        if (isEvent(notification)) {
            Gson gson = new Gson();
            Event event = gson.fromJson(gson.toJson(notification.getNotificationActor()), Event.class) ;
            Utils.openEventFragment(event, fragmentActivity);
        } else if (isNews(notification)) {
            NewsFragment newsFragment = new NewsFragment();
            Utils.updateFragment(newsFragment, fragmentActivity);
        } else if (isBlogPost(notification)) {
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
    public String getTitle(Notification notification) {
        if (isEvent(notification)) {
            return getEvent(notification).getEventName();
        } else if (isNews(notification)) {
            return getNews(notification).getTitle();
        } else if (isBlogPost(notification)) {
            return getBlogPost(notification).getTitle();
        }
        return "Notification";
    }

    @Override
    public String getSubtitle(Notification notification) {
        return notification.getNotificationVerb();
    }

    @Override
    public String getAvatarUrl(Notification notification) {
        if (isEvent(notification)) {
            return getEvent(notification).getEventImageURL();
        } else if (isNews(notification)) {
            return getNews(notification).getBody().getBodyImageURL();
        }
        return null;
    }

    @Override
    public int getAvatarPlaceholder() {
        return R.drawable.lotus_sq;
    }

    private boolean isEvent(Notification notification) {
        return notification.getNotificationActorType().contains(TYPE_EVENT);
    }

    private boolean isNews(Notification notification) {
        return notification.getNotificationActorType().contains(TYPE_NEWSENTRY);
    }

    private boolean isBlogPost(Notification notification) {
        return notification.getNotificationActorType().contains(TYPE_BLOG);
    }

    private Event getEvent(Notification notification) {
        return gson.fromJson(gson.toJson(notification.getNotificationActor()), Event.class);
    }

    private NewsArticle getNews(Notification notification) {
        return gson.fromJson(gson.toJson(notification.getNotificationActor()), NewsArticle.class);
    }

    private PlacementBlogPost getBlogPost(Notification notification) {
        return gson.fromJson(gson.toJson(notification.getNotificationActor()), PlacementBlogPost.class);
    }
}
