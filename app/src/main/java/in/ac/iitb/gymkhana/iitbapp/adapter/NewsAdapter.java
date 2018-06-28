package in.ac.iitb.gymkhana.iitbapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.data.NewsArticle;
import ru.noties.markwon.Markwon;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsArticle> newsArticles;
    private Context context;
    private ItemClickListener itemClickListener;

    public NewsAdapter(List<NewsArticle> newsArticles, ItemClickListener itemClickListener) {
        this.newsArticles = newsArticles;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View newsArticleView = inflater.inflate(R.layout.news_article_card, parent, false);

        final NewsAdapter.ViewHolder articleViewHolder = new NewsAdapter.ViewHolder(newsArticleView);
        newsArticleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, articleViewHolder.getAdapterPosition());
            }
        });
        return articleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return newsArticles.size();
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
