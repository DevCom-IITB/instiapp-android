package app.insti.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.insti.ActivityBuffer;
import app.insti.Constants;
import app.insti.ItemClickListener;
import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.NewsAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.NewsArticle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends BaseFragment {

    public static boolean showLoader = true;
    private RecyclerView newsRecyclerView;
    private SwipeRefreshLayout newsSwipeRefreshLayout;
    private boolean freshNewsDisplayed = false;
    private String searchQuery;

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

        setHasOptionsMenu(true);

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
        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.getNews("sessionid=" + getArguments().getString(Constants.SESSION_ID), 0, 20, searchQuery).enqueue(new Callback<List<NewsArticle>>() {
            @Override
            public void onResponse(Call<List<NewsArticle>> call, Response<List<NewsArticle>> response) {
                if (response.isSuccessful()) {
                    List<NewsArticle> articles = response.body();
                    freshNewsDisplayed = true;
                    displayNews(articles);
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
                String link = result.get(position).getLink();
                if (link != null && !link.isEmpty())
                    openWebURL(link);
            }
        });
        getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
            @Override
            public void run(Activity pActivity) {
                try {
                    newsRecyclerView = getActivity().findViewById(R.id.news_recycler_view);
                    newsRecyclerView.setAdapter(newsAdapter);
                    newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    newsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        //                        multiple calls should not be made
                        boolean loading = false;

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            if (dy > 0) {
                                LinearLayoutManager layoutManager = (LinearLayoutManager) newsRecyclerView.getLayoutManager();
                                if (((layoutManager.getChildCount() + layoutManager.findFirstVisibleItemPosition()) > (layoutManager.getItemCount() - 5)) && (!loading)) {
                                    loading = true;
                                    View v = getActivity().findViewById(R.id.training_feed_swipe_refresh_layout);
                                    RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
                                    retrofitInterface.getNews("sessionid=" + getArguments().getString(Constants.SESSION_ID), layoutManager.getItemCount(), 10, searchQuery).enqueue(new Callback<List<NewsArticle>>() {
                                        @Override
                                        public void onResponse(Call<List<NewsArticle>> call, Response<List<NewsArticle>> response) {

                                            loading = false;
                                            List<NewsArticle> newsArticles = (ArrayList<NewsArticle>) newsAdapter.getNewsArticles();
                                            newsArticles.addAll(response.body());
                                            if (response.body().size() == 0) {
                                                showLoader = false;
                                            }
                                            newsAdapter.setNewsArticles(newsArticles);
                                            newsAdapter.notifyDataSetChanged();
//                                            new updateDatabase().execute(blogPosts);
                                        }

                                        @Override
                                        public void onFailure(Call<List<NewsArticle>> call, Throwable t) {
                                            loading = false;
                                        }
                                    });
                                }

                            }
                        }
                    });
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_view_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    //Text is cleared, do your thing
                    searchQuery = null;
                    updateNews();
                    showLoader = true;
                    return true;
                } else if (newText.length() >= 3) {
                    performSearch(newText);
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch(String query) {
        searchQuery = query;
        updateNews();
        showLoader = false;
    }
}
