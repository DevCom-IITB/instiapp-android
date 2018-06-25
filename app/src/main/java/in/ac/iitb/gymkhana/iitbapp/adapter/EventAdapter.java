package in.ac.iitb.gymkhana.iitbapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.data.Event;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

    private List<Event> eventList;
    private ItemClickListener itemClickListener;
    private Context context;
    public EventAdapter(List<Event> eventList, ItemClickListener itemClickListener){
        this.eventList = eventList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_event_card, parent,false);
        final ViewHolder postViewHolder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(view,postViewHolder.getAdapterPosition());
            }
        });

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Event event = eventList.get(position);
        holder.name.setText(event.getEventName());
        holder.description.setText(event.getEventStartTime().toString());
        Picasso.with(context).load(event.getEventImageURL()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return  eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public TextView description;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.event_card_name);
            description = (TextView) itemView.findViewById(R.id.event_card_description);
            image = (ImageView) itemView.findViewById(R.id.event_card_avatar);
        }



    }
}
