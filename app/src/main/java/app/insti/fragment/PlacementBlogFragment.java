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
import app.insti.Utils;
import app.insti.adapter.PlacementBlogAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.PlacementBlogPost;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlacementBlogFragment extends RecyclerViewFragment<PlacementBlogPost, PlacementBlogAdapter> {

    public PlacementBlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_placement_blog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Placement Blog");
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_placement_blog);

        setHasOptionsMenu(true);
        updateData();

        postType = PlacementBlogPost.class;
        adapterType = PlacementBlogAdapter.class;
        recyclerView = getActivity().findViewById(R.id.placement_feed_recycler_view);
        swipeRefreshLayout = getActivity().findViewById(R.id.placement_feed_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
    }

    @Override
    protected Call<List<PlacementBlogPost>> getCall(RetrofitInterface retrofitInterface, String sessionIDHeader, int postCount) {
        return retrofitInterface.getPlacementBlogFeed(sessionIDHeader, postCount, 20, searchQuery);
    }
}
