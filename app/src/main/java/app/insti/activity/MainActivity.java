package app.insti.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.SessionManager;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.data.Body;
import app.insti.data.Event;
import app.insti.data.Notification;
import app.insti.data.Role;
import app.insti.data.User;
import app.insti.fragment.BackHandledFragment;
import app.insti.fragment.BodyFragment;
import app.insti.fragment.CalendarFragment;
import app.insti.fragment.ComplaintFragment;
import app.insti.fragment.EventFragment;
import app.insti.fragment.ExploreFragment;
import app.insti.fragment.FeedFragment;
import app.insti.fragment.FileComplaintFragment;
import app.insti.fragment.MapFragment;
import app.insti.fragment.MessMenuFragment;
import app.insti.fragment.MyEventsFragment;
import app.insti.fragment.NewsFragment;
import app.insti.fragment.NotificationsFragment;
import app.insti.fragment.PlacementBlogFragment;
import app.insti.fragment.UserFragment;
import app.insti.fragment.QuickLinksFragment;
import app.insti.fragment.SettingsFragment;
import app.insti.fragment.TrainingBlogFragment;
import app.insti.notifications.NotificationEventReceiver;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static app.insti.Constants.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION;
import static app.insti.Constants.MY_PERMISSIONS_REQUEST_LOCATION;
import static app.insti.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static app.insti.Constants.RESULT_LOAD_IMAGE;
import static app.insti.notifications.NotificationIntentService.ACTION_OPEN_EVENT;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BackHandledFragment.BackHandlerInterface {


    private static final String TAG = "MainActivity";
    SessionManager session;
    FeedFragment feedFragment;
    private User currentUser;
    private boolean showNotifications = false;
    private BackHandledFragment selectedFragment;
    private Menu menu;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initPicasso();
        } catch (IllegalStateException ignored) {
        }

        /* Make notification channel on oreo */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        feedFragment = new FeedFragment();
        updateFragment(feedFragment);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_OPEN_EVENT)) {
                EventFragment eventFragment = new EventFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EVENT_JSON, intent.getStringExtra(Constants.EVENT_JSON));
                eventFragment.setArguments(bundle);
                updateFragment(eventFragment);
            } else {
                handleIntent(intent);
            }
        }

        fetchNotifications();

        checkLatestVersion();

        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }

    private void fetchNotifications() {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getNotifications(getSessionIDHeader()).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    List<Notification> notifications = response.body();
                    if (notifications != null && !notifications.isEmpty()) {
                        menu.findItem(R.id.action_notifications).setIcon(R.drawable.baseline_notifications_active_white_24);
                    } else {
                        menu.findItem(R.id.action_notifications).setIcon(R.drawable.ic_notifications_white_24dp);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
            }
        });
    }

    private void checkLatestVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            final int versionCode = pInfo.versionCode;
            RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
            retrofitInterface.getLatestVersion().enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body().get("version").getAsInt() > versionCode) {
                            showUpdateSnackBar();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        } catch (PackageManager.NameNotFoundException ignored) {

        }
    }

    private void showUpdateSnackBar() {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "New Version Available", Snackbar.LENGTH_LONG).setAction("UPDATE", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        }).show();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // The id of the channel.
        String id = getResources().getString(R.string.default_notification_channel_id);

        // The user-visible name of the channel.
        CharSequence name = "InstiApp";

        // The user-visible description of the channel.
        String description = "InstiApp Notifications";

        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel mChannel = null;
        mChannel = new NotificationChannel(id, name, importance);

        // Configure the notification channel.
        mChannel.setDescription(description);

        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);

        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        mNotificationManager.createNotificationChannel(mChannel);
    }

    private void handleIntent(Intent appLinkIntent) {
        String appLinkAction = appLinkIntent.getAction();
        String appLinkData = appLinkIntent.getDataString();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            switch (getType(appLinkData)) {
                case "body":
                    Body body = new Body(getID(appLinkData));
                    BodyFragment bodyFragment = BodyFragment.newInstance(body);
                    updateFragment(bodyFragment);
                    break;
                case "user":
                    UserFragment userFragment = UserFragment.newInstance(getID(appLinkData));
                    updateFragment(userFragment);
                    break;
                case "event":
                    RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
                    retrofitInterface.getEvent(getSessionIDHeader(), getID(appLinkData)).enqueue(new Callback<Event>() {
                        @Override
                        public void onResponse(Call<Event> call, Response<Event> response) {
                            EventFragment eventFragment = new EventFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.EVENT_JSON, response.body().toString());
                            eventFragment.setArguments(bundle);
                            updateFragment(eventFragment);
                        }

                        @Override
                        public void onFailure(Call<Event> call, Throwable t) {

                        }
                    });
            }
        }
    }

    private String getID(String appLinkData) {
        if (appLinkData.charAt(appLinkData.length() - 1) == '/')
            appLinkData = appLinkData.substring(0, appLinkData.length() - 1);
        switch (getType(appLinkData)) {
            case "body":
                return appLinkData.substring(appLinkData.indexOf("org") + 4);
            case "user":
                return appLinkData.substring(appLinkData.indexOf("user") + 5);
            case "event":
                return appLinkData.substring(appLinkData.indexOf("event") + 6);
        }
        return null;
    }

    private String getType(String appLinkData) {
        if (appLinkData.startsWith("http://insti.app/org/") || appLinkData.startsWith("https://insti.app/org/")) {
            return "body";
        } else if (appLinkData.startsWith("http://insti.app/user/") || appLinkData.startsWith("https://insti.app/user/")) {
            return "user";
        } else if (appLinkData.startsWith("http://insti.app/event/") || appLinkData.startsWith("https://insti.app/event/")) {
            return "event";
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        initNavigationView();
        if (session.isLoggedIn()) {
            currentUser = User.fromString(session.pref.getString(Constants.CURRENT_USER, ""));
            updateNavigationView();
            updateFCMId();
        }
    }

    /**
     * Update FCM Id and update profile
     */
    private void updateFCMId() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String fcmId = instanceIdResult.getToken();
                RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
                retrofitInterface.getUserMe(getSessionIDHeader(), fcmId).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            session.createLoginSession(response.body().getUserName(), response.body(), session.getSessionID());
                            currentUser = response.body();
                        } else {
                            session.logout();
                            currentUser = null;
                            Toast.makeText(MainActivity.this, "Your session has expired!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    }
                });
            }
        });
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_feed);
    }

    private void updateNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.USER_ID, currentUser.getUserID());
                UserFragment userFragment = new UserFragment();
                userFragment.setArguments(bundle);
                updateFragment(userFragment);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        TextView nameTextView = header.findViewById(R.id.user_name_nav_header);
        TextView rollNoTextView = header.findViewById(R.id.user_rollno_nav_header);
        ImageView profilePictureImageView = header.findViewById(R.id.user_profile_picture_nav_header);
        nameTextView.setText(currentUser.getUserName());
        rollNoTextView.setText(currentUser.getUserRollNumber());

        Picasso.get()
                .load(currentUser.getUserProfilePictureUrl())
                .resize(200, 0)
                .placeholder(R.drawable.user_placeholder)
                .into(profilePictureImageView);
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (feedFragment != null && feedFragment.isVisible()) {
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        fetchNotifications();
        getMenuInflater().inflate(R.menu.main, this.menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_notifications) {
            showNotifications = true;
            NotificationsFragment notificationsFragment = new NotificationsFragment();
            updateFragment(notificationsFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_feed:
                feedFragment = new FeedFragment();
                updateFragment(feedFragment);
                break;
            case R.id.nav_my_events:
                if (session.isLoggedIn()) {
                    MyEventsFragment myeventsFragment = new MyEventsFragment();
                    updateFragment(myeventsFragment);
                } else {
                    Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.nav_explore:
                updateFragment(ExploreFragment.newInstance());
                break;

            case R.id.nav_news:
                NewsFragment newsFragment = new NewsFragment();
                updateFragment(newsFragment);
                break;

            case R.id.nav_placement_blog:
                if (session.isLoggedIn()) {
                    PlacementBlogFragment placementBlogFragment = new PlacementBlogFragment();
                    updateFragment(placementBlogFragment);
                } else {
                    Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.nav_training_blog:
                if (session.isLoggedIn()) {
                    TrainingBlogFragment trainingBlogFragment = new TrainingBlogFragment();
                    updateFragment(trainingBlogFragment);
                } else {
                    Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.nav_mess_menu:
                MessMenuFragment messMenuFragment = new MessMenuFragment();
                updateFragment(messMenuFragment);
                break;
            case R.id.nav_calendar:
                CalendarFragment calendarFragment = new CalendarFragment();
                updateFragment(calendarFragment);
                break;
            case R.id.nav_qlinks:
                QuickLinksFragment quickLinksFragment = new QuickLinksFragment();
                updateFragment(quickLinksFragment);
                break;
            case R.id.nav_map:
                MapFragment mapFragment = new MapFragment();
                updateFragment(mapFragment);
                break;
            case R.id.nav_complaint:
                if (session.isLoggedIn()) {
                    ComplaintFragment complaintFragment = new ComplaintFragment();
                    updateFragment(complaintFragment);
                } else {
                    Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                updateFragment(settingsFragment);
                //Checking the about fragment
                //AboutFragment aboutFragment = new AboutFragment();
                //updateFragment(aboutFragment);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateFragment(Fragment fragment) {
        Log.i(TAG, "updateFragment: " + fragment.toString());
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(Constants.SESSION_ID, session.pref.getString(Constants.SESSION_ID, ""));
        if (fragment instanceof MessMenuFragment)
            bundle.putString(Constants.USER_HOSTEL, session.isLoggedIn() && currentUser.getHostel() != null ? currentUser.getHostel() : "1");
        if (fragment instanceof SettingsFragment && session.isLoggedIn())
            bundle.putString(Constants.USER_ID, currentUser.getUserID());
        if (fragment instanceof ComplaintFragment && session.isLoggedIn())
            bundle.putString(Constants.USER_ID, currentUser.getUserID());
        fragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        if (fragment instanceof FeedFragment)
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = manager.beginTransaction();

        /* Animate only for UserFragment */
        if (fragment instanceof UserFragment) {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        }

        transaction.replace(R.id.framelayout_for_fragment, fragment, fragment.getTag());
        transaction.addToBackStack(fragment.getTag()).commit();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
                return;
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MapFragment.getMainActivity().setupGPS();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Need Permission", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION:
                Log.i(TAG, "@@@@@@@@@@@@@@ Permission request captured");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "@@@@@@@@@@@@@@ Permission Granted");
                    FileComplaintFragment.getMainActivity().getMapReady();
                } else {
                    Log.i(TAG, "@@@@@@@@@@@@@@ Permission Cancelled");
                    Toast toast = Toast.makeText(MainActivity.this, "Need Permission", Toast.LENGTH_SHORT);
                    toast.show();
                }
        }
    }


    public String getSessionIDHeader() {
        return "sessionid=" + session.getSessionID();
    }

    public void initPicasso() {
        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        builder.downloader(new com.squareup.picasso.OkHttp3Downloader((
                new OkHttpClient.Builder().build()
        )));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
            for (Fragment subfragment : fragment.getChildFragmentManager().getFragments()) {
                subfragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public boolean createEventAccess() {
        if (currentUser == null || currentUser.getUserRoles() == null || currentUser.getUserRoles().size() == 0)
            return false;
        return true;
    }

    public boolean editEventAccess(Event event) {
        if (currentUser == null || currentUser.getUserRoles() == null || currentUser.getUserRoles().size() == 0)
            return false;

        for (Role role : currentUser.getUserRoles()) {
            for (Body body : role.getRoleBodies()) {
                for (Body eventBody : event.getEventBodies()) {
                    if (body.getBodyID().equals(eventBody.getBodyID())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean editBodyAccess(Body toEditBody) {
        if (currentUser == null || currentUser.getUserRoles() == null || currentUser.getUserRoles().size() == 0)
            return false;

        for (Role role : currentUser.getUserRoles()) {
            for (Body body : role.getRoleBodies()) {
                if (body.getBodyID().equals(toEditBody.getBodyID())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.selectedFragment = backHandledFragment;
    }
}
