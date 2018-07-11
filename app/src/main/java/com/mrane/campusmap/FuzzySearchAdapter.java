package com.mrane.campusmap;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mrane.data.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import app.insti.R;
import app.insti.fragment.MapFragment;

public class FuzzySearchAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	private List<Marker> resultlist = null;
	private ArrayList<Marker> inputlist;
	private List<ScoredMarker> map;
	private Locale l;
	private String searchedText = "";
	private SettingsManager settingsManager;
	private boolean turnOnResidences;

	public FuzzySearchAdapter(Context context, List<Marker> inputlist) {
		mContext = context;
		l = Locale.getDefault();
		this.resultlist = inputlist;
		Collections.sort(resultlist, new MarkerNameComparator());
		inflater = LayoutInflater.from(mContext);
		this.inputlist = new ArrayList<Marker>();
		this.inputlist.addAll(resultlist);
		map = new ArrayList<ScoredMarker>();
	}

	public class ViewHolder {
		TextView label;
		LinearLayout rowContainer;
	}

	public class ScoredMarker {
		Marker m;
		int score;

		public ScoredMarker(int score, Marker m) {
			this.m = m;
			this.score = score;
		}
	}

	public int getResultSize() {
		return resultlist.size();
	}

	@Override
	public int getCount() {
		if (this.getResultSize() == 0) {
			return 1;
		}
		return resultlist.size();
	}

	@Override
	public Marker getItem(int position) {
		return resultlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.map_row_layout, null);

			holder.label = (TextView) view.findViewById(R.id.label);
			Typeface regular = Typeface.createFromAsset(mContext.getAssets(),
					MapFragment.FONT_REGULAR);
			holder.label.setTypeface(regular);
			holder.rowContainer = (LinearLayout) view
					.findViewById(R.id.row_container);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// Set the results into TextViews

		if (this.getResultSize() == 0) {
			if (settingsManager.showResidences()) {
				holder.label.setText("Sorry, no results found.");
			} else {
				if (turnOnResidences) {
					holder.label
							.setText("There are results in residences. Select show residences from settings.");
				} else {
					holder.label.setText("Sorry, no results found.");
				}
			}
		} else {
			holder.label.setText(getSpannedText(resultlist.get(position)
					.getName(), searchedText));
		}
		return view;
	}

	private Spanned getSpannedText(String name, String searchedText2) {
		String smallLetterName = name.toLowerCase(l);
		searchedText2 = searchedText2.toLowerCase(l).replaceAll("\\s", "");
		if (searchedText2.equals("")) {
			return Html.fromHtml(name);
		} else {
			String htmlName = "";
			if (isShortForm(smallLetterName, searchedText2)) {
				htmlName = getHighlightedShortform(name, searchedText2);
			} else {
				int i = 0;
				int j = 0;
				while (i < searchedText2.length()) {
					if (smallLetterName.charAt(j) == searchedText2.charAt(i)) {
						htmlName += "<b>" + name.charAt(j) + "</b>";
						i++;
						j++;
					} else {
						htmlName += name.charAt(j);
						j++;
					}
				}
				if (j < name.length()) {
					htmlName += name.substring(j, name.length());
				}
			}
			return Html.fromHtml(htmlName);
		}
	}

	private String getHighlightedShortform(String name, String searchedText2) {
		String smallCapsName = name.toLowerCase(l);
		String possibleShortform = "";
		for (int i = 0; i < searchedText2.length(); i++) {
			possibleShortform = "";
			for (int j = 0; j < searchedText2.length(); j++) {
				if (j <= i) {
					possibleShortform += searchedText2.charAt(j);
				} else {
					possibleShortform += "(.*)" + " " + searchedText2.charAt(j);
				}
			}
			possibleShortform += "(.*)";
			if (smallCapsName.matches(possibleShortform)) {
				name = makeBold(name, searchedText2.substring(0, i + 1));
				i++;
				while (i < searchedText2.length()) {
					name = makeBold(name, " " + searchedText2.charAt(i));
					i++;
				}
			}
		}
		return name;
	}

	private String makeBold(String name, String substring) {
		String smallCapsName = name.toLowerCase(l);
		int firstIndex = smallCapsName.indexOf(substring);
		if (name.charAt(firstIndex) == ' ') {
			name = name.substring(0, firstIndex + 1)
					+ "<b>"
					+ name.substring(firstIndex + 1,
							firstIndex + substring.length())
					+ "</b>"
					+ name.substring(firstIndex + substring.length(),
							name.length());
		} else {
			name = name.substring(0, firstIndex)
					+ "<b>"
					+ name.substring(firstIndex,
							firstIndex + substring.length())
					+ "</b>"
					+ name.substring(firstIndex + substring.length(),
							name.length());
		}
		return name;
	}

	public void filter(String charText) {
		turnOnResidences = false;
		charText = charText.toLowerCase(Locale.getDefault());
		searchedText = charText;
		resultlist.clear();
		map.clear();
		if (charText.length() == 0) {
			if (settingsManager.showResidences()) {
				resultlist.addAll(inputlist);
			} else {
				for (Marker m : inputlist) {
					if (m.getGroupIndex() != Marker.RESIDENCES) {
						resultlist.add(m);
					}
				}
			}
		} else if (charText.length() > 10) {
			for (Marker m : inputlist) {
				if (m.getName().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					if (settingsManager.showResidences()) {
						resultlist.add(m);
					} else {
						if (m.getGroupIndex() != Marker.RESIDENCES) {
							resultlist.add(m);
						} else {
							turnOnResidences = true;
						}
					}
				}
			}
		} else {
			for (Marker m : inputlist) {
				int score = checkModifyMarker(m, charText);
				if (score != 0) {
					if (settingsManager.showResidences()) {
						map.add(new ScoredMarker(score, m));
					} else {
						if (m.getGroupIndex() != Marker.RESIDENCES) {
							map.add(new ScoredMarker(score, m));
						} else {
							turnOnResidences = true;
						}
					}
				}
			}
			resultlist = sortByScore(map);
		}
		notifyDataSetChanged();
	}

	private List<Marker> sortByScore(List<ScoredMarker> tempScore) {
		List<Marker> templist = new ArrayList<Marker>();
		Collections.sort(tempScore, new MarkerScoreComparator());
		for (ScoredMarker k : tempScore) {
			templist.add(k.m);
		}
		return templist;
	}

	private int checkModifyMarker(Marker m, String charText) {
		int tempScore = 5;
		String tempCharText = "(.*)";
		for (int i = 0; i < charText.length(); i++) {
			tempCharText += charText.charAt(i) + "(.*)";
		}
		if (m.getName().toLowerCase(l).matches(tempCharText)) {
			boolean b = false;
			if (m.getName().toLowerCase(l).startsWith(charText)) {
				return 1;
			}
			if (isShortForm(m.getName(), charText)) {
				return 2;
			}
			for (String s : m.getName().split(" ")) {
				b = b || s.toLowerCase(l).startsWith("" + charText.charAt(0));
				if (!b) {
					tempScore += 10;
				}
				if (s.startsWith(charText)) {
					return 3;
				}
			}
			if (b) {
				return tempScore;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	private boolean isShortForm(String name, String charText) {
		name = name.toLowerCase(l);
		charText = charText.toLowerCase(l).replaceAll("\\s", "");
		String possibleShortform = "";
		for (int i = 0; i < charText.length(); i++) {
			possibleShortform = "";
			for (int j = 0; j < charText.length(); j++) {
				if (j <= i) {
					possibleShortform += charText.charAt(j);
				} else {
					possibleShortform += "(.*)" + " " + charText.charAt(j);
				}
			}
			possibleShortform += "(.*)";
			if (name.matches(possibleShortform)) {
				return true;
			}
		}
		return false;
	}

	public SettingsManager getSettingsManager() {
		return settingsManager;
	}

	public void setSettingsManager(SettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}

	public class MarkerScoreComparator implements Comparator<ScoredMarker> {
		public int compare(ScoredMarker m1, ScoredMarker m2) {
			return m1.score - m2.score;
		}
	}

	public class MarkerNameComparator implements Comparator<Marker> {
		public int compare(Marker m1, Marker m2) {
			return m1.getName().toLowerCase(l)
					.compareTo(m2.getName().toLowerCase(l));
		}
	}
}
