package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.MainActivity;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.EventAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.data.Event;
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
        final CalendarView simpleCalendarView = (CalendarView) view.findViewById(R.id.simpleCalendarView); // get the reference of CalendarView
        simpleCalendarView.setFirstDayOfWeek(1); // set Sunday as the first day of the week

        simpleCalendarView.setWeekNumberColor(getResources().getColor(R.color.colorCalendarWeek));//setWeekNumberColor

        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {


                if (toast != null) {
                    toast.cancel();
                }
                String sdate = dayOfMonth + "/" + (month + 1) + "/" + year;
                toast = Toast.makeText(getContext(), "Date: (" + sdate + ")", Toast.LENGTH_LONG);
                toast.show();
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

        updateEvents();
        return view;

    }

    private void updateEvents() {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getNewsFeed(((MainActivity)getActivity()).getSessionIDHeader()).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    NewsFeedResponse newsFeedResponse = response.body();
                    events = newsFeedResponse.getEvents();
                }
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                //Network Error
            }
        });
    }

    private void showEventsForDate(Date date) {

        final List<Event> filteredEvents = new ArrayList<Event>();
        for( Event event : events) {
            Date nextDay = new Date(date.getTime() + (1000 * 60 * 60 * 24));
            Timestamp start = event.getEventStartTime();
            if (start.after(date) && start.before(nextDay)) {
                filteredEvents.add(event);
            }
        }

        RecyclerView eventRecyclerView = (RecyclerView) getActivity().findViewById(R.id.calendar_event_card_recycler_view);
        EventAdapter eventAdapter = new EventAdapter(filteredEvents, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Event event = filteredEvents.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EVENT_JSON, new Gson().toJson(event));
                EventFragment eventFragment = new EventFragment();
                eventFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                ft.addToBackStack(eventFragment.getTag());
                ft.commit();
            }
        });
        eventRecyclerView.setAdapter(eventAdapter);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
