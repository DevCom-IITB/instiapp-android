package app.insti.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.insti.Constants;
import app.insti.interfaces.ItemClickListener;
import app.insti.R;
import app.insti.api.model.Event;
import app.insti.api.model.NewsArticle;
import app.insti.api.model.Notification;
import app.insti.api.model.PlacementBlogPost;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.Viewholder> {
    private List<Notification> notifications;
    private Context context;
    private ItemClickListener itemClickListener;

    public NotificationsAdapter(List<Notification> notifications, ItemClickListener itemClickListener) {
        this.notifications = notifications;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationView = inflater.inflate(R.layout.feed_card, viewGroup, false);

        final Viewholder notificationsViewHolder = new Viewholder(notificationView);
        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, notificationsViewHolder.getAdapterPosition());
            }
        });
        return notificationsViewHolder;
    }

    @Override
    public void onBindViewHolder(Viewholder viewholder, int i) {
        Gson gson = new Gson();
        Notification appNotification = notifications.get(i);
        viewholder.notificationVerb.setText(appNotification.getNotificationVerb());
        if (appNotification.getNotificationActorType().contains("event")) {
            Event event = gson.fromJson(gson.toJson(appNotification.getNotificationActor()), Event.class);
            Picasso.get().load(
                    Constants.resizeImageUrl(event.getEventImageURL(), 200)
            ).into(viewholder.notificationPicture);
            viewholder.notificationTitle.setText(event.getEventName());
        } else if (appNotification.getNotificationActorType().contains("newsentry")) {
            NewsArticle article = gson.fromJson(gson.toJson(appNotification.getNotificationActor()), NewsArticle.class);
            Picasso.get().load(
                    Constants.resizeImageUrl(article.getBody().getBodyImageURL(), 200)
            ).into(viewholder.notificationPicture);
            viewholder.notificationTitle.setText(article.getTitle());
        } else if (appNotification.getNotificationActorType().contains("blogentry")) {
            PlacementBlogPost post = gson.fromJson(gson.toJson(appNotification.getNotificationActor()), PlacementBlogPost.class);
            Picasso.get().load(R.drawable.lotus_sq).into(viewholder.notificationPicture);
            viewholder.notificationTitle.setText(post.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView notificationTitle;
        private ImageView notificationPicture;
        private TextView notificationVerb;

        public Viewholder(View itemView) {
            super(itemView);

            notificationPicture = (ImageView) itemView.findViewById(R.id.object_picture);
            notificationTitle = (TextView) itemView.findViewById(R.id.object_title);
            notificationVerb = (TextView) itemView.findViewById(R.id.object_subtitle);
        }
    }
}
