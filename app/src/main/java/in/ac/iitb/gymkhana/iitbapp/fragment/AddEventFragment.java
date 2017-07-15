package in.ac.iitb.gymkhana.iitbapp.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.ac.iitb.gymkhana.iitbapp.MainActivity;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class AddEventFragment extends Fragment {
    @BindView(R.id.button_createEvent)
    Button createEvent;
    @BindView(R.id.cb_public)
    CheckBox publicCheckBox;
    @BindView(R.id.tv_date)
    TextView date;
    @BindView(R.id.et_eventName)
    EditText eventName;
    @BindView(R.id.tv_time)
    TextView time;
    @BindView(R.id.et_venue)
    EditText venue;
    @BindView(R.id.et_eventDetails)
    EditText details;
    @BindView(R.id.iv_eventImage)
    ImageView eventImage;
    @BindView(R.id.ib_eventImage)
    ImageButton imageButton;
    Timestamp timestamp;
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

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                long millis = calendar.getTimeInMillis();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
                timestamp = new Timestamp(millis);
            }

        });
        if (publicCheckBox.isChecked()) {
            publicStatus = 1;
        } else
            publicStatus = 0;

        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMin = calendar.get(Calendar.MINUTE);
                long millis = calendar.getTimeInMillis();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMin, true);
                timePickerDialog.show();
                timestamp = new Timestamp(millis);

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
                addEvent();
            }
        });


        return view;
    }

    public void addEvent() {
        EventCreateRequest eventCreateRequest = new EventCreateRequest(eventName.getText().toString(), details.getText().toString(), venue.getText().toString(), timestamp, timestamp, publicStatus, 0, 0);
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.eventCreate(eventCreateRequest).enqueue(new Callback<EventCreateResponse>() {
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
