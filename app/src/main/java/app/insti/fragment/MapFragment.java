package app.insti.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mrane.campusmap.ExpandableListAdapter;
import com.mrane.campusmap.FuzzySearchAdapter;
import com.mrane.campusmap.IndexFragment;
import com.mrane.campusmap.ListFragment;
import com.mrane.campusmap.SettingsManager;
import com.mrane.data.Building;
import com.mrane.data.Locations;
import com.mrane.data.Room;
import com.mrane.navigation.CardSlideListener;
import com.mrane.navigation.SlidingUpPanelLayout;
import com.mrane.zoomview.CampusMapView;
import com.mrane.zoomview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import app.insti.MainActivity;
import app.insti.R;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.data.Venue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements TextWatcher,
        TextView.OnEditorActionListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener,
        View.OnTouchListener, ExpandableListView.OnChildClickListener {
    private static MapFragment mainactivity;
    private SettingsManager settingsManager;
    private FuzzySearchAdapter adapter;
    private ExpandableListAdapter expAdapter;
    private FragmentManager fragmentManager;
    private ListFragment listFragment;
    private IndexFragment indexFragment;
    private Fragment fragment;
    public LinearLayout newSmallCard;
    public ImageView placeColor;
    private RelativeLayout fragmentContainer;
    private View actionBarView;
    public TextView placeNameTextView;
    public TextView placeSubHeadTextView;
    public EditText editText;
    public HashMap<String, com.mrane.data.Marker> data;
    private List<com.mrane.data.Marker> markerlist;
    public FragmentTransaction transaction;
    public CampusMapView campusMapView;
    public ImageButton removeIcon;
    public ImageButton indexIcon;
    public ImageButton mapIcon;
    public ImageButton addMarkerIcon;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SlidingUpPanelLayout slidingLayout;
    private CardSlideListener cardSlideListener;
    private boolean noFragments = true;
    private boolean editTextFocused = false;
    private final String firstStackTag = "FIRST_TAG";
    private final int MSG_ANIMATE = 1;
    private final int MSG_PLAY_SOUND = 2;
    private final int MSG_DISPLAY_MAP = 3;
    private final long DELAY_ANIMATE = 150;
    private final long DELAY_INIT_LAYOUT = 250;
    private Toast toast;
    private String message = "Sorry, no such place in our data.";
    public static final PointF MAP_CENTER = new PointF(2971f, 1744f);
    public static final long DURATION_INIT_MAP_ANIM = 500;
    public static final String FONT_SEMIBOLD = "rigascreen_bold.ttf";
    public static final String FONT_REGULAR = "rigascreen_regular.ttf";
    public static final int SOUND_ID_RESULT = 0;
    public static final int SOUND_ID_ADD = 1;
    public static final int SOUND_ID_REMOVE = 2;
    public SoundPool soundPool;
    public int[] soundPoolIds;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ANIMATE:
                    showResultOnMap((String) msg.obj);
                    break;
                case MSG_PLAY_SOUND:
                    playAnimSound(msg.arg1);
                    break;
                case MSG_DISPLAY_MAP:
                    displayMap();
                    break;
            }
        }
    };

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainactivity = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Set title */
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("InstiMap");

        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getAllVenues().enqueue(new Callback<List<Venue>>() {
            @Override
            public void onResponse(Call<List<Venue>> call, Response<List<Venue>> response) {
                if (response.isSuccessful()) {
                    Locations mLocations = new Locations(response.body());
                    data = mLocations.data;
                    markerlist = new ArrayList<com.mrane.data.Marker>(data.values());
                    setUpDrawer();
                    setupMap();
                }
            }

            @Override
            public void onFailure(Call<List<Venue>> call, Throwable t) { }
        });

    }

    private void setupMap() {
        if (getView() == null) {
            return;
        }

        newSmallCard = (LinearLayout) getActivity().findViewById(R.id.new_small_card);
        slidingLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
        placeNameTextView = (TextView) getActivity().findViewById(R.id.place_name);
        placeColor = (ImageView) getActivity().findViewById(R.id.place_color);
        placeSubHeadTextView = (TextView) getActivity().findViewById(R.id.place_sub_head);

        cardSlideListener = new CardSlideListener(this);
        slidingLayout.setPanelSlideListener(cardSlideListener);

        slidingLayout.post(setAnchor());

        initShowDefault();
        initImageUri();

        fragmentContainer = (RelativeLayout) getActivity().findViewById(R.id.fragment_container);

        adapter = new FuzzySearchAdapter(getContext(), markerlist);
        editText = (EditText)getView().findViewById(R.id.search);
        editText.addTextChangedListener(this);
        editText.setOnEditorActionListener(this);
        editText.setOnFocusChangeListener(this);

        settingsManager = new SettingsManager(getContext());

        campusMapView = (CampusMapView) getActivity().findViewById(R.id.campusMapView);
        campusMapView.setImageAsset("map.jpg");
        campusMapView.setSettingsManager(settingsManager);
        campusMapView.setData(data);

        removeIcon = (ImageButton) getActivity().findViewById(R.id.remove_icon);
        removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeClick(v);
            }
        });
        indexIcon = (ImageButton) getActivity().findViewById(R.id.index_icon);
        indexIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexClick(v);
            }
        });
        mapIcon = (ImageButton) getActivity().findViewById(R.id.map_icon);
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapClick(v);
            }
        });
        addMarkerIcon = (ImageButton) getActivity().findViewById(R.id.add_marker_icon);

        fragmentManager = getChildFragmentManager();
        listFragment = new ListFragment();
        indexFragment = new IndexFragment();

        adapter.setSettingsManager(settingsManager);

        initSoundPool();
        setFonts();

        getActivity().findViewById(R.id.add_marker_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarkerClick(v);
            }
        });

        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private void setUpDrawer() {
        Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        actionBarView = getActivity().findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close) {

            TextView settingsTitle = (TextView) getActivity().findViewById(R.id.settings_title);

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                settingsTitle.setVisibility(View.GONE);

                editText.setVisibility(View.VISIBLE);
                setCorrectIcons();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                editText.setVisibility(View.GONE);
                indexIcon.setVisibility(View.GONE);
                mapIcon.setVisibility(View.GONE);
                removeIcon.setVisibility(View.GONE);
                settingsTitle.setVisibility(View.VISIBLE);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initShowDefault() {
        String[] keys = { "Convocation Hall", "Hostel 13 House of Titans",
                "Hostel 15", "Main Gate no. 2",
                "Market Gate, Y point Gate no. 3", "Lake Side Gate no. 1", };
        for (String key : keys) {
            if (data.containsKey(key)) {
                data.get(key).setShowDefault(true);
            } else {
                Log.d("null point", "key not found (initShowDefault): " + key);
            }
        }
    }

    private void initImageUri() {
        String[] keys = { "Convocation Hall", "Guest House/ Jalvihar",
                "Guest House/ Vanvihar", "Gulmohar Restaurant", "Hostel 14",
                "Industrial Design Centre", "Main Building",
                "Nestle Cafe (Coffee Shack)", "School of Management",
                "Victor Menezes Convention Centre" };
        String[] uri = { "convo_hall", "jalvihar", "vanvihar", "gulmohar",
                "h14", "idc", "mainbuilding", "nescafestall", "som", "vmcc" };
        for (int i = 0; i < keys.length; i++) {
            if (data.containsKey(keys[i])) {
                data.get(keys[i]).setImageUri(uri[i]);
            } else {
                Log.d("null point", "check " + keys[i]);
            }
        }
    }

    private void setFonts() {
        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), FONT_REGULAR);

        placeNameTextView.setTypeface(regular, Typeface.BOLD);
        placeSubHeadTextView.setTypeface(regular);
        editText.setTypeface(regular);

        TextView settingsTitle = (TextView) getActivity()
                .findViewById(R.id.settings_title);
        settingsTitle.setTypeface(regular);
    }

    private Runnable setAnchor() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int totalHeight = slidingLayout.getHeight();
                int expandedCardHeight = getResources().getDimensionPixelSize(
                        R.dimen.expanded_card_height);
                float anchorPoint = 0.5f;
                slidingLayout.setAnchorPoint(anchorPoint);
                Log.d("testing", "Anchor point = " + anchorPoint);
            }
        };

        return runnable;
    }

    private void initSoundPool() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolIds = new int[3];
        soundPoolIds[SOUND_ID_RESULT] = soundPool.load(getContext(),
                R.raw.result_marker, 1);
        soundPoolIds[SOUND_ID_ADD] = soundPool.load(getContext(), R.raw.add_marker, 2);
        soundPoolIds[SOUND_ID_REMOVE] = soundPool.load(getContext(),
                R.raw.remove_marker, 3);
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        String text = editText.getText().toString()
                .toLowerCase(Locale.getDefault());
        adapter.filter(refineText(text));
    }

    private String refineText(String text) {
        String refinedText = text.replaceAll(Pattern.quote("("), "@")
                .replaceAll(Pattern.quote(")"), "@")
                .replaceAll(Pattern.quote("."), "@")
                .replaceAll(Pattern.quote("+"), "@")
                .replaceAll(Pattern.quote("{"), "@")
                .replaceAll(Pattern.quote("?"), "@")
                .replaceAll(Pattern.quote("\\"), "@")
                .replaceAll(Pattern.quote("["), "@")
                .replaceAll(Pattern.quote("^"), "@")
                .replaceAll(Pattern.quote("$"), "@");

        return refinedText;
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        this.setCorrectIcons();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((actionId == EditorInfo.IME_ACTION_SEARCH)
                || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            onItemClick(null, v, 0, 0);
        }
        return true;
    }

    public static MapFragment getMainActivity() {
        return mainactivity;
    }

    private void putFragment(Fragment tempFragment) {
        this.dismissCard();
        transaction = fragmentManager.beginTransaction();
        fragment = tempFragment;
        if (noFragments) {
            transaction.add(R.id.fragment_container, tempFragment);
            transaction.addToBackStack(firstStackTag);
            transaction.commit();
        } else {
            transaction.replace(R.id.fragment_container, tempFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        noFragments = false;
    }

    public void backToMap() {
        noFragments = true;
        this.hideKeyboard();
        fragmentManager.popBackStack(firstStackTag,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        this.removeEditTextFocus(null);
        this.setCorrectIcons();
        this.displayMap();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int id, long arg3) {
        if (adapter.getResultSize() == 0) {
            toast.setText(message);
            toast.show();
        } else {
            String selection = editText.getText().toString();
            if (id < adapter.getCount()) {
                selection = adapter.getItem(id).getName();
            }
            this.hideKeyboard();
            this.removeEditTextFocus(selection);
            this.backToMap();
        }
    }

    public void displayMap() {
        // check if is Image ready
        if (!campusMapView.isImageReady()) {
            Message msg = mHandler.obtainMessage(MSG_DISPLAY_MAP);
            mHandler.sendMessageDelayed(msg, DELAY_INIT_LAYOUT);
        } else {
            // get text from auto complete text box
            String key = editText.getText().toString();

            // get Marker object if exists
            com.mrane.data.Marker marker = data.get(key);

            // display and zoom to marker if exists
            if (marker != null) {
                Message msg = mHandler.obtainMessage(MSG_ANIMATE, key);
                mHandler.sendMessageDelayed(msg, DELAY_ANIMATE);
            } else {
                campusMapView.setResultMarker(null);
                this.dismissCard();
                campusMapView.invalidate();
            }
        }
    }

    private void showResultOnMap(String key) {
        com.mrane.data.Marker marker = data.get(key);
        showCard(marker);
        campusMapView.setAndShowResultMarker(marker);
    }

    public void showCard() {
        com.mrane.data.Marker marker = campusMapView.getResultMarker();
        showCard(marker);
    }

    public void showCard(com.mrane.data.Marker marker) {
        String name = marker.getName();
        if (!marker.getShortName().equals("0"))
            name = marker.getShortName();
        placeNameTextView.setText(name);
        setSubHeading(marker);
        setAddMarkerIcon(marker);
        addDescriptionView(marker);
        placeColor.setImageDrawable(new ColorDrawable(marker.getColor()));
        getActivity().findViewById(R.id.place_group_color).setBackgroundColor(
                marker.getColor());
        getActivity().findViewById(R.id.dragView).setVisibility(View.VISIBLE);
        reCenterMarker(marker);
        cardSlideListener.showCard();
    }

    private void setImage(LinearLayout parent, com.mrane.data.Marker marker) {
        View v = getLayoutInflater().inflate(R.layout.map_card_image, parent);
        ImageView iv = (ImageView) v.findViewById(R.id.place_image);
        int imageId = getResources().getIdentifier(marker.getImageUri(),
                "drawable", getContext().getPackageName());
        iv.setImageResource(imageId);
    }

    private void addDescriptionView(com.mrane.data.Marker marker) {
        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.other_details);
        parent.removeAllViews();
        if (!marker.getImageUri().isEmpty()) {
            setImage(parent, marker);
        }
        if (marker instanceof Building) {
            setChildrenView(parent, (Building) marker);
        }
        if (!marker.getDescription().isEmpty()) {
            View desc = getLayoutInflater().inflate(R.layout.map_place_description,
                    parent);

            TextView descHeader = (TextView) desc
                    .findViewById(R.id.desc_header);
            Typeface regular = Typeface.createFromAsset(getContext().getAssets(),
                    FONT_REGULAR);
            descHeader.setTypeface(regular, Typeface.BOLD);

            TextView descContent = (TextView) desc
                    .findViewById(R.id.desc_content);
            descContent.setTypeface(regular);
            descContent.setText(getDescriptionText(marker));
            Linkify.addLinks(descContent, Linkify.ALL);
            descContent.setLinkTextColor(Color.rgb(19, 140, 190));
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setChildrenView(LinearLayout parent, Building building) {
        View childrenView = getLayoutInflater().inflate(R.layout.map_children_view,
                parent);

        View headerLayout = childrenView.findViewById(R.id.header_layout);
        TextView headerName = (TextView) childrenView
                .findViewById(R.id.list_header);
        String headerText = "inside ";
        if (building.getShortName().equals("0"))
            headerText += building.getName();
        else
            headerText += building.getShortName();
        Typeface bold = Typeface.createFromAsset(getContext().getAssets(), FONT_REGULAR);
        headerName.setTypeface(bold, Typeface.BOLD);
        headerName.setText(headerText);

        final ImageView icon = (ImageView) childrenView
                .findViewById(R.id.arrow_icon);
        final ListView childrenListView = (ListView) childrenView
                .findViewById(R.id.child_list);
        childrenListView.setVisibility(View.GONE);

        ArrayList<String> childNames = new ArrayList<String>();
        for (String name : building.children) {
            childNames.add(name);
        }

        final CustomListAdapter adapter = new CustomListAdapter(getContext(),
                R.layout.map_child, childNames);
        childrenListView.setAdapter(adapter);

        childrenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                String key = adapter.getItem(position);
                removeEditTextFocus(key);
                backToMap();
            }

        });

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (childrenListView.getVisibility() == View.VISIBLE) {
                    childrenListView.setVisibility(View.GONE);
                    icon.setImageResource(R.drawable.ic_action_next_item);
                } else {
                    setListViewHeightBasedOnChildren(childrenListView);
                    childrenListView.setVisibility(View.VISIBLE);
                    icon.setImageResource(R.drawable.ic_action_expand);
                }
            }
        });

    }

    private class CustomListAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private int id;
        private List<String> items;

        public CustomListAdapter(Context context, int textViewResourceId,
                                 List<String> list) {
            super(context, textViewResourceId, list);
            mContext = context;
            id = textViewResourceId;
            items = list;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View mView = v;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }

            TextView text = (TextView) mView.findViewById(R.id.child_name);
            Log.d("testing", "position = " + position);
            if (items.get(position) != null) {
                Typeface regular = Typeface.createFromAsset(getContext().getAssets(),
                        FONT_REGULAR);
                text.setText(items.get(position));
                text.setTypeface(regular);
            }

            return mView;
        }

    }

    private SpannableStringBuilder getDescriptionText(com.mrane.data.Marker marker) {
        String text = marker.getDescription();
        SpannableStringBuilder desc = new SpannableStringBuilder(text);
        String[] toBoldParts = { "Email", "Phone No.", "Fax No." };
        for (String part : toBoldParts) {
            setBold(desc, part);
        }
        return desc;
    }

    private void setBold(SpannableStringBuilder text, String part) {
        int start = text.toString().indexOf(part);
        int end = start + part.length();
        final StyleSpan bold = new StyleSpan(Typeface.BOLD);
        if (start >= 0)
            text.setSpan(bold, start, end,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setSubHeading(com.mrane.data.Marker marker) {
        SpannableStringBuilder result = new SpannableStringBuilder("");
        result.append(marker.getName());
        if (marker instanceof Room) {
            Room room = (Room) marker;
            String tag = room.tag;
            if (!tag.equals("Inside")) {
                tag += ",";
            } else {
                tag = "in";
            }
            com.mrane.data.Marker parent = data.get(room.parentKey);
            final String parentKey = parent.getName();
            String parentName = parent.getName();
            if (!parent.getShortName().equals("0"))
                parentName = parent.getShortName();
            result.append(" - " + tag + " ");
            int start = result.length();
            result.append(parentName);
            int end = result.length();
            result.append(" ");
            ClickableSpan parentSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    editText.setText(parentKey);
                    displayMap();
                }

                @Override
                public void updateDrawState(TextPaint p) {
                    p.setColor(Color.rgb(19, 140, 190));
                    p.setUnderlineText(true);
                }
            };
            result.setSpan(parentSpan, start, end,
                    SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE);
            ClickableSpan restSpan1 = new ClickableSpan() {
                private TextPaint ds;

                @Override
                public void onClick(View widget) {
                    updateDrawState(ds);
                    widget.invalidate();
                    // newCardTouchListener.toggleExpansion();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.bgColor = Color.TRANSPARENT;
                    ds.setUnderlineText(false);
                    this.ds = ds;
                }
            };

            ClickableSpan restSpan2 = new ClickableSpan() {
                private TextPaint ds;

                @Override
                public void onClick(View widget) {
                    updateDrawState(ds);
                    widget.invalidate();
                    // newCardTouchListener.toggleExpansion();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.bgColor = Color.TRANSPARENT;
                    ds.setUnderlineText(false);
                    this.ds = ds;
                }
            };

            result.setSpan(restSpan1, 0, start,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            result.setSpan(restSpan2, end, end + 1,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            placeSubHeadTextView.setMovementMethod(LinkMovementMethod
                    .getInstance());
            // placeSubHeadTextView.setHighlightColor(Color.TRANSPARENT);
            placeSubHeadTextView.setOnClickListener(null);
        } else {
            placeSubHeadTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // newCardTouchListener.toggleExpansion();
                }
            });
        }
        placeSubHeadTextView.setText(result);
    }

    private Drawable getLockIcon(com.mrane.data.Marker marker) {
        int color = marker.getColor();
        int drawableId = R.drawable.lock_all_off;
        if (campusMapView.isAddedMarker(marker)) {
            if (color == com.mrane.data.Marker.COLOR_BLUE)
                drawableId = R.drawable.lock_blue_on;
            else if (color == com.mrane.data.Marker.COLOR_YELLOW)
                drawableId = R.drawable.lock_on_yellow;
            else if (color == com.mrane.data.Marker.COLOR_GREEN)
                drawableId = R.drawable.lock_green_on;
            else if (color == com.mrane.data.Marker.COLOR_GRAY)
                drawableId = R.drawable.lock_gray_on;
        }
        Drawable lock = getResources().getDrawable(drawableId);
        return lock;
    }

    public void expandCard() {
        reCenterMarker();
    }

    private void reCenterMarker() {
        com.mrane.data.Marker marker = campusMapView.getResultMarker();
        reCenterMarker(marker);
    }

    private void reCenterMarker(com.mrane.data.Marker marker) {
        PointF p = marker.getPoint();
        float shift = getResources().getDimension(R.dimen.expanded_card_height) / 2.0f;
        PointF center = new PointF(p.x, p.y + shift);
        SubsamplingScaleImageView.AnimationBuilder anim = campusMapView.animateCenter(center);
        anim.start();
    }

    public boolean removeMarker() {
        if (campusMapView.getResultMarker() == null) {
            return false;
        } else {
            if (slidingLayout.isPanelExpanded()
                    || slidingLayout.isPanelAnchored()) {
                slidingLayout.collapsePanel();
            } else {
                editText.setText("");
                campusMapView.setResultMarker(null);
                dismissCard();
            }
            return true;
        }
    }

    /**
     * Hides the card
     *
     * @return true if the card was visible while this function was called
     */
    public void dismissCard() {
        cardSlideListener.dismissCard();
        campusMapView.invalidate();
    }

    public void removeClick(View v) {
        this.editText.setText("");
        displayMap();
    }

    public void indexClick(View v) {
        this.putFragment(indexFragment);
        this.removeEditTextFocus(null);
        this.setCorrectIcons();
    }

    public void mapClick(View v) {
        this.backToMap();
        this.removeEditTextFocus("");
    }

    private void removeEditTextFocus(String text) {
        if (this.editTextFocused) {
            this.hideKeyboard();
            editText.clearFocus();
        }

        if (text == null) {

        } else if (text.equals("")) {
            this.setOldText();
        } else {
            editText.setText(text);
        }

    }

    public FuzzySearchAdapter getAdapter() {
        return adapter;
    }

    private void setOldText() {
        com.mrane.data.Marker oldMarker = campusMapView.getResultMarker();
        if (oldMarker == null) {
            if (editText.length() > 0) {
                editText.getText().clear();
            }
        } else {
            editText.setText(oldMarker.getName());
        }
    }

    private void setCorrectIcons() {
        if (noFragments) {
            if (this.handleRemoveIcon()) {
                this.noIndexButton();
            } else {
                this.setVisibleButton(indexIcon);
            }
        } else {
            if (fragment instanceof ListFragment) {
                if (this.handleRemoveIcon()) {
                    this.noIndexButton();
                } else {
                    this.setVisibleButton(indexIcon);
                }
            } else if (fragment instanceof IndexFragment) {
                this.setVisibleButton(mapIcon);
            }
        }
    }

    private void noIndexButton() {
        indexIcon.setVisibility(View.GONE);
        mapIcon.setVisibility(View.GONE);
    }

    private boolean handleRemoveIcon() {
        String text = editText.getText().toString();
        if (text.isEmpty() || text.equals(null)) {
            removeIcon.setVisibility(View.GONE);
            return false;
        } else {
            removeIcon.setVisibility(View.VISIBLE);
            return true;
        }
    }

    private void setVisibleButton(ImageButton icon) {
        indexIcon.setVisibility(View.GONE);
        mapIcon.setVisibility(View.GONE);

        icon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFocusChange(View v, boolean focus) {
        this.editTextFocused = focus;
        if (focus) {
            this.putFragment(listFragment);
            fragmentContainer.setOnTouchListener(this);
            String text = editText.getText().toString()
                    .toLowerCase(Locale.getDefault());
            adapter.filter(text);
        } else {
            fragmentContainer.setOnTouchListener(null);
        }
        this.setCorrectIcons();
    }

    private void hideKeyboard() {
        MainActivity.hideKeyboard(getActivity());
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        if (adapter.getResultSize() != 0) {
            removeEditTextFocus(null);
        }
        return false;
    }

    public void addMarkerClick(View v) {
        campusMapView.toggleMarker();
        setAddMarkerIcon();
    }

    public void playAnimSound(int sound_index) {
        if (sound_index >= 0 && sound_index < soundPoolIds.length) {
            if (!settingsManager.isMuted()) {
                soundPool.play(soundPoolIds[sound_index], 1.0f, 1.0f, 1, 0, 1f);
            }
        }
    }

    public void playAnimSoundDelayed(int sound_index, long delay) {
        Message msg = mHandler.obtainMessage(MSG_PLAY_SOUND, sound_index, 0);
        mHandler.sendMessageDelayed(msg, delay);
    }

    private void setAddMarkerIcon() {
        setAddMarkerIcon(campusMapView.getResultMarker());
    }

    private void setAddMarkerIcon(com.mrane.data.Marker m) {
        addMarkerIcon.setImageDrawable(getLockIcon(m));
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        String selection = (String) expAdapter.getChild(groupPosition,
                childPosition);
        this.hideKeyboard();
        this.removeEditTextFocus(selection);
        this.backToMap();
        return true;
    }

    public void setExpAdapter(ExpandableListAdapter expAdapter) {
        this.expAdapter = expAdapter;
    }

    private static final String INSTANCE_CARD_STATE = "instanceCardState";
    private static final String INSTANCE_VISIBILITY_INDEX = "instanceVisibilityIndex";

    public SlidingUpPanelLayout getSlidingLayout() {
        return slidingLayout;
    }
}

