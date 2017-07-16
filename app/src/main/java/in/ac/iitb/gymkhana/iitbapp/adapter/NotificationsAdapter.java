package in.ac.iitb.gymkhana.iitbapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.api.model.AppNotification;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.Viewholder> {
    private List<AppNotification> notifications;
    private Context context;
    private ItemClickListener itemClickListener;

    public NotificationsAdapter(List<AppNotification> notifications, ItemClickListener itemClickListener) {
        this.notifications = notifications;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationView = inflater.inflate(R.layout.notification, viewGroup, false);

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
        AppNotification appNotification = notifications.get(i);
        viewholder.notificationTitle.setText(appNotification.getNotificationName());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView notificationTitle;

        public Viewholder(View itemView) {
            super(itemView);

            notificationTitle = (TextView) itemView.findViewById(R.id.notification_title);
        }
    }
}
