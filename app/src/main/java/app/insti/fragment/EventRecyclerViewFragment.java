package app.insti.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.adapter.FeedAdapter;
import app.insti.api.model.Event;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link EventRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventRecyclerViewFragment extends Fragment implements TransitionTargetFragment, TransitionTargetChild {
    private static final String TAG = "EventRecyclerViewFragment";
    public Fragment parentFragment = null;

    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;

    private List<Event> eventList;

    public EventRecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void transitionEnd() {
        if (parentFragment instanceof TransitionTargetFragment) {
            ((TransitionTargetFragment) parentFragment).transitionEnd();
        }
    }

    @Override
    public Fragment getParent() {
        return parentFragment;
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
        feedAdapter = new FeedAdapter(eventList, this);
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