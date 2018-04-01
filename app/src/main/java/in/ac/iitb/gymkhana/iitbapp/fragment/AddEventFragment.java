package in.ac.iitb.gymkhana.iitbapp.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.CustomTimePicker;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.ImageUploadRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.ImageUploadResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static in.ac.iitb.gymkhana.iitbapp.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static in.ac.iitb.gymkhana.iitbapp.Constants.RESULT_LOAD_IMAGE;


public class AddEventFragment extends BaseFragment {
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
    ImageView eventPictureImageView;
    int publicStatus;
    View view;
    String base64Image;
    ProgressDialog progressDialog;
    String TAG = "AddEventFragment";


    public AddEventFragment() {
        // Required empty public constructor
    }

    public static String convertImageToString(Bitmap imageBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (imageBitmap != null) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();

        view = inflater.inflate(R.layout.fragment_add_event, container, false);
        ButterKnife.bind(this, view);

        eventPictureImageView = view.findViewById(R.id.ib_eventImage);
        progressDialog = new ProgressDialog(getContext());

        cb_permission.setVisibility(View.GONE);
        cb_public.setVisibility(View.GONE);
        et_mapLocation.setVisibility(View.GONE);


        close.setVisibility(View.GONE);
        open.setVisibility(View.VISIBLE);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                final int mYear = calendar.get(Calendar.YEAR);
                final int mMonth = calendar.get(Calendar.MONTH);
                final int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int mMin = calendar.get(Calendar.MINUTE);
                long millis = calendar.getTimeInMillis();


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        CustomTimePicker timePickerDialog = new CustomTimePicker(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(minute<10) {
                                    start.setText(dayOfMonth + "/" + month + "/" + year + " " + hourOfDay + ":0" + minute);
                                }
                                else{

                                    start.setText(dayOfMonth + "/" + month + "/" + year + " " + hourOfDay + ":" + minute);
                                }
                            }
                        }, mHour, mMin, true);

                        if(year==mYear){
                            if(month==mMonth){
                                if(dayOfMonth==mDay){
                                    timePickerDialog.setMin(mHour,mMin);
                                }
                                else if(dayOfMonth>mDay){
                                    timePickerDialog.removeMin();
                                }
                            }
                            else{
                                timePickerDialog.removeMin();
                            }
                        }
                        else{
                            timePickerDialog.removeMin();
                        }
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);


                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

                datePickerDialog.show();
                timestamp_start = new Timestamp(millis);


            }


        });


        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                final int mYear = calendar.get(Calendar.YEAR);
                final int mMonth = calendar.get(Calendar.MONTH);
                final int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int mMin = calendar.get(Calendar.MINUTE);
                final long millis = calendar.getTimeInMillis();


                final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        CustomTimePicker timePickerDialog = new CustomTimePicker(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(minute<10) {
                                    end.setText(dayOfMonth + "/" + month + "/" + year + " " + hourOfDay + ":0" + minute);
                                }
                                else{
                                    end.setText(dayOfMonth + "/" + month + "/" + year + " " + hourOfDay + ":" + minute);
                                }
                            }
                        }, mHour, mMin, true);
                        if(year==mYear){
                            if(month==mMonth){
                                if(dayOfMonth==mDay){
                                    timePickerDialog.setMin(mHour,mMin);
                                }
                                else if(dayOfMonth>mDay){
                                   timePickerDialog.removeMin();
                                }
                            }
                            else{
                                timePickerDialog.removeMin();
                            }
                        }
                        else{
                            timePickerDialog.removeMin();
                        }

                        Log.i("hour", String.valueOf(mHour));
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);



                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    return;
                }
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                sendImage();
            }
        });
        return view;
    }

    private void sendImage() {
        progressDialog.setMessage("Uploading Image");
        ImageUploadRequest imageUploadRequest = new ImageUploadRequest(base64Image);
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.uploadImage("sessionid=" + getArguments().getString(Constants.SESSION_ID), imageUploadRequest).enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful()) {
                    ImageUploadResponse imageUploadResponse = response.body();
                    String imageURL = imageUploadResponse.getPictureURL();
                    addEvent(imageURL);
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void addEvent(String eventImageURL) {
        progressDialog.setMessage("Creating Event");
        EventCreateRequest eventCreateRequest = new EventCreateRequest(eventName.getText().toString(), details.getText().toString(), eventImageURL, timestamp_start.toString(), timestamp_end.toString(), false, Arrays.asList(new String[]{venue.getText().toString()}), Arrays.asList(new String[]{"bde82d5e-f379-4b8a-ae38-a9f03e4f1c4a"}));
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.createEvent("sessionid=" + getArguments().getString(Constants.SESSION_ID), eventCreateRequest).enqueue(new Callback<EventCreateResponse>() {
            @Override
            public void onResponse(Call<EventCreateResponse> call, Response<EventCreateResponse> response) {
                Toast.makeText(getContext(), "Event Created", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<EventCreateResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Event Creation Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            eventPictureImageView.setImageBitmap(getScaledBitmap(picturePath, imageButton.getWidth(), imageButton.getHeight()));
            eventPictureImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            base64Image = convertImageToString(getScaledBitmap(picturePath, 800, 800));
            Log.d(TAG, "onActivityResult: " + base64Image);
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
