package com.mrane.campusmap;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import app.insti.R;
import app.insti.fragment.MapFragment;

public class ListFragment extends Fragment {

    private MapFragment mapFragment = null;

    public ListFragment() {
    }

    public ListFragment forFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FuzzySearchAdapter adapter = mapFragment.getAdapter();
        View rootView = inflater.inflate(R.layout.map_list_fragment, container, false);
        ListView list = rootView.findViewById(R.id.suggestion_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(mapFragment);
        list.setOnTouchListener(mapFragment);
        list.setFastScrollEnabled(true);
        return rootView;
    }

}
