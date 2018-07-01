package in.ac.iitb.gymkhana.iitbapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.FeedAdapter;
import in.ac.iitb.gymkhana.iitbapp.data.Event;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link EventRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventRecyclerViewFragment extends Fragment {
    private static final String TAG = "EventRecyclerViewFragment";


    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;

    private List<Event> eventList;

    public EventRecyclerViewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventRecyclerViewFragment newInstance(List<Event> eventList) {
        EventRecyclerViewFragment fragment = new EventRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EVENT_LIST_JSON, new Gson().toJson(eventList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventList = new Gson().fromJson(getArguments().getString(Constants.EVENT_LIST_JSON), new TypeToken<List<Event>>() {
            }.getType());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.event_recycler_view);
        feedAdapter = new FeedAdapter(eventList, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Event event = eventList.get(position);
                EventFragment eventFragment = new EventFragment();
                Bundle arguments = getArguments();
                arguments.putString(Constants.EVENT_JSON, new Gson().toJson(event));
                eventFragment.setArguments(getArguments());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                ft.addToBackStack(eventFragment.getTag());
                ft.commit();
            }
        });
        recyclerView.setAdapter(feedAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_recycler_view, container, false);
    }


}