package app.insti.fragment;


import android.animation.ArgbEvaluator;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.adapter.FeedAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Event;
import app.insti.api.response.NewsFeedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends BaseFragment {

    private FloatingActionButton fab;
    private View view;
    private FeedAdapter feedAdapter = null;
    private List<Event> events = new ArrayList<>();
    private HashSet<CalendarDay> haveMonths = new HashSet<>();
    private boolean initialized = false;


    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        // Setup toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Calendar");
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_calendar);

        // Handle selecting date
        final MaterialCalendarView matCalendarView = view.findViewById(R.id.simpleCalendarView);
        matCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    try {
                        showEventsForDate(toDate(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Update events on month change
        matCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                updateEvents(date, false);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateEvents(CalendarDay.today(), true);
    }

    /** Show the fab if we can make events */
    private void showFab() {
        if (((MainActivity) getActivity()).createEventAccess()) {
            fab.show();
            NestedScrollView nsv = view.findViewById(R.id.calendar_nsv);
            nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) fab.hide();
                    else fab.show();
                }
            });

            // Handle fab click
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalendarDay day = ((MaterialCalendarView) view.findViewById(R.id.simpleCalendarView)).getSelectedDate();
                    String date = day.getYear() + "-" + day.getMonth() + "-" + day.getDay();
                    WebViewFragment webViewFragment = (new WebViewFragment()).withDate(date);

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.WV_TYPE, Constants.WV_TYPE_ADD_EVENT);
                    webViewFragment.setArguments(bundle);

                    ((MainActivity) getActivity()).updateFragment(webViewFragment);
                }
            });
        }
    }


    /** Convert CalendarDay to Date */
    private Date toDate(CalendarDay date) throws ParseException {
        String sdate = date.getDay() + "/" + date.getMonth() + "/" + date.getYear();
        Date showDate = new SimpleDateFormat("dd/M/yyyy").parse(sdate);
        return showDate;
    }

    /** Decorator for Calendar */
    private class EventDecorator implements DayViewDecorator {
        private final int color = getResources().getColor(R.color.colorAccent);
        private final int white = Utils.getAttrColor(getContext(), R.attr.themeColor);

        private final HashSet<CalendarDay> dates;
        private final int alpha;

        public EventDecorator(int alpha, HashSet<CalendarDay> dates) {
            this.dates = dates;
            this.alpha = alpha;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            // Color background with alpha
            GradientDrawable gD = new GradientDrawable();
            gD.setColor((int) new ArgbEvaluator().evaluate(((float) alpha / 255.0f), white, color));
            gD.setShape(GradientDrawable.OVAL);

            // Inset to show border on selected
            InsetDrawable iD;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iD = new InsetDrawable(gD, 0.1f);
            } else {
                iD = new InsetDrawable(gD, 10);
            }

            view.setBackgroundDrawable(iD);
        }
    }

    private void updateEvents(CalendarDay calendarDay, final boolean setToday) {
        // Do not make duplicate calls
        if (haveMonths.contains(calendarDay)) {
            if (!setToday) {
                return;
            } else {
                setupCalendar(true);
            }
        }
        haveMonths.add(calendarDay);

        // Parsers
        String ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        final SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT);
        isoFormatter.setTimeZone(utc);

        // Get the start date
        final Date startDate;
        try {
            startDate = toDate(calendarDay);
        } catch (ParseException ignored) { return; }

        // Get start and end times
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, -1);
        final Date oneMonthBackDate = cal.getTime();
        cal.add(Calendar.MONTH, 2);
        final Date oneMonthOnDate = cal.getTime();

        final String oneMonthBack = isoFormatter.format(oneMonthBackDate).toString();
        final String oneMonthOn = isoFormatter.format(oneMonthOnDate).toString();

        // Make the API call
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getEventsBetweenDates(Utils.getSessionIDHeader(), oneMonthBack, oneMonthOn).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    if (getActivity() == null || getView() == null) return;

                    // Concatenate the response
                    NewsFeedResponse newsFeedResponse = response.body();
                    List<Event> eventList = newsFeedResponse.getEvents();
                    if (eventList == null) return;

                    // Concatenate
                    for (Event event : eventList) {
                        if (!events.contains(event)) events.add(event);
                    }

                    setupCalendar(setToday);
                }
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                //Network Error
                Toast.makeText(getContext(), "Failed to fetch events!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Show the calendar */
    private void setupCalendar(boolean setToday) {
        // Make the calendar visible if it isn't
        final LinearLayout calendarLayout = getView().findViewById(R.id.calendar_layout);
        if (calendarLayout.getVisibility() == View.GONE) {
            calendarLayout.setVisibility(VISIBLE);
            getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(250);
            calendarLayout.startAnimation(anim);
        }

        // Initialize to show today's date
        final MaterialCalendarView matCalendarView = view.findViewById(R.id.simpleCalendarView);
        if (setToday) {
            // Select today's date
            if (!initialized) {
                initialized = true;
                matCalendarView.setSelectedDate(CalendarDay.today());
            }

            // Show the fab
            showFab();
        }

        // Show today
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date todayWithZeroTime = formatter.parse(formatter.format(toDate(matCalendarView.getSelectedDate())));
            showEventsForDate(todayWithZeroTime);
        } catch (ParseException ignored) {}

        // Generate the decorators
        showHeatMap(events);
    }

    /** Build and show the heat map from the list of events */
    private void showHeatMap(List<Event> eventList) {
        // Build strength map for each date
        Map<CalendarDay, Integer> strength = new HashMap<>();
        for (Event event : eventList) {
            // Get starting date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(event.getEventStartTime());
            CalendarDay day = CalendarDay.from(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DATE)
            );

            // Update the map with strength
            if (strength.containsKey(day)) {
                strength.put(day, strength.get(day) + 1);
            } else {
                strength.put(day, 1);
            }
        }

        // Get the calendar
        final MaterialCalendarView matCalendarView = view.findViewById(R.id.simpleCalendarView);

        // Remove all decorators
        matCalendarView.removeDecorators();

        // Create decorator for each color type
        final int scale = 2;
        final int maxMult = 5;
        final int alphaStep = (int) (255.0f / (scale * maxMult));
        for (int i = 1; i <= maxMult; i++) {
            HashSet<CalendarDay> days = new HashSet<>();

            // Iterate over the map to check remaining entries
            Iterator it = strength.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                int noEvents = (Integer) pair.getValue();
                if (noEvents <= i * scale || (i == maxMult && noEvents > i * scale)) {
                    days.add((CalendarDay) pair.getKey());
                    it.remove();
                }
            }

            // Add the decorator
            if (days.size() > 0)
                matCalendarView.addDecorator(new EventDecorator(scale * i * alphaStep, days));
        }
    }

    private void showEventsForDate(Date date) {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;

        final List<Event> filteredEvents = new ArrayList<Event>();
        for (Event event : events) {
            Date nextDay = new Date(date.getTime() + (1000 * 60 * 60 * 24));
            Timestamp start = event.getEventStartTime();
            if (start.after(date) && start.before(nextDay)) {
                filteredEvents.add(event);
            }
        }

        /* Show number of events */
        TextView noEvents = getActivity().findViewById(R.id.number_of_events);
        if (filteredEvents.size() == 0) {
            noEvents.setText("No Events");
        } else if (filteredEvents.size() == 1) {
            noEvents.setText("1 Event");
        } else {
            noEvents.setText(Integer.toString(filteredEvents.size()) + " Events");
        }

        RecyclerView eventRecyclerView = (RecyclerView) getActivity().findViewById(R.id.calendar_event_card_recycler_view);

        // Initialize or refresh adapter
        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter(filteredEvents, this);
        } else {
            feedAdapter.setList(filteredEvents);
            feedAdapter.notifyDataSetChanged();
        }

        // Initialize recycler view
        if (eventRecyclerView.getAdapter() != feedAdapter) {
            eventRecyclerView.setAdapter(feedAdapter);
            eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

}
