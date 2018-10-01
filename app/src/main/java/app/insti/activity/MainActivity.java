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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.SessionManager;
import app.insti.Utils;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.Notification;
import app.insti.api.model.Role;
import app.insti.api.model.User;
import app.insti.api.request.UserFCMPatchRequest;
import app.insti.fragment.BackHandledFragment;
import app.insti.fragment.BodyFragment;
import app.insti.fragment.CalendarFragment;
import app.insti.fragment.EventFragment;
import app.insti.fragment.ExploreFragment;
import app.insti.fragment.FeedFragment;
import app.insti.fragment.MapFragment;
import app.insti.fragment.MessMenuFragment;
import app.insti.fragment.NewsFragment;
import app.insti.fragment.NotificationsFragment;
import app.insti.fragment.PlacementBlogFragment;
import app.insti.fragment.QuickLinksFragment;
import app.insti.fragment.SettingsFragment;
import app.insti.fragment.TrainingBlogFragment;
import app.insti.fragment.UserFragment;
import retrofit2.Call;
import retrofit2.Response;

import static app.insti.Constants.DATA_TYPE_BODY;
import static app.insti.Constants.DATA_TYPE_EVENT;
import static app.insti.Constants.DATA_TYPE_NEWS;
import static app.insti.Constants.DATA_TYPE_PT;
import static app.insti.Constants.DATA_TYPE_USER;
import static app.insti.Constants.FCM_BUNDLE_NOTIFICATION_ID;
import static app.insti.Constants.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION;
import static app.insti.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static app.insti.Constants.RESULT_LOAD_IMAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BackHandledFragment.BackHandlerInterface {


    private static final String TAG = "MainActivity";
    SessionManager session;
    FeedFragment feedFragment;
    private User currentUser;
    private BackHandledFragment selectedFragment;
    private Menu menu;
    private RetrofitInterface retrofitInterface;
    private List<Notification> notifications = null;

    /** which menu item should be checked on activity start */
    private int initMenuChecked = R.id.nav_feed;

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

        ServiceGenerator serviceGenerator = new ServiceGenerator(getApplicationContext());
        Utils.setRetrofitInterface(serviceGenerator.getRetrofitInterface());

        /* Make notification channel on oreo */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            Utils.setSessionId(session.getSessionID());
        }

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
            // Check for data passed by FCM
            if (intent.getExtras() != null && intent.getBundleExtra(Constants.MAIN_INTENT_EXTRAS) != null) {
                handleFCMIntent(intent.getBundleExtra(Constants.MAIN_INTENT_EXTRAS));
            } else {
                handleIntent(intent);
            }
        }

        checkLatestVersion();
    }

    /** Get the notifications from memory cache or network */
    private void fetchNotifications() {
        // Try memory cache
        if (notifications != null) {
            showNotifications();
            return;
        }

        // Get from network
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getNotifications(Utils.getSessionIDHeader()).enqueue(new EmptyCallback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    notifications = response.body();
                    showNotifications();
                }
            }
        });
    }

    /** Show the right notification icon */
    private void showNotifications() {
        if (notifications != null && !notifications.isEmpty()) {
            menu.findItem(R.id.action_notifications).setIcon(R.drawable.baseline_notifications_active_white_24);
        } else {
            menu.findItem(R.id.action_notifications).setIcon(R.drawable.ic_notifications_white_24dp);
        }
    }

    /** Get version code we are currently on */
    private int getCurrentVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
            return 0;
        }
    }

    /** Check for updates in andro.json */
    private void checkLatestVersion() {
        final int versionCode = getCurrentVersion();
        if (versionCode == 0) { return; }
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getLatestVersion().enqueue(new EmptyCallback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    final JsonElement currentVersion = response.body().get("version");
                    if (currentVersion != null && currentVersion.getAsInt() > versionCode) {
                        showUpdateSnackBar(response.body().get("message").getAsString());
                    }
                }
            }
        });
    }

    private void showUpdateSnackBar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).setAction("UPDATE", new View.OnClickListener() {
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
        mChannel.setLightColor(Color.BLUE);

        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{0, 200});

        mNotificationManager.createNotificationChannel(mChannel);
    }

    /** Handle opening event/body/blog from FCM notification */
    private void handleFCMIntent(Bundle bundle) {
        /* Mark the notification read */
        final String notificationId = bundle.getString(FCM_BUNDLE_NOTIFICATION_ID);
        if (notificationId != null) {
            Utils.getRetrofitInterface().markNotificationRead(Utils.getSessionIDHeader(), notificationId).enqueue(new EmptyCallback<Void>());
        }

        /* Follow the notification */
        chooseIntent(
                bundle.getString(Constants.FCM_BUNDLE_TYPE),
                bundle.getString(Constants.FCM_BUNDLE_ID),
                bundle.getString(Constants.FCM_BUNDLE_EXTRA)
        );
    }

    /** Handle intents for links */
    private void handleIntent(Intent appLinkIntent) {
        String appLinkAction = appLinkIntent.getAction();
        String appLinkData = appLinkIntent.getDataString();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            chooseIntent(getType(appLinkData), getID(appLinkData));
        }
    }

    /** Open the proper fragment from given type and id */
    private void chooseIntent(String type, String id) {
        if (type == null || id == null) { return; }
        switch (type) {
            case DATA_TYPE_BODY:
                openBodyFragment(id);
                return;
            case DATA_TYPE_USER:
                openUserFragment(id);
                return;
            case DATA_TYPE_EVENT:
                openEventFragment(id);
                return;
            case DATA_TYPE_NEWS:
                initMenuChecked = R.id.nav_news;
                updateFragment(new NewsFragment());
                return;
        }
        Log.e("NOTIFICATIONS", "Server sent invalid notification?");
    }

    /** Open the proper fragment from given type, id and extra */
    private void chooseIntent(String type, String id, String extra) {
        if (extra == null) {
            chooseIntent(type, id);
        } else {
            switch (type) {
                case DATA_TYPE_PT:
                    if (extra.contains("/trainingblog")) {
                        initMenuChecked = R.id.nav_training_blog;
                        openTrainingBlog();
                    } else {
                        initMenuChecked = R.id.nav_placement_blog;
                        openPlacementBlog();
                    }
                    return;
            }
            chooseIntent(type, id);
        }
    }

    /** Open user fragment from given id */
    private void openUserFragment(String id) {
        UserFragment userFragment = UserFragment.newInstance(id);
        updateFragment(userFragment);
    }

    /** Open the body fragment from given id */
    private void openBodyFragment(String id) {
        Utils.openBodyFragment(new Body(id), this);
    }

    /** Open the event fragment from the provided id */
    private void openEventFragment(String id) {
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        final FragmentActivity self = this;
        retrofitInterface.getEvent(Utils.getSessionIDHeader(), id).enqueue(new EmptyCallback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Utils.openEventFragment(response.body(), self);
            }
        });
    }

    private String getID(String appLinkData) {
        if (appLinkData.charAt(appLinkData.length() - 1) == '/')
            appLinkData = appLinkData.substring(0, appLinkData.length() - 1);
        switch (getType(appLinkData)) {
            case DATA_TYPE_BODY:
                return appLinkData.substring(appLinkData.indexOf("org") + 4);
            case DATA_TYPE_USER:
                return appLinkData.substring(appLinkData.indexOf("user") + 5);
            case DATA_TYPE_EVENT:
                return appLinkData.substring(appLinkData.indexOf("event") + 6);
        }
        return null;
    }

    private String getType(String appLinkData) {
        if (appLinkData.contains("://insti.app/org/")) {
            return DATA_TYPE_BODY;
        } else if (appLinkData.contains("://insti.app/user/")) {
            return DATA_TYPE_USER;
        } else if (appLinkData.contains("://insti.app/event/")) {
            return DATA_TYPE_EVENT;
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
                final String fcmId = instanceIdResult.getToken();
                RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();

                retrofitInterface.patchUserMe(Utils.getSessionIDHeader(), new UserFCMPatchRequest(fcmId, getCurrentVersion())).enqueue(new EmptyCallback<User>() {
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
                });
            }
        });
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(initMenuChecked);
    }

    private void updateNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserFragment(currentUser.getUserID());
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
        getMenuInflater().inflate(R.menu.main, this.menu);

        // Fetch notifictions if logged in or hide icon
        if (session.isLoggedIn()) {
            fetchNotifications();
        } else {
            this.menu.findItem(R.id.action_notifications).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_notifications) {
            NotificationsFragment notificationsFragment = new NotificationsFragment();
            updateFragment(notificationsFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_feed:
                feedFragment = new FeedFragment();
                updateFragment(feedFragment);
                break;

            case R.id.nav_explore:
                updateFragment(ExploreFragment.newInstance());
                break;

            case R.id.nav_news:
                updateFragment(new NewsFragment());
                break;

            case R.id.nav_placement_blog:
                openPlacementBlog();
                break;
            case R.id.nav_training_blog:
                openTrainingBlog();
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

            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                updateFragment(settingsFragment);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Open placement blog fragment */
    private void openPlacementBlog() {
        if (session.isLoggedIn()) {
            PlacementBlogFragment placementBlogFragment = new PlacementBlogFragment();
            updateFragment(placementBlogFragment);
        } else {
            Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    private void openTrainingBlog() {
        if (session.isLoggedIn()) {
            TrainingBlogFragment trainingBlogFragment = new TrainingBlogFragment();
            updateFragment(trainingBlogFragment);
        } else {
            Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    /** Change the active fragment to the supplied one */
    public void updateFragment(Fragment fragment) {
        Log.d(TAG, "updateFragment: " + fragment.toString());
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(Constants.SESSION_ID, session.pref.getString(Constants.SESSION_ID, ""));
        if (fragment instanceof MessMenuFragment)
            bundle.putString(Constants.USER_HOSTEL, session.isLoggedIn() && currentUser.getHostel() != null ? currentUser.getHostel() : "1");
        if (fragment instanceof SettingsFragment && session.isLoggedIn())
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
        }
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
