package app.insti.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.TrainingBlogPost;
import app.insti.interfaces.ItemClickListener;

public class TrainingBlogAdapter extends RecyclerViewAdapter<TrainingBlogPost> {
    Context context;
    public TrainingBlogAdapter(List<TrainingBlogPost> posts, ItemClickListener itemClickListener) {
        super(posts, itemClickListener);
    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(@NonNull ViewGroup parent, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.blog_post_card, parent, false);

        this.context = context;
        final TrainingBlogAdapter.ViewHolder postViewHolder = new TrainingBlogAdapter.ViewHolder(postView);
        postView.setOnClickListener(v -> itemClickListener.onItemClick(v, postViewHolder.getAdapterPosition()));
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {
        if (recyclerHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) recyclerHolder;
            TrainingBlogPost post = getPosts().get(position);
            Utils.getMarkwon(false).setMarkdown(holder.postTitle, post.getTitle());

            Date publishedDate = post.getPublished();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(publishedDate);
            DateFormat displayFormat;
            if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                displayFormat = new SimpleDateFormat("EEE, MMM d, HH:mm", Locale.US);
            } else {
                displayFormat = new SimpleDateFormat("EEE, MMM d, ''yy, HH:mm", Locale.US);
            }
            Drawable image =context.getResources().getDrawable(R.drawable.ic_pin_9);

            holder.postTitle.setCompoundDrawablesWithIntrinsicBounds(  post.getPinned() ? image:null  , null,  null, null);
            holder.postPublished.setText(displayFormat.format(publishedDate));

            Utils.getMarkwon(false).setMarkdown(holder.postContent, post.getContent());
        } else {
            ((ProgressViewHolder) recyclerHolder).progressBar.setIndeterminate(true);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postTitle;
        private TextView postPublished;
        private TextView postContent;

        public ViewHolder(View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.post_title);
            postPublished = itemView.findViewById(R.id.post_published);
            postContent = itemView.findViewById(R.id.post_content);
            postContent.setMovementMethod(LinkMovementMethod.getInstance());

        }
    }
}
