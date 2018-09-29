package app.insti.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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
import app.insti.interfaces.Readable;
import app.insti.interfaces.Writable;
import app.insti.data.NewsArticle;
import app.insti.fragment.NewsFragment;
import ru.noties.markwon.Markwon;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Readable<NewsArticle>,Writable<NewsArticle> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<NewsArticle> newsArticles;
    private Context context;
    private ItemClickListener itemClickListener;

    public NewsAdapter(List<NewsArticle> newsArticles, ItemClickListener itemClickListener) {
        this.newsArticles = newsArticles;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public List<NewsArticle> getPosts() {
        return newsArticles;
    }

    @Override
    public void setPosts(List<NewsArticle> posts) {
        this.newsArticles = posts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View postView = inflater.inflate(R.layout.news_article_card, parent, false);

            final NewsAdapter.ViewHolder postViewHolder = new NewsAdapter.ViewHolder(postView);
            postView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, postViewHolder.getAdapterPosition());
                }
            });
            postViewHolder.articleContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, postViewHolder.getAdapterPosition());
                }
            });

            return postViewHolder;
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            View loadView = inflater.inflate(R.layout.blog_load_item, parent, false);
            final NewsAdapter.ViewHolder postViewHolder = new NewsAdapter.ViewHolder(loadView);
            return new NewsAdapter.ProgressViewHolder(loadView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder recyclerHolder, int position) {
        if (recyclerHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) recyclerHolder;
            NewsArticle article = newsArticles.get(position);
            Markwon.setMarkdown(holder.articleTitle, article.getTitle());
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

            Markwon.setMarkdown(holder.articleContent, article.getContent());
        } else {
            ((ProgressViewHolder) recyclerHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return newsArticles.size() > position ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return NewsFragment.showLoader ? (newsArticles.size() + 1) : newsArticles.size();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.blog_load_item);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView articleTitle;
        private TextView articleBody;
        private TextView articlePublished;
        private TextView articleContent;

        public ViewHolder(View itemView) {
            super(itemView);

            articleTitle = (TextView) itemView.findViewById(R.id.article_title);
            articleBody = (TextView) itemView.findViewById(R.id.article_body);
            articlePublished = (TextView) itemView.findViewById(R.id.article_published);
            articleContent = (TextView) itemView.findViewById(R.id.article_content);
        }
    }
}
