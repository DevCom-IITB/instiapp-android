package in.ac.iitb.gymkhana.iitbapp.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddEventFragment extends Fragment {
    @BindView(R.id.button_createEvent)
    Button createEvent;

    @BindView(R.id.tv_start)
    TextView start;
    @BindView(R.id.et_eventName)
    EditText eventName;
    @BindView(R.id.tv_end)
    TextView end;
    @BindView(R.id.et_venue)
    EditText venue;
    @BindView(R.id.et_eventDetails)
    EditText details;
    @BindView(R.id.iv_eventImage)
    ImageView eventImage;
    @BindView(R.id.ib_eventImage)
    ImageButton imageButton;
    Timestamp timestamp_start;
    Timestamp timestamp_end;

    @BindView(R.id.advanced_menu)
    RelativeLayout advancedMenu;
    @BindView(R.id.cb_public)
    CheckBox cb_public;
    @BindView(R.id.cb_permission)
    CheckBox cb_permission;
    @BindView(R.id.map_location)
    EditText et_mapLocation;
    @BindView(R.id.open)
            ImageView open;
    @BindView(R.id.close)
            ImageView close;
    int publicStatus;
    View view;


    public AddEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();

        view = inflater.inflate(R.layout.fragment_add_event, container, false);
        ButterKnife.bind(this, view);

        cb_permission.setVisibility(View.GONE);
        cb_public.setVisibility(View.GONE);
        et_mapLocation.setVisibility(View.GONE);


        close.setVisibility(View.GONE);
        open.setVisibility(View.VISIBLE);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int mMin = calendar.get(Calendar.MINUTE);
                long millis = calendar.getTimeInMillis();


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                start.setText(dayOfMonth + "/" + month + "/" + year + " " + hourOfDay + ":" + minute);
                            }
                        }, mHour, mMin, true);
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);


                datePickerDialog.show();
                timestamp_start = new Timestamp(millis);

            }

        });


        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int mMin = calendar.get(Calendar.MINUTE);
                long millis = calendar.getTimeInMillis();


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                end.setText(dayOfMonth + "/" + month + "/" + year + " " + hourOfDay + ":" + minute);
                            }
                        }, mHour, mMin, true);
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);


                datePickerDialog.show();
                timestamp_end = new Timestamp(millis);

            }

        });
        if (cb_permission.isChecked()) {
            publicStatus = 1;
        } else publicStatus = 0;

        advancedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_public.getVisibility() == View.VISIBLE) {
                    open.setVisibility(View.VISIBLE);
                    close.setVisibility(View.GONE);
                    cb_permission.setVisibility(View.GONE);
                    cb_public.setVisibility(View.GONE);
                    et_mapLocation.setVisibility(View.GONE);
                } else {
                    close.setVisibility(View.VISIBLE);
                    open.setVisibility(View.GONE);
                    cb_permission.setVisibility(View.VISIBLE);
                    cb_public.setVisibility(View.VISIBLE);
                    et_mapLocation.setVisibility(View.VISIBLE);
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Add Image", Toast.LENGTH_SHORT).show();
                //TODO (1) upload image to server
            }
        });
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Add Event", Toast.LENGTH_SHORT).show();
                //TODO (2) save event
                addEvent();

            }
        });


        return view;
    }

    public void addEvent() {
        EventCreateRequest eventCreateRequest = new EventCreateRequest(eventName.getText().toString(), details.getText().toString(), "http://resources.wncc-iitb.org/logo_banner.png", timestamp_start.toString(), timestamp_end.toString(), false, Arrays.asList(new String[]{venue.getText().toString()}), Arrays.asList(new String[]{"bde82d5e-f379-4b8a-ae38-a9f03e4f1c4a"}));
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.createEvent(eventCreateRequest).enqueue(new Callback<EventCreateResponse>() {
            @Override
            public void onResponse(Call<EventCreateResponse> call, Response<EventCreateResponse> response) {
                Toast.makeText(getContext(), "Event Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<EventCreateResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Event Creation Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
