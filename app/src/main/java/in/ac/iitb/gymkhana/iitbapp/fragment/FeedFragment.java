package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.FeedAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.data.DatabaseContract;
import in.ac.iitb.gymkhana.iitbapp.data.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView feedRecyclerView;
    private SwipeRefreshLayout feedSwipeRefreshLayout;

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
//        Cursor cursor = getContext().getContentResolver().query(DatabaseContract.NewsFeedEntry.CONTENT_URI, null, null, null, null);
//        if (cursor.getCount() != 0) {
//            final List<Event> events = new ArrayList<>();
//            while (cursor.moveToNext()) {
//                Event event = new Event(cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_NAME)),
//                        cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_DESCRIPTION)),
//                        cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_IMAGE)),
//                        cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_CREATOR_NAME)),
//                        cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_CREATOR_ID)),
//                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_GOING_STATUS)));
//                events.add(event);
//            }
//            FeedAdapter feedAdapter = new FeedAdapter(events, new ItemClickListener() {
//                @Override
//                public void onItemClick(View v, int position) {
//                    String eventJson = new Gson().toJson(events.get(position));
//                    Bundle bundle = new Bundle();
//                    bundle.putString(Constants.EVENT_JSON, eventJson);
//                    EventFragment eventFragment = new EventFragment();
//                    eventFragment.setArguments(bundle);
//                    FragmentManager manager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction transaction = manager.beginTransaction();
//                    transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
//                    transaction.commit();
//                }
//            });
//            feedRecyclerView = (RecyclerView) getActivity().findViewById(R.id.feed_recycler_view);
//            feedRecyclerView.setAdapter(feedAdapter);
//            feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        }

        updateFeed();

        feedSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.feed_swipe_refresh_layout);
        feedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        });
    }

    private void updateFeed() {
        //TODO: Fetch userID from SharedPreferences
        String userID = "51e04db1-040f-406c-8b6f-0c47a1bdc5a4";
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getNewsFeed(userID).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    NewsFeedResponse newsFeedResponse = response.body();
                    final List<Event> events = newsFeedResponse.getEvents();

                    FeedAdapter feedAdapter = new FeedAdapter(events, new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            String eventJson = new Gson().toJson(events.get(position));
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.EVENT_JSON, eventJson);
                            EventFragment eventFragment = new EventFragment();
                            eventFragment.setArguments(bundle);
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                            transaction.commit();
                        }
                    });
                    feedRecyclerView = (RecyclerView) getActivity().findViewById(R.id.feed_recycler_view);
                    feedRecyclerView.setAdapter(feedAdapter);
                    feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    long itemsRemoved = getContext().getContentResolver().delete(DatabaseContract.NewsFeedEntry.CONTENT_URI, null, null);

                    Log.d("FeedFragment", itemsRemoved + " items removed.");
                    ContentValues contentValues[] = new ContentValues[events.size()];
                    for (int i = 0; i < events.size(); i++) {
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_NAME, events.get(i).getEventName());
                        contentValues1.put(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_DESCRIPTION, events.get(i).getEventDescription());
                        contentValues1.put(DatabaseContract.NewsFeedEntry.COLUMN_EVENT_IMAGE, events.get(i).getEventImageURL());
                        contentValues[i] = contentValues1;
                    }
                    int insertCount = getContext().getContentResolver().bulkInsert(DatabaseContract.NewsFeedEntry.CONTENT_URI, contentValues);
                    Log.d("FeedFragment", Integer.toString(insertCount) + " elements inserted");
                }
                //Server Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                //Network Error
                feedSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
