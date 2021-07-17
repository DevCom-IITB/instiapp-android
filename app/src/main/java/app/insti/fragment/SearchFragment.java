package app.insti.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.fragment.NewSearchFragment;
import app.insti.adapter.SearchAdapter;
import app.insti.adapter.TrainingBlogAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.SearchDataPost;
import app.insti.api.model.TrainingBlogPost;
import retrofit2.Call;
import app.insti.fragment.SearchRecyclerViewFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends SearchRecyclerViewFragment<SearchDataPost, SearchAdapter> {

    private FloatingActionButton fab;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();


        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Search FAQs");
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_search);

        setHasOptionsMenu(true);

        postType = SearchDataPost.class;
        adapterType = SearchAdapter.class;
        recyclerView = getActivity().findViewById(R.id.search_feed_recycler_view);
        swipeRefreshLayout = getActivity().findViewById(R.id.search_feed_swipe_refresh_layout);
        fab = getView().findViewById(R.id.fab_search);
        initFab();
        updateData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFab();
                updateData();
            }
        });
    }

    @Override
    protected Call<List<SearchDataPost>> getCall(RetrofitInterface retrofitInterface, String sessionIDHeader, int postCount) {
        return retrofitInterface.getSearchFeed(sessionIDHeader, postCount, 20, searchQuery);
    }

    private void initFab() {
//        if (((MainActivity) getActivity()).createEventAccess()) {
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewSearchFragment newSearchFragment = new NewSearchFragment();
                    ((MainActivity) getActivity()).updateFragment(newSearchFragment);
                }
            });
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) fab.hide();
                    else if (dy < 0) fab.show();
                }
            });
//        }
    }

}