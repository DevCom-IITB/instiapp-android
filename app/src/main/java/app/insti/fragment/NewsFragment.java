package app.insti.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.NewsAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.data.NewsArticle;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends RecyclerViewFragment<NewsArticle, NewsAdapter> {

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
        updateData();

        postType = NewsArticle.class;
        adapterType = NewsAdapter.class;
        recyclerView = getActivity().findViewById(R.id.news_recycler_view);
        swipeRefreshLayout = getActivity().findViewById(R.id.news_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
    }

    @Override
    Call<List<NewsArticle>> getCall(RetrofitInterface retrofitInterface, String sessionIDHeader) {
        return retrofitInterface.getNews(sessionIDHeader, 0, 20, searchQuery);
    }
}
