package com.mrane.campusmap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mrane.data.Marker;

import java.util.HashMap;

import app.insti.R;
import app.insti.fragment.MapFragment;

public class ListFragment extends Fragment {

    MapFragment mainActivity;
    FuzzySearchAdapter adapter;
    HashMap<String, Marker> data;
    View rootView;
    ListView list;

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = MapFragment.getMainActivity();
        adapter = mainActivity.getAdapter();
        rootView = inflater.inflate(R.layout.map_list_fragment, container, false);
        list = (ListView) rootView.findViewById(R.id.suggestion_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(mainActivity);
        list.setOnTouchListener(mainActivity);
        list.setFastScrollEnabled(true);
        return rootView;
    }

}
