package app.insti.fragment;


import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

import app.insti.ActivityBuffer;
import app.insti.Constants;
import app.insti.ItemClickListener;
import app.insti.R;
import app.insti.adapter.PlacementBlogAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.data.AppDatabase;
import app.insti.data.PlacementBlogPost;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlacementBlogFragment extends BaseFragment {

    private RecyclerView placementFeedRecyclerView;
    private SwipeRefreshLayout feedSwipeRefreshLayout;
    private AppDatabase appDatabase;
    private boolean freshBlogDisplayed = false;
    public static  boolean showLoader=true;


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

        appDatabase = AppDatabase.getAppDatabase(getContext());
        new PlacementBlogFragment.showPlacementBlogFromDB().execute();

        updatePlacementFeed();

        feedSwipeRefreshLayout = getActivity().findViewById(R.id.placement_feed_swipe_refresh_layout);
        feedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updatePlacementFeed();
            }
        });
    }

    private void updatePlacementFeed() {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getPlacementBlogFeed("sessionid=" + getArguments().getString(Constants.SESSION_ID),0,20).enqueue(new Callback<List<PlacementBlogPost>>() {
            @Override
            public void onResponse(Call<List<PlacementBlogPost>> call, Response<List<PlacementBlogPost>> response) {
                if (response.isSuccessful()) {
                    List<PlacementBlogPost> posts = response.body();
                    freshBlogDisplayed = true;
                    displayPlacementFeed(posts);

                    new updateDatabase().execute(posts);
                }
                //Server Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<PlacementBlogPost>> call, Throwable t) {
                //Network Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void displayPlacementFeed(final List<PlacementBlogPost> result) {
        /* Skip if we're already destroyed */
        if (getActivity() == null) return;

        final PlacementBlogAdapter placementBlogAdapter = new PlacementBlogAdapter(result, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                openWebURL(result.get(position).getLink());
            }
        });
        getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
            @Override
            public void run(Activity pActivity) {
                try {
                    placementFeedRecyclerView = getActivity().findViewById(R.id.placement_feed_recycler_view);
                    placementFeedRecyclerView.setAdapter(placementBlogAdapter);
                    placementFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    placementFeedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                        multiple calls should not be made
                        boolean loading=false;
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            if(dy>0){
                                LinearLayoutManager layoutManager= (LinearLayoutManager) placementFeedRecyclerView.getLayoutManager();
                                if(((layoutManager.getChildCount()+layoutManager.findFirstVisibleItemPosition())>(layoutManager.getItemCount()-5))&&(!loading)){
                                    loading=true;
                                    View v=getActivity().findViewById(R.id.placement_feed_swipe_refresh_layout);
                                    RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
                                    retrofitInterface.getPlacementBlogFeed("sessionid=" +getArguments().getString(Constants.SESSION_ID),layoutManager.getItemCount(),10).enqueue(new Callback<List<PlacementBlogPost>>() {
                                        @Override
                                        public void onResponse(Call<List<PlacementBlogPost>> call, Response<List<PlacementBlogPost>> response) {

                                            loading=false;
                                            List<PlacementBlogPost> blogPosts= (ArrayList<PlacementBlogPost>) placementBlogAdapter.getPosts();
                                            blogPosts.addAll(response.body());
                                            if(response.body().size()==0){
                                                showLoader=false;
                                            }
                                            placementBlogAdapter.setPosts(blogPosts);
                                            placementBlogAdapter.notifyDataSetChanged();
//                                            new updateDatabase().execute(blogPosts);
                                        }

                                        @Override
                                        public void onFailure(Call<List<PlacementBlogPost>> call, Throwable t) {
                                            loading=false;
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

    private class updateDatabase extends AsyncTask<List<PlacementBlogPost>, Void, Integer> {
        @Override
        protected Integer doInBackground(List<PlacementBlogPost>... posts) {
            appDatabase.dbDao().deletePlacementBlogPosts();
            appDatabase.dbDao().insertPlacementBlogPosts(posts[0]);
            return 1;
        }
    }

    private class showPlacementBlogFromDB extends AsyncTask<String, Void, List<PlacementBlogPost>> {
        @Override
        protected List<PlacementBlogPost> doInBackground(String... posts) {
            return appDatabase.dbDao().getAllPlacementBlogPosts();
        }

        protected void onPostExecute(List<PlacementBlogPost> result) {
            if (!freshBlogDisplayed) {
                displayPlacementFeed(result);
            }
        }
    }
}
