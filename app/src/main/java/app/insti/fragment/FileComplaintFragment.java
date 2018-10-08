package app.insti.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import app.insti.Constants;
import app.insti.CustomAutoCompleteTextView;
import app.insti.R;
import app.insti.TagClass;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.adapter.ImageViewPagerAdapter;
import app.insti.api.LocationAPIUtils;
import app.insti.api.RetrofitInterface;
import app.insti.api.request.ComplaintCreateRequest;
import app.insti.api.request.ImageUploadRequest;
import app.insti.api.response.ComplaintCreateResponse;
import app.insti.api.response.ImageUploadResponse;
import app.insti.utils.TagCategories;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static app.insti.Constants.MY_PERMISSIONS_REQUEST_LOCATION;
import static app.insti.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static app.insti.Constants.RESULT_LOAD_IMAGE;

public class FileComplaintFragment extends Fragment {

    private static final String TAG = FileComplaintFragment.class.getSimpleName();
    private static FileComplaintFragment mainactivity;
    private Button buttonSubmit;
    private ImageButton imageActionButton;
    private CustomAutoCompleteTextView autoCompleteTextView;
    private EditText editTextSuggestions;
    private EditText editTextTags;
    private EditText editTextLocationDetails;
    private MapView mMapView;
    GoogleMap googleMap;
    private TagView tagView;
    private TagView tagViewPopulate;
    private ScrollView tagsLayout;
    private LatLng Location;
    private String Name;
    private String Address;
    private List<String> Tags;
    private ArrayList<TagClass> tagList;
    private List<String> uploadedImagesUrl = new ArrayList<>();
    private int cursor = 1;
    private List<TagClass> tagList2 = new ArrayList<>();

    private String base64Image;
    private ImageViewPagerAdapter imageViewPagerAdapter;
    private ViewPager viewPager;
    private CircleIndicator indicator;
    private Button buttonAnalysis;
    private RelativeLayout layout_buttons;
    String userId;
    View view;
    NestedScrollView nestedScrollView;
    private boolean GPSIsSetup = false;
    FusedLocationProviderClient mFusedLocationClient;
    ProgressDialog progressDialog;
    CollapsingToolbarLayout collapsing_toolbar;
    LinearLayout linearLayoutAnalyse;

    public FileComplaintFragment() {
        // Required empty public constructor
    }

    public static FileComplaintFragment getMainActivity() {
        return mainactivity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
        PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment) fragmentManager.findFragmentById(R.id.place_autocomplete_fragment);
        android.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mainactivity = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_file_complaint, container, false);

        Bundle bundle = getArguments();
        userId = bundle.getString(Constants.USER_ID);

        Toast.makeText(getContext(), getString(R.string.initial_message_file_complaint), Toast.LENGTH_LONG).show();

        prepareTags();

        progressDialog = new ProgressDialog(getContext());

        LinearLayout imageViewHolder = view.findViewById(R.id.image_holder_view);
        CollapsingToolbarLayout.LayoutParams layoutParams = new CollapsingToolbarLayout.LayoutParams(
                CollapsingToolbarLayout.LayoutParams.MATCH_PARENT,
                getResources().getDisplayMetrics().heightPixels / 2
        );

        collapsing_toolbar = view.findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setVisibility(View.GONE);

        imageViewHolder.setLayoutParams(layoutParams);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Add Complaint");

        nestedScrollView = view.findViewById(R.id.nested_scrollview);

        linearLayoutAnalyse = view.findViewById(R.id.layoutAnalyse);
        layout_buttons = view.findViewById(R.id.layout_buttons);
        layout_buttons.setVisibility(View.GONE);

        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setVisibility(View.INVISIBLE);
        buttonSubmit.setVisibility(View.GONE);

        buttonAnalysis = view.findViewById(R.id.button_analysis);
        buttonAnalysis.setVisibility(View.INVISIBLE);
        buttonAnalysis.setVisibility(View.GONE);

        tagsLayout = view.findViewById(R.id.tags_layout);

        viewPager = view.findViewById(R.id.complaint_image_view_pager);
        indicator = view.findViewById(R.id.indicator);

        imageActionButton = view.findViewById(R.id.fabButton);
        imageActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveOptionsToAddImage();
            }
        });

        ImageButton imageButtonAddTags = (ImageButton) view.findViewById(R.id.imageButtonAddTags);

        autoCompleteTextView = (CustomAutoCompleteTextView) view.findViewById(R.id.dynamicAutoCompleteTextView);
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (!(autoCompleteTextView.getText().toString().trim().isEmpty())) {
                        int paddingDp = 60;
                        float density = getContext().getResources().getDisplayMetrics().density;
                        int paddingPixel = (int) (paddingDp * density);
                        linearLayoutAnalyse.setPadding(0, 0, 0, paddingPixel);
                        layout_buttons.setVisibility(View.VISIBLE);
                        buttonSubmit.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.initial_message_file_complaint), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    buttonSubmit.setVisibility(View.INVISIBLE);
                    buttonSubmit.setVisibility(View.GONE);
                    linearLayoutAnalyse.setPadding(0, 0, 0, 0);
                }
            }
        });

        editTextSuggestions = view.findViewById(R.id.editTextSuggestions);

        editTextLocationDetails = view.findViewById(R.id.editTextLocationDetails);

        editTextTags = view.findViewById(R.id.editTextTags);

        editTextTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTags(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageButtonAddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add Tags
                populateTags(editTextTags.getText().toString());
                editTextTags.setText("");
                tagViewPopulate.addTags(new ArrayList<Tag>());
                MainActivity.hideKeyboard(getActivity());
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tags = new ArrayList<>();
                for (int i = 0; i < tagList2.size(); i++) {
                    Tags.add(tagList2.get(i).getName());
                }
                addComplaint();
            }
        });

        buttonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnalysis();
            }
        });

        mMapView = view.findViewById(R.id.google_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        getMapReady();

        //        Autocomplete location bar

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setHint("Enter Location");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                Location = place.getLatLng();
                Name = place.getName().toString();
                Address = place.getAddress().toString();
                updateMap(Location, Name, Address, cursor); //on selecting the place will automatically shows the Details on the map.
                cursor++;
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);

            }
        });
        //        ends here


        tagView = view.findViewById(R.id.tag_view);

        tagView.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView tagView, final Tag tag, final int i) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setMessage("\"" + tag.text + "\" will be deleted. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tagView.remove(i);
                        tagList2.remove(i);
                        Log.i(TAG, "tagList2: " + tagList2.toString());
                        Toast.makeText(getContext(), "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });

        tagViewPopulate = view.findViewById(R.id.tag_populate);

        tagViewPopulate.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int i) {
                editTextTags.setText(tag.text);
                editTextTags.setSelection(tag.text.length()); //to set cursor position
            }
        });

        tagViewPopulate.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView tagView, final Tag tag, final int i) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setMessage("\"" + tag.text + "\" will be deleted. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tagView.remove(i);
                        Toast.makeText(getContext(), "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });
        return view;
    }

    public void getMapReady() {

        Log.i(TAG, "in getMapReady");
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setAllGesturesEnabled(true);
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setMyLocationButtonEnabled(true);
                uiSettings.setIndoorLevelPickerEnabled(true);
                uiSettings.setScrollGesturesEnabled(true);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "No initial permission granted");
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    Log.i(TAG, "Initial Permission Granted");
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            Log.i(TAG, "in onMyLocationButtonClick");
                            locate();
                            return false;
                        }
                    });
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.i(TAG, "lat = " + location.getLatitude() + " lon = " + location.getLongitude());
                                Location = new LatLng(location.getLatitude(), location.getLongitude());
                                updateMap(Location, "Current Location", location.getLatitude() + ", " + location.getLongitude(), cursor);
                            } else {
                                Toast.makeText(getContext(), getString(R.string.getting_current_location), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void locate() {
        Log.i(TAG, "In locate");
        if (!GPSIsSetup) {
            Log.i(TAG, "GPS not enabled");
            displayLocationSettingsRequest();
        } else {
            Log.i(TAG, "GPS enabled");
            try {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.i(TAG, "lat = " + location.getLatitude() + " lon = " + location.getLongitude());
                            Location = new LatLng(location.getLatitude(), location.getLongitude());
                            updateMap(Location, "Current Location", location.getLatitude() + ", " + location.getLongitude(), cursor);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.getting_current_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
                GPSIsSetup = true;
            } catch (SecurityException ignored) {
                Toast.makeText(getContext(), getString(R.string.no_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayLocationSettingsRequest() {
        Log.i(TAG, "In displayLocationSettingsRequest");
        if (getView() == null || getActivity() == null) return;
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity())
                .checkLocationSettings(settingsBuilder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    Log.i(TAG, "In displayLocationSettingsRequest try");
                    LocationSettingsResponse result = task.getResult(ApiException.class);
                    if (result.getLocationSettingsStates().isGpsPresent() &&
                            result.getLocationSettingsStates().isGpsUsable() &&
                            result.getLocationSettingsStates().isLocationPresent() &&
                            result.getLocationSettingsStates().isLocationUsable()) {
                        Log.i(TAG, "In displayLocationSettingsRequest if setupGPS called");
                        setupGPS();
                    }
                } catch (ApiException ex) {
                    Log.i(TAG, "In displayLocationSettingsRequest catch");
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(getActivity(), 87);
                                Log.i(TAG, "In displayLocationSettingsRequest catch case1 try setupGPS called");
                                setupGPS();
                            } catch (IntentSender.SendIntentException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(getContext(), getString(R.string.GPS_not_enables), Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }

    private void setupGPS() {
        Log.i(TAG, "In setup");
        if (getView() == null || getActivity() == null) return;
        // Permissions stuff
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            try {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.i(TAG, "lat = " + location.getLatitude() + " lon = " + location.getLongitude());
                            Location = new LatLng(location.getLatitude(), location.getLongitude());
                            updateMap(Location, "Current Location", location.getLatitude() + ", " + location.getLongitude(), cursor);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.getting_current_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
                GPSIsSetup = true;
            } catch (SecurityException ignored) {
                Toast.makeText(getContext(), getString(R.string.no_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void populateTags(String cs) {
        if (!(cs.isEmpty())) {
            tagList2.add(new TagClass(cs));
            ArrayList<Tag> tags = new ArrayList<>();
            Tag tag;
            for (int i = 0; i < tagList2.size(); i++) {
                tag = new Tag(tagList2.get(i).getName());
                tag.radius = 10f;
                tag.isDeletable = true;
                tags.add(tag);
            }
            tagView.addTags(tags);
        } else {
            Toast.makeText(getContext(), "Please enter some tags", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTags(CharSequence cs) {
        if (!cs.toString().equals("")) {
            String text = cs.toString();
            ArrayList<Tag> tags = new ArrayList<>();
            Tag tag;
            for (int i = 0; i < tagList.size(); i++) {
                if (tagList.get(i).getName().toLowerCase().contains(text.toLowerCase())) {
                    tagsLayout.setVisibility(View.VISIBLE);
                    tag = new Tag(tagList.get(i).getName());
                    tag.radius = 10f;
                    tag.isDeletable = false;
                    tags.add(tag);
                }
            }
            tagViewPopulate.addTags(tags);
        } else {
            tagViewPopulate.addTags(new ArrayList<Tag>());
            return;
        }

        tagsLayout.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }


    private void prepareTags() {
        tagList = new ArrayList<>();
        try {
            for (int i = 0; i < TagCategories.CATEGORIES.length; i++) {
                tagList.add(new TagClass(TagCategories.CATEGORIES[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addComplaint() {
        final String complaint = "Complaint: " + autoCompleteTextView.getText().toString();
        final String suggestion;
        final String locationDetails;
        Log.i(TAG, "Suggestion: " + editTextSuggestions.getText().toString());
        if (!(editTextSuggestions.getText().toString().isEmpty())) {
            suggestion = "\nSuggestion: " + editTextSuggestions.getText().toString();
        } else {
            suggestion = "";
        }
        if (!(editTextLocationDetails.getText().toString().isEmpty())) {
            locationDetails = "\nLocation Details: " + editTextLocationDetails.getText().toString();
        } else {
            locationDetails = "";
        }
        if (Location == null) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response!
            new AlertDialog.Builder(getContext())
                    .setTitle("Location Needed")
                    .setMessage("You have not specified your location. The app will by default make \"IIT Area\" as your location.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Location = new LatLng(19.133810, 72.913257);
                            Address = "IIT Area";
                            ComplaintCreateRequest complaintCreateRequest = new ComplaintCreateRequest(complaint + suggestion + locationDetails, Address, (float) Location.latitude, (float) Location.longitude, Tags, uploadedImagesUrl);
                            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                            retrofitInterface.postComplaint("sessionid=" + getArguments().getString(Constants.SESSION_ID), complaintCreateRequest).enqueue(new Callback<ComplaintCreateResponse>() {
                                @Override
                                public void onResponse(Call<ComplaintCreateResponse> call, Response<ComplaintCreateResponse> response) {
                                    Toast.makeText(getContext(), "Complaint successfully posted", Toast.LENGTH_LONG).show();
                                    Bundle bundle = getArguments();
                                    bundle.putString(Constants.USER_ID, userId);
                                    ComplaintFragment complaintFragment = new ComplaintFragment();
                                    complaintFragment.setArguments(bundle);
                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.framelayout_for_fragment, complaintFragment, complaintFragment.getTag());
                                    transaction.addToBackStack(complaintFragment.getTag());
                                    manager.popBackStackImmediate("Complaint Fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    transaction.commit();
                                }

                                @Override
                                public void onFailure(Call<ComplaintCreateResponse> call, Throwable t) {
                                    Log.i(TAG, "failure in addComplaint: " + t.toString());
                                    Toast.makeText(getContext(), "Complaint Creation Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "Submission aborted", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        } else {
            ComplaintCreateRequest complaintCreateRequest = new ComplaintCreateRequest(complaint + suggestion + locationDetails, Address, (float) Location.latitude, (float) Location.longitude, Tags, uploadedImagesUrl);
            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
            retrofitInterface.postComplaint("sessionid=" + getArguments().getString(Constants.SESSION_ID), complaintCreateRequest).enqueue(new Callback<ComplaintCreateResponse>() {
                @Override
                public void onResponse(Call<ComplaintCreateResponse> call, Response<ComplaintCreateResponse> response) {
                    Toast.makeText(getContext(), "Complaint successfully posted", Toast.LENGTH_LONG).show();
                    Bundle bundle = getArguments();
                    bundle.putString(Constants.USER_ID, userId);
                    ComplaintFragment complaintFragment = new ComplaintFragment();
                    complaintFragment.setArguments(bundle);
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.framelayout_for_fragment, complaintFragment, complaintFragment.getTag());
                    transaction.addToBackStack(complaintFragment.getTag());
                    manager.popBackStackImmediate("Complaint Fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commit();
                }

                @Override
                public void onFailure(Call<ComplaintCreateResponse> call, Throwable t) {
                    Log.i(TAG, "failure in addComplaint: " + t.toString());
                    Toast.makeText(getContext(), "Complaint Creation Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateMap(LatLng Location, String Name, String Address, int cursor) {
        Log.i(TAG, "In updateMap");
        LocationAPIUtils locationAPIUtils = new LocationAPIUtils(googleMap, mMapView);
        locationAPIUtils.callGoogleToShowLocationOnMap(Location, Name, Address, cursor);
        showAnalysis();
    }

    private void showAnalysis() {
        /* Machine Learning Part */
    }

    private void giveOptionsToAddImage() {
        final CharSequence[] items = {getString(R.string.take_photo_using_camera), getString(R.string.choose_from_library)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo_using_camera))) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        return;
                    }
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, Constants.REQUEST_CAMERA_INT_ID);
                    }
                } else if (items[item].equals(getString(R.string.choose_from_library))) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        return;
                    }
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");

                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CAMERA_INT_ID && data != null) {
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            base64Image = convertImageToString(bitmap);
            collapsing_toolbar.setVisibility(View.VISIBLE);
            sendImage();

        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.RESULT_LOAD_IMAGE && data != null) {
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            base64Image = convertImageToString(getScaledBitmap(picturePath, 800, 800));
            collapsing_toolbar.setVisibility(View.VISIBLE);
            sendImage();
        }
    }

    private void sendImage() {
        progressDialog.setMessage("Uploading Image");
        ImageUploadRequest imageUploadRequest = new ImageUploadRequest(base64Image);
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.uploadImage("sessionid=" + getArguments().getString(Constants.SESSION_ID), imageUploadRequest).enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful()) {
                    ImageUploadResponse imageUploadResponse = response.body();
                    uploadedImagesUrl.add(imageUploadResponse.getPictureURL());
                    showImage(uploadedImagesUrl);
                    Log.i(TAG, "ImageURL: " + uploadedImagesUrl.toString());
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                Log.i(TAG, "failure in sendImage: " + t.toString());
                progressDialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showImage(List<String> uploadedImagesUrl) {

        if (viewPager != null) {
            try {
                imageViewPagerAdapter = new ImageViewPagerAdapter(getFragmentManager(), uploadedImagesUrl);
                collapsing_toolbar.setVisibility(View.VISIBLE);
                viewPager.setAdapter(imageViewPagerAdapter);
                indicator.setViewPager(viewPager);
                imageViewPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());
                viewPager.getAdapter().notifyDataSetChanged();
                synchronized (viewPager) {
                    viewPager.notifyAll();
                }
                imageViewPagerAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Picture Taken", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculaInSampleSize(sizeOptions, width, height);
        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculaInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    private String convertImageToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            return null;
        }
    }
}