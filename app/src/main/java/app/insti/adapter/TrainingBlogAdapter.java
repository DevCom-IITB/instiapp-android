package app.insti.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import app.insti.interfaces.ItemClickListener;
import app.insti.R;
import app.insti.data.TrainingBlogPost;
import app.insti.fragment.TrainingBlogFragment;
import app.insti.interfaces.Readable;
import app.insti.interfaces.Writable;
import ru.noties.markwon.Markwon;

public class TrainingBlogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Readable<TrainingBlogPost>,Writable<TrainingBlogPost> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<TrainingBlogPost> posts;
    private Context context;
    private ItemClickListener itemClickListener;

    public TrainingBlogAdapter(List<TrainingBlogPost> posts, ItemClickListener itemClickListener) {
        this.posts = posts;
        this.itemClickListener = itemClickListener;
    }

    public List<TrainingBlogPost> getPosts() {
        return posts;
    }

    public void setPosts(List<TrainingBlogPost> posts) {
        this.posts = posts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View postView = inflater.inflate(R.layout.blog_post_card, parent, false);

            final TrainingBlogAdapter.ViewHolder postViewHolder = new TrainingBlogAdapter.ViewHolder(postView);
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
            final TrainingBlogAdapter.ViewHolder postViewHolder = new TrainingBlogAdapter.ViewHolder(loadView);
            return new TrainingBlogAdapter.ProgressViewHolder(loadView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {
        if (recyclerHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) recyclerHolder;
            TrainingBlogPost post = posts.get(position);
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
    public int getItemViewType(int position) {
        return posts.size() > position ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return TrainingBlogFragment.showLoader ? (posts.size() + 1) : posts.size();
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
