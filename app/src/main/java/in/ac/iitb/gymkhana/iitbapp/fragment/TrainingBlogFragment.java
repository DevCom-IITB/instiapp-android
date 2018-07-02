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
import in.ac.iitb.gymkhana.iitbapp.adapter.TrainingBlogAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.AppDatabase;
import in.ac.iitb.gymkhana.iitbapp.data.TrainingBlogPost;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingBlogFragment extends BaseFragment {

    private RecyclerView trainingFeedRecyclerView;
    private SwipeRefreshLayout feedSwipeRefreshLayout;
    private AppDatabase appDatabase;
    private boolean freshBlogDisplayed = false;


    public TrainingBlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_blog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        appDatabase = AppDatabase.getAppDatabase(getContext());
        new TrainingBlogFragment.showTrainingBlogFromDB().execute();

        updateTrainingFeed();

        feedSwipeRefreshLayout = getActivity().findViewById(R.id.training_feed_swipe_refresh_layout);
        feedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateTrainingFeed();
            }
        });
    }

    private void updateTrainingFeed() {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getTrainingBlogFeed("sessionid=" + getArguments().getString(Constants.SESSION_ID)).enqueue(new Callback<List<TrainingBlogPost>>() {
            @Override
            public void onResponse(Call<List<TrainingBlogPost>> call, Response<List<TrainingBlogPost>> response) {
                if (response.isSuccessful()) {
                    List<TrainingBlogPost> posts = response.body();
                    freshBlogDisplayed = true;
                    displayTrainingFeed(posts);

                    new updateDatabase().execute(posts);
                }
                //Server Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<TrainingBlogPost>> call, Throwable t) {
                //Network Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void displayTrainingFeed(final List<TrainingBlogPost> result) {
        /* Skip if we're already destroyed */
        if (getActivity() == null) return;

        final TrainingBlogAdapter trainingBlogAdapter = new TrainingBlogAdapter(result, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                openWebURL(result.get(position).getLink());
            }
        });
        getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
            @Override
            public void run(Activity pActivity) {
                try {
                    trainingFeedRecyclerView = getActivity().findViewById(R.id.training_feed_recycler_view);
                    trainingFeedRecyclerView.setAdapter(trainingBlogAdapter);
                    trainingFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    private class updateDatabase extends AsyncTask<List<TrainingBlogPost>, Void, Integer> {
        @Override
        protected Integer doInBackground(List<TrainingBlogPost>... posts) {
            appDatabase.dbDao().deleteTrainingBlogPosts();
            appDatabase.dbDao().insertTrainingBlogPosts(posts[0]);
            return 1;
        }
    }

    private class showTrainingBlogFromDB extends AsyncTask<String, Void, List<TrainingBlogPost>> {
        @Override
        protected List<TrainingBlogPost> doInBackground(String... posts) {
            return appDatabase.dbDao().getAllTrainingBlogPosts();
        }

        protected void onPostExecute(List<TrainingBlogPost> result) {
            if (!freshBlogDisplayed) {
                displayTrainingFeed(result);
            }
        }
    }
}
