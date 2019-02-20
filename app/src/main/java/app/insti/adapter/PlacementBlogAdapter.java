package app.insti.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.insti.R;
import app.insti.api.model.PlacementBlogPost;
import app.insti.fragment.PlacementBlogFragment;
import app.insti.interfaces.ItemClickListener;
import app.insti.interfaces.Readable;
import app.insti.interfaces.Writable;
import ru.noties.markwon.Markwon;

public class PlacementBlogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Readable<PlacementBlogPost>,Writable<PlacementBlogPost> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<PlacementBlogPost> posts;
    private ItemClickListener itemClickListener;
    public PlacementBlogAdapter(List<PlacementBlogPost> posts, ItemClickListener itemClickListener) {
        this.posts = posts;
        this.itemClickListener = itemClickListener;
        this.setHasStableIds(true);
    }

    public List<PlacementBlogPost> getPosts() {
        return posts;
    }

    public void setPosts(List<PlacementBlogPost> posts) {
        this.posts = posts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        if (viewType == VIEW_ITEM) {
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
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            View loadView = inflater.inflate(R.layout.blog_load_item, parent, false);
            return new PlacementBlogAdapter.ProgressViewHolder(loadView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return posts.size() > position ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return PlacementBlogFragment.showLoader ? (posts.size() + 1) : posts.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {
        if (recyclerHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) recyclerHolder;
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
        } else {
            ((ProgressViewHolder) recyclerHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position < posts.size()) {
            return posts.get(position).getPostID().hashCode();
        }
        return -1;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.blog_load_item);
        }
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
