package in.ac.iitb.gymkhana.iitbapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.data.Event;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<Event> posts;
    private Context context;
    private ItemClickListener itemClickListener;

    public FeedAdapter(List<Event> posts, ItemClickListener itemClickListener) {
        this.posts = posts;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.feed_card, viewGroup, false);

        final ViewHolder postViewHolder = new ViewHolder(postView);
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, postViewHolder.getAdapterPosition());
            }
        });
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Event currentEvent = posts.get(i);
        viewHolder.eventTitle.setText(currentEvent.getEventName());
//        viewHolder.eventDetails.setText(currentEvent.getEventDescription());
        Timestamp timestamp = currentEvent.getEventStartTime();
        Date Date = new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM");
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm a");

        viewHolder.eventDate.setText(simpleDateFormatDate.format(Date));
        viewHolder.eventTime.setText(simpleDateFormatTime.format(Date));
        viewHolder.eventVenue.setText(currentEvent.getEventVenues().get(0).getVenueName());

        Picasso.with(context).load(currentEvent.getEventImageURL()).into(viewHolder.eventPicture);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView eventPicture;
        private TextView eventTitle;
//        private TextView eventDetails;
        private TextView eventDate;
        private TextView eventTime;
        private TextView eventVenue;
        private ImageView eventEnthu;

        public ViewHolder(View itemView) {
            super(itemView);

            eventPicture = (ImageView) itemView.findViewById(R.id.event_picture);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
//            eventDetails = (TextView) itemView.findViewById(R.id.event_details);
            eventDate = (TextView) itemView.findViewById(R.id.event_date);
            eventTime = (TextView) itemView.findViewById(R.id.event_time);
            eventVenue = (TextView) itemView.findViewById(R.id.event_venue);
        }
    }
}
