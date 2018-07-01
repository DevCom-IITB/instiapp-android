package in.ac.iitb.gymkhana.iitbapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.data.PlacementBlogPost;
import ru.noties.markwon.Markwon;

public class PlacementBlogAdapter extends RecyclerView.Adapter<PlacementBlogAdapter.ViewHolder> {

    private List<PlacementBlogPost> posts;
    private Context context;
    private ItemClickListener itemClickListener;

    public PlacementBlogAdapter(List<PlacementBlogPost> posts, ItemClickListener itemClickListener) {
        this.posts = posts;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.blog_post_card, parent, false);

        final PlacementBlogAdapter.ViewHolder postViewHolder = new PlacementBlogAdapter.ViewHolder(postView);
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, postViewHolder.getAdapterPosition());
            }
        });
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlacementBlogPost post = posts.get(position);
        Markwon.setMarkdown(holder.postTitle, post.getTitle());

        Date publishedDate = post.getPublished();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(publishedDate);
        DateFormat displayFormat;
        if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            displayFormat = new SimpleDateFormat("EEE, MMM d, HH:mm", Locale.US);
        } else {
            displayFormat = new SimpleDateFormat("EEE, MMM d, ''yy, HH:mm", Locale.US);
        }
        holder.postPublished.setText(displayFormat.format(publishedDate));

        Markwon.setMarkdown(holder.postContent, post.getContent());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postTitle;
        private TextView postPublished;
        private TextView postContent;

        public ViewHolder(View itemView) {
            super(itemView);

            postTitle = (TextView) itemView.findViewById(R.id.post_title);
            postPublished = (TextView) itemView.findViewById(R.id.post_published);
            postContent = (TextView) itemView.findViewById(R.id.post_content);
        }
    }
}
