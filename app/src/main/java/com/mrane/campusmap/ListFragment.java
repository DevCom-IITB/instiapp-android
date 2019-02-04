package com.mrane.campusmap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import app.insti.R;
import app.insti.fragment.MapFragment;

public class ListFragment extends Fragment {

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MapFragment mainActivity = MapFragment.getMainActivity();
        final FuzzySearchAdapter adapter = mainActivity.getAdapter();
        View rootView = inflater.inflate(R.layout.map_list_fragment, container, false);
        ListView list = rootView.findViewById(R.id.suggestion_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(mainActivity);
        list.setOnTouchListener(mainActivity);
        list.setFastScrollEnabled(true);
        return rootView;
    }

}
