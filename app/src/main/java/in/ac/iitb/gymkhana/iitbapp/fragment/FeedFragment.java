package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.FeedAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.Event;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    RecyclerView feedRecyclerView;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        NewsFeedRequest newsFeedRequest = new NewsFeedRequest(NewsFeedRequest.FOLLOWED, 0, 20);
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getNewsFeed(newsFeedRequest).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    NewsFeedResponse newsFeedResponse = response.body();
                    List<Event> events = newsFeedResponse.getEvents();

                    FeedAdapter feedAdapter = new FeedAdapter(events, new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            // TODO: Launch EventFragment
                        }
                    });
                    feedRecyclerView = (RecyclerView) getActivity().findViewById(R.id.feed_recycler_view);
                    feedRecyclerView.setAdapter(feedAdapter);
                    feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
                //Server Error
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                //Network Error
            }
        });
    }
}
