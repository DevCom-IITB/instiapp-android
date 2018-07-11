package com.mrane.campusmap;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrane.data.Marker;

import java.util.HashMap;
import java.util.List;

import app.insti.R;
import app.insti.fragment.MapFragment;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader;
	private HashMap<String, List<String>> _listDataChild;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<String>> listChildData) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		final String childText = (String) getChild(groupPosition, childPosition);
		
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.map_list_item, null);
		}

		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.lblListItem);
		View itemGroupColor = (View) convertView.findViewById(R.id.item_group_color);
		int color = Marker.getColor(Marker.getGroupId(headerTitle));
		itemGroupColor.setBackgroundColor(color);
		Typeface regular = Typeface.createFromAsset(_context.getAssets(), MapFragment.FONT_REGULAR);
		txtListChild.setTypeface(regular);
		txtListChild.setText(childText);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.map_expandable_list_header, null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);
		Typeface regular = Typeface.createFromAsset(_context.getAssets(), MapFragment.FONT_REGULAR);
		lblListHeader.setTypeface(regular);
		lblListHeader.setText(headerTitle);
		ImageView iconExpand = (ImageView) convertView
				.findViewById(R.id.icon_expand);
		
		ImageView groupColor = (ImageView) convertView.findViewById(R.id.group_color);
		int color = Marker.getColor(Marker.getGroupId(headerTitle));
		groupColor.setImageDrawable(new ColorDrawable(color));

		if (isExpanded) {
			iconExpand.setImageResource(R.drawable.ic_action_expand);
		} else {
			iconExpand.setImageResource(R.drawable.ic_action_next_item);
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
