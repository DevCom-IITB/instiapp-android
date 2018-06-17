package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.ActivityBuffer;
import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.PlacementBlogAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.AppDatabase;
import in.ac.iitb.gymkhana.iitbapp.data.PlacementBlogPost;
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
        retrofitInterface.getPlacementBlogFeed("sessionid=" + getArguments().getString(Constants.SESSION_ID)).enqueue(new Callback<List<PlacementBlogPost>>() {
            @Override
            public void onResponse(Call<List<PlacementBlogPost>> call, Response<List<PlacementBlogPost>> response) {
                if (response.isSuccessful()) {
                    List<PlacementBlogPost> posts = response.body();
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
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
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
            displayPlacementFeed(result);
        }
    }
}
