package com.mrane.campusmap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.mrane.data.Marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import app.insti.R;
import app.insti.fragment.MapFragment;

public class IndexFragment extends Fragment implements OnGroupExpandListener,
		OnGroupCollapseListener, OnGroupClickListener {

	MapFragment mainActivity;
	ExpandableListAdapter adapter;
	HashMap<String, Marker> data;
	View rootView;
	ExpandableListView list;
	List<String> headers = new ArrayList<String>();
	HashMap<String, List<String>> childData = new HashMap<String, List<String>>();
	int pos;
	int prevGroup = -1;

	public IndexFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainActivity = MapFragment.getMainActivity();
		data = mainActivity.data;
		if (headers.isEmpty()) {
			setHeaderAndChildData();
		}
		adapter = new ExpandableListAdapter(mainActivity.getContext(), headers, childData);
		rootView = inflater.inflate(R.layout.map_index_fragment, container, false);
		list = (ExpandableListView) rootView.findViewById(R.id.index_list);
		mainActivity.setExpAdapter(adapter);
		list.setAdapter(adapter);
		list.setOnChildClickListener(mainActivity);
		list.setOnGroupExpandListener(this);
		list.setOnGroupCollapseListener(this);
		list.setOnGroupClickListener(this);

		return rootView;
	}

	@Override
	  public void onResume() {
		String name = mainActivity.editText.getText().toString();
		if(name.isEmpty()) {
			collapseAllGroups();
			list.setSelectedGroup(0);
		} else {
			if(data.containsKey(name)) {
				String groupName = data.get(name).getGroupName();
				int groupId = getGroupId(groupName);
				if(groupId != -1) {
					list.expandGroup(groupId);
					list.setSelectedGroup(groupId);
				}
			}
		}
	     super.onResume();
	  }
	
	private int getGroupId(String groupName) {
		int groupCount = adapter.getGroupCount();
		int temp = -1;
		for (int i=0; i < groupCount; i++) {
			if(adapter.getGroup(i).equals(groupName)) {
				temp = i;
			}
		}
		return temp;
	}

	private void collapseAllGroups() {
		int groupCount = adapter.getGroupCount();
		for (int i=0; i < groupCount; i++) {
			list.collapseGroup(i);
		}
		
	}

	private void setChildData() {
		Collection<Marker> keys = data.values();
		for (Marker key : keys) {
			List<String> child = childData.get(key.getGroupName());
			child.add(key.getName());
		}
		sortChildData();
	}

	private void sortChildData() {
		for (String header : headers) {
			List<String> child = childData.get(header);
			Collections.sort(child);
		}

	}

	private void setHeaderAndChildData() {
		String[] headerString = Marker.getGroupNames();
		Collections.addAll(headers, headerString);
		for (String header : headers) {
			childData.put(header, new ArrayList<String>());
		}
		setChildData();
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		if (prevGroup != -1 && prevGroup != groupPosition) {
			list.collapseGroup(prevGroup);
		}
		prevGroup = groupPosition;
	}

	@Override
	public void onGroupCollapse(int groupPosition) {
		if (prevGroup != -1) {
			//list.setSelectionFromTop(prevGroup, 0);
		}
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
	        long id) {
	    // Implement this method to scroll to the correct position as this doesn't
	    // happen automatically if we override onGroupExpand() as above
	    parent.smoothScrollToPosition(groupPosition);

	    // Need default behaviour here otherwise group does not get expanded/collapsed
	    // on click
	    if (parent.isGroupExpanded(groupPosition)) {
	        parent.collapseGroup(groupPosition);
	    } else {
	        parent.expandGroup(groupPosition);
	    }

	    return true;
	}

}
