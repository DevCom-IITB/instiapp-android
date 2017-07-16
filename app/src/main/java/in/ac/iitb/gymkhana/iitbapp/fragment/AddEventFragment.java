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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.ac.iitb.gymkhana.iitbapp.AddEventAdapter;
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
    Image image;
    @BindView(R.id.advanced_menu)
    TextView advancedMenu;
    int publicStatus;
    View view;
    ListPopupWindow popupWindow;
    AddEventAdapter addEventAdapter;


    public AddEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();

        view = inflater.inflate(R.layout.fragment_add_event, container, false);
        ButterKnife.bind(this, view);

        popupWindow=new ListPopupWindow(getContext());
        addEventAdapter=new AddEventAdapter();

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

        advancedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPopup();
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
        EventCreateRequest eventCreateRequest = new EventCreateRequest(eventName.getText().toString(), details.getText().toString(), venue.getText().toString(), timestamp_start, timestamp_end, addEventAdapter.publicStatus, 0, 0);
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

    public void createPopup() {

        popupWindow.setAdapter(addEventAdapter);
        popupWindow.setAnchorView(advancedMenu);

        popupWindow.setHeight(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(advancedMenu.getRight() - advancedMenu.getLeft());


        popupWindow.show();

    }

}
