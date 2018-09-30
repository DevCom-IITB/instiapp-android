package app.insti.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import app.insti.Constants;
import app.insti.interfaces.ItemClickListener;
import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.FeedAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.response.NewsFeedResponse;
import app.insti.api.model.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends BaseFragment {

    FloatingActionButton fab;
    private View view;
    private Toast toast;
    private List<Event> events;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Calendar");

        final CalendarView simpleCalendarView = (CalendarView) view.findViewById(R.id.simpleCalendarView); // get the reference of CalendarView
        simpleCalendarView.setFirstDayOfWeek(1); // set Sunday as the first day of the week

        simpleCalendarView.setWeekNumberColor(getResources().getColor(R.color.colorCalendarWeek));//setWeekNumberColor

        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String sdate = dayOfMonth + "/" + (month + 1) + "/" + year;
                try {
                    Date showDate = new SimpleDateFormat("dd/M/yyyy").parse(sdate);
                    showEventsForDate(showDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                AddEventFragment addEventFragment = new AddEventFragment();
                ((MainActivity) getActivity()).updateFragment(addEventFragment);
            }
        });
        if (((MainActivity) getActivity()).createEventAccess()) {
            fab.setVisibility(View.VISIBLE);
        }

        updateEvents();
        return view;

    }

    private void updateEvents() {
        String ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        final SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT);
        isoFormatter.setTimeZone(utc);

        final Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        final Date oneMonthBackDate = cal.getTime();
        cal.add(Calendar.MONTH, 2);
        final Date oneMonthOnDate = cal.getTime();

        final String oneMonthBack = isoFormatter.format(oneMonthBackDate).toString();
        final String oneMonthOn = isoFormatter.format(oneMonthOnDate).toString();

        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.getEventsBetweenDates(((MainActivity) getActivity()).getSessionIDHeader(), oneMonthBack, oneMonthOn).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    NewsFeedResponse newsFeedResponse = response.body();
                    events = newsFeedResponse.getEvents();
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date todayWithZeroTime = formatter.parse(formatter.format(today));
                        showEventsForDate(todayWithZeroTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                //Network Error
                Toast.makeText(getContext(), "Failed to fetch events!", Toast.LENGTH_SHORT).show();
            }
        });
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
        FeedAdapter eventAdapter = new FeedAdapter(filteredEvents, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Event event = filteredEvents.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EVENT_JSON, new Gson().toJson(event));
                EventFragment eventFragment = new EventFragment();
                eventFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                ft.addToBackStack(eventFragment.getTag());
                ft.commit();
            }
        });
        eventRecyclerView.setAdapter(eventAdapter);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

}
