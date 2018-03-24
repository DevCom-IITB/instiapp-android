package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import in.ac.iitb.gymkhana.iitbapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    FloatingActionButton fab;
    private View view;
    private Toast toast;

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
                toast = Toast.makeText(getContext(), "Date: (" + dayOfMonth + "/" + (month + 1) + "/" + year + ")", Toast.LENGTH_LONG);
                toast.show();


            }
        });
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                AddEventFragment addEventFragment = new AddEventFragment();
                addEventFragment.setArguments(getArguments());
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.calendar_layout, addEventFragment);
                ft.addToBackStack("addEvent");
                ft.commit();
            }
        });
        return view;

    }

}
