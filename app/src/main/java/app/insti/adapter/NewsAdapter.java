package app.insti.adapter;

import android.content.Context;
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
import app.insti.api.model.NewsArticle;
import app.insti.interfaces.ItemClickListener;

public class NewsAdapter extends RecyclerViewAdapter<NewsArticle> {

    public NewsAdapter(List<NewsArticle> posts, ItemClickListener itemClickListener) {
        super(posts, itemClickListener);
    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(@NonNull ViewGroup parent, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.news_article_card, parent, false);

        final NewsAdapter.ViewHolder postViewHolder = new NewsAdapter.ViewHolder(postView);
        View.OnClickListener clickListener = v -> itemClickListener.onItemClick(v, postViewHolder.getAdapterPosition());
        postView.setOnClickListener(clickListener);
        postViewHolder.articleContent.setOnClickListener(clickListener);

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder recyclerHolder, int position) {
        if (recyclerHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) recyclerHolder;
            NewsArticle article = getPosts().get(position);
            Utils.getMarkwon().setMarkdown(holder.articleTitle, article.getTitle());
            holder.articleBody.setText(article.getBody().getBodyName());

            Date publishedDate = article.getPublished();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(publishedDate);
            DateFormat displayFormat;
            if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                displayFormat = new SimpleDateFormat("EEE, MMM d, HH:mm", Locale.US);
            } else {
                displayFormat = new SimpleDateFormat("EEE, MMM d, ''yy, HH:mm", Locale.US);
            }
            holder.articlePublished.setText(displayFormat.format(publishedDate));

            Utils.getMarkwon().setMarkdown(holder.articleContent, article.getContent());
        } else {
            ((ProgressViewHolder) recyclerHolder).progressBar.setIndeterminate(true);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView articleTitle;
        private TextView articleBody;
        private TextView articlePublished;
        private TextView articleContent;

        public ViewHolder(View itemView) {
            super(itemView);

            articleTitle = itemView.findViewById(R.id.article_title);
            articleBody = itemView.findViewById(R.id.article_body);
            articlePublished = itemView.findViewById(R.id.article_published);
            articleContent = itemView.findViewById(R.id.article_content);
        }
    }
}
