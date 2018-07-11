package app.insti.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.insti.ActivityBuffer;
import app.insti.Constants;
import app.insti.ItemClickListener;
import app.insti.R;
import app.insti.adapter.NewsAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.data.AppDatabase;
import app.insti.data.NewsArticle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends BaseFragment {

    private RecyclerView newsRecyclerView;
    private SwipeRefreshLayout newsSwipeRefreshLayout;
    private AppDatabase appDatabase;
    private boolean freshNewsDisplayed = false;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("News");

        appDatabase = AppDatabase.getAppDatabase(getContext());
        new NewsFragment.showNewsFromDB().execute();

        updateNews();

        newsSwipeRefreshLayout = getActivity().findViewById(R.id.news_swipe_refresh_layout);
        newsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateNews();
            }
        });
    }

    private void updateNews() {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getNews("sessionid=" + getArguments().getString(Constants.SESSION_ID)).enqueue(new Callback<List<NewsArticle>>() {
            @Override
            public void onResponse(Call<List<NewsArticle>> call, Response<List<NewsArticle>> response) {
                if (response.isSuccessful()) {
                    List<NewsArticle> articles = response.body();
                    freshNewsDisplayed = true;
                    displayNews(articles);

                    new updateDatabase().execute(articles);
                }
                //Server Error
                newsSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<NewsArticle>> call, Throwable t) {
                //Network Error
                newsSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void displayNews(final List<NewsArticle> result) {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;

        final NewsAdapter newsAdapter = new NewsAdapter(result, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                openWebURL(result.get(position).getLink());
            }
        });
        getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
            @Override
            public void run(Activity pActivity) {
                try {
                    newsRecyclerView = getActivity().findViewById(R.id.news_recycler_view);
                    newsRecyclerView.setAdapter(newsAdapter);
                    newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private void openWebURL(String URL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(browse);
    }

    private class updateDatabase extends AsyncTask<List<NewsArticle>, Void, Integer> {
        @Override
        protected Integer doInBackground(List<NewsArticle>... posts) {
            appDatabase.dbDao().deleteNewsArticles();
            appDatabase.dbDao().insertNewsArticles(posts[0]);
            return 1;
        }
    }

    private class showNewsFromDB extends AsyncTask<String, Void, List<NewsArticle>> {
        @Override
        protected List<NewsArticle> doInBackground(String... posts) {
            return appDatabase.dbDao().getAllNewsArticles();
        }

        protected void onPostExecute(List<NewsArticle> result) {
            if (!freshNewsDisplayed) {
                displayNews(result);
            }
        }
    }
}
