package app.insti.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.SessionManager;
import app.insti.UpdatableList;
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
import app.insti.fragment.CalendarFragment;
import app.insti.fragment.ComplaintsFragment;
import app.insti.fragment.ExploreFragment;
import app.insti.fragment.FeedFragment;
import app.insti.fragment.FileComplaintFragment;
import app.insti.fragment.MapFragment;
import app.insti.fragment.MessMenuFragment;
import app.insti.fragment.NewsFragment;
import app.insti.fragment.NotificationsFragment;
import app.insti.fragment.PlacementBlogFragment;
import app.insti.fragment.QuickLinksFragment;
import app.insti.fragment.SearchFragment;
import app.insti.fragment.SettingsFragment;
import app.insti.fragment.TrainingBlogFragment;
import app.insti.fragment.UserFragment;
import app.insti.fragment.WebViewFragment;
import app.insti.notifications.NotificationId;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Response;

import static app.insti.Constants.DATA_TYPE_BODY;
import static app.insti.Constants.DATA_TYPE_EVENT;
import static app.insti.Constants.DATA_TYPE_MAP;
import static app.insti.Constants.DATA_TYPE_MESS;
import static app.insti.Constants.DATA_TYPE_NEWS;
import static app.insti.Constants.DATA_TYPE_PT;
import static app.insti.Constants.DATA_TYPE_USER;
import static app.insti.Constants.FCM_BUNDLE_NOTIFICATION_ID;
import static app.insti.Constants.MY_PERMISSIONS_REQUEST_LOCATION;
import static app.insti.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static app.insti.Constants.RESULT_LOAD_IMAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BackHandledFragment.BackHandlerInterface {


    private static final String TAG = "MainActivity";
    private SessionManager session;
    private FeedFragment feedFragment;
    private User currentUser;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Check for dark theme */
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        Utils.isDarkTheme = sharedPref.getBoolean(Constants.DARK_THEME, false);
        if (Utils.isDarkTheme)
            this.setTheme(R.style.AppThemeDark);

        ServiceGenerator serviceGenerator = new ServiceGenerator(getApplicationContext());
        Utils.setRetrofitInterface(serviceGenerator.getRetrofitInterface());

        Utils.makeGson();
        Utils.makeMarkwon(getApplicationContext());

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

    /**
     * Get the notifications from memory cache or network
     */
    private void fetchNotifications() {
        // Try memory cache
        if (Utils.notificationCache != null) {
            showNotifications();
            return;
        }

        // Get from network
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getNotifications(Utils.getSessionIDHeader()).enqueue(new EmptyCallback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    Utils.notificationCache = new UpdatableList<>();
                    Utils.notificationCache.setList(response.body());
                    showNotifications();

                    NotificationId.setCurrentCount(Utils.notificationCache.size());
                    ShortcutBadger.applyCount(getApplicationContext(), NotificationId.getCurrentCount());
                }
            }
        });
    }

    /**
     * Show the right notification icon
     */
    private void showNotifications() {
        if (Utils.notificationCache != null && !Utils.notificationCache.isEmpty()) {
            menu.findItem(R.id.action_notifications).setIcon(R.drawable.baseline_notifications_active_white_24);
        } else {
            menu.findItem(R.id.action_notifications).setIcon(R.drawable.ic_notifications_white_24dp);
        }
    }

    /**
     * Get version code we are currently on
     */
    private int getCurrentVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
            return 0;
        }
    }

    /**
     * Check for updates in andro.json
     */
    private void checkLatestVersion() {
        final int versionCode = getCurrentVersion();
        if (versionCode == 0) {
            return;
        }
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

    /**
     * Handle opening event/body/blog from FCM notification
     */
    private void handleFCMIntent(Bundle bundle) {
        /* Mark the notification read */
        final String notificationId = bundle.getString(FCM_BUNDLE_NOTIFICATION_ID);
        if (notificationId != null) {
            Utils.getRetrofitInterface().markNotificationRead(Utils.getSessionIDHeader(), notificationId).enqueue(new EmptyCallback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    ShortcutBadger.applyCount(getApplicationContext(), NotificationId.decrementAndGetCurrentCount());
                }
            });
        }

        /* Follow the notification */
        chooseIntent(
                bundle.getString(Constants.FCM_BUNDLE_TYPE),
                bundle.getString(Constants.FCM_BUNDLE_ID),
                bundle.getString(Constants.FCM_BUNDLE_EXTRA)
        );
    }

    /**
     * Handle intents for links
     */
    private void handleIntent(Intent appLinkIntent) {
        String appLinkAction = appLinkIntent.getAction();
        String appLinkData = appLinkIntent.getDataString();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            chooseIntent(getType(appLinkData), getID(appLinkData));
        }
    }

    /**
     * Open the proper fragment from given type and id
     */
    private void chooseIntent(String type, String id) {
        if (type == null || (!type.equals(DATA_TYPE_MESS) && id == null)) {
            return;
        }
        switch (type) {
            case DATA_TYPE_BODY:
                Utils.openBodyFragment(new Body(id), this);
                return;
            case DATA_TYPE_USER:
                Utils.openUserFragment(id, this);
                return;
            case DATA_TYPE_EVENT:
                openEventFragment(id);
                return;
            case DATA_TYPE_NEWS:
                updateFragment((new NewsFragment()).withId(id));
                return;
            case DATA_TYPE_MAP:
                updateFragment(MapFragment.newInstance(id));
                return;
            case DATA_TYPE_MESS:
                updateFragment(new MessMenuFragment());
                return;
        }
        Log.e("NOTIFICATIONS", "Server sent invalid notification?");
    }

    /**
     * Open the proper fragment from given type, id and extra
     */
    private void chooseIntent(String type, String id, String extra) {
        if (extra == null) {
            chooseIntent(type, id);
        } else {
            switch (type) {
                case DATA_TYPE_PT:
                    if (extra.contains("/trainingblog")) {
                        openTrainingBlog(id);
                    } else {
                        openPlacementBlog(id);
                    }
                    return;
            }
            chooseIntent(type, id);
        }
    }

    /**
     * Open the event fragment from the provided id
     */
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
        try {
            /* Parse URL and get second part */
            String[] parts = new URL(appLinkData).getPath().split("/");
            if (parts.length >= 3) {
                return parts[2];
            }
        } catch (MalformedURLException ignored) {}
        return null;
    }

    private String getType(String appLinkData) {
        try {
            /* Parse URL and check length */
            String[] parts = new URL(appLinkData).getPath().split("/");
            if (parts.length < 2) return null;

            /* Map to proper data type */
            switch (parts[1].toLowerCase()) {
                case "org":
                    return DATA_TYPE_BODY;
                case "event":
                    return DATA_TYPE_EVENT;
                case "user":
                    return DATA_TYPE_USER;
                case "map":
                    return DATA_TYPE_MAP;
                case "mess":
                    return DATA_TYPE_MESS;
            }
        } catch (MalformedURLException ignored) {}
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        initNavigationView();
        if (session.isLoggedIn()) {
            currentUser = session.getCurrentUser();
            updateNavigationView();
            updateFCMId();
        }
    }

    @Override
    protected void onDestroy() {
        Utils.eventCache.clear();
        Utils.notificationCache = null;
        super.onDestroy();
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
                            Utils.currentUserCache = currentUser;
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
    }

    private void updateNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openUserFragment(currentUser.getUserID(), MainActivity.this);
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
            notificationsFragment.show(getSupportFragmentManager(), TAG);
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
            case R.id.nav_achievements:
                WebViewFragment webViewFragment = new WebViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.WV_TYPE, Constants.WV_TYPE_ACHIEVEMENTS);
                webViewFragment.setArguments(bundle);
                updateFragment(webViewFragment);
                break;
            case R.id.nav_search:
                openSearch();
                break;

            case R.id.nav_complaint:
                if (session.isLoggedIn()) {
                    ComplaintsFragment complaintsFragment = new ComplaintsFragment();
                    updateFragment(complaintsFragment);
                } else {
                    Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                updateFragment(settingsFragment);
                break;

            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Open placement blog fragment
     */
    private void openPlacementBlog() {
        openPlacementBlog(null);
    }

    private void openPlacementBlog(String id) {
        if (session.isLoggedIn()) {
            PlacementBlogFragment placementBlogFragment = new PlacementBlogFragment();
            if (id != null) placementBlogFragment.withId(id);
            updateFragment(placementBlogFragment);
        } else {
            Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    private void openTrainingBlog() {
        openTrainingBlog(null);
    }

    private void openTrainingBlog(String id) {
        if (session.isLoggedIn()) {
            TrainingBlogFragment trainingBlogFragment = new TrainingBlogFragment();
            if (id != null) trainingBlogFragment.withId(id);
            updateFragment(trainingBlogFragment);
        } else {
            Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }
    private void openSearch(){
        openSearch(null);
    }
    private void openSearch(String id) {
//        if (session.isLoggedIn()) {
            SearchFragment searchFragment = new SearchFragment();
            if (id != null) searchFragment.withId(id);
            updateFragment(searchFragment);
//        } else {
//            Toast.makeText(this, Constants.LOGIN_MESSAGE, Toast.LENGTH_LONG).show();
//        }
    }

    /**
     * Change the active fragment to the supplied one
     */
    public void updateFragment(Fragment fragment) {
        if (session.isLoggedIn() && currentUser == null) {
            currentUser = session.getCurrentUser();
        }
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(Constants.SESSION_ID, session.pref.getString(Constants.SESSION_ID, ""));
        if (fragment instanceof MessMenuFragment)
            bundle.putString(Constants.USER_HOSTEL, session.isLoggedIn() && currentUser.getHostel() != null ? currentUser.getHostel() : "1");
        if (fragment instanceof SettingsFragment && session.isLoggedIn())
            bundle.putString(Constants.USER_ID, currentUser.getUserID());
        if (fragment instanceof ComplaintsFragment && session.isLoggedIn()) {
            bundle.putString(Constants.USER_ID, currentUser.getUserID());
            bundle.putString(Constants.CURRENT_USER_PROFILE_PICTURE, currentUser.getUserProfilePictureUrl());
        }
        fragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        if (fragment instanceof FeedFragment)
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = manager.beginTransaction();

        /* Animate only for UserFragment */
        if (fragment instanceof UserFragment) {
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_down);
        }

        transaction.replace(R.id.framelayout_for_fragment, fragment, Utils.getTag(fragment));
        transaction.addToBackStack(Utils.getTag(fragment)).commit();
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
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Map
                    MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.TAG);
                    if (mapFragment != null && mapFragment.isVisible()) {
                        mapFragment.setupGPS(true);
                    }

                    // File complaint
                    FileComplaintFragment fileComplaintFragment = (FileComplaintFragment) getSupportFragmentManager().findFragmentByTag(FileComplaintFragment.TAG);
                    if (fileComplaintFragment != null && fileComplaintFragment.isVisible()) {
                        fileComplaintFragment.getMapReady();
                    }
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
        return (currentUser != null && currentUser.getUserRoles() != null && currentUser.getUserRoles().size() > 0);
    }

    public boolean editEventAccess(Event event) {
        if (!createEventAccess())
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
        if (!createEventAccess())
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
