package in.ac.iitb.gymkhana.iitbapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import java.util.Calendar;

public class CustomTimePicker extends TimePickerDialog {

    private int minhour=-1,minMinute=-1;
    private int currentHour, currentMinute;

    public CustomTimePicker(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
        currentHour= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        currentMinute=Calendar.getInstance().get(Calendar.MINUTE);
    }

    public void setMin(int hour, int minute){
        minhour=hour;
        minMinute=minute;
    }
    public void removeMin(){
        minhour=-1;
        minMinute=-1;
    }


    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        super.onTimeChanged(view, hourOfDay, minute);

        boolean validTime;
        validTime = hourOfDay >= minhour && (minhour != hourOfDay || minMinute <= minute);

        if (validTime) {
            currentHour = hourOfDay;
            currentMinute = minute;
        } else {
            updateTime(currentHour, currentMinute);
        }

    }
}
