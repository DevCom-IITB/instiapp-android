package app.insti;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import app.insti.api.UnsafeOkHttpClient;
import app.insti.api.model.NotificationsResponse;
import app.insti.data.User;
import app.insti.fragment.CalendarFragment;
import app.insti.fragment.FeedFragment;
import app.insti.fragment.MapFragment;
import app.insti.fragment.MessMenuFragment;
import app.insti.fragment.MyEventsFragment;
import app.insti.fragment.NewsFragment;
import app.insti.fragment.NotificationsFragment;
import app.insti.fragment.PlacementBlogFragment;
import app.insti.fragment.ProfileFragment;
import app.insti.fragment.QLinksFragment;
import app.insti.fragment.SettingsFragment;
import app.insti.fragment.TrainingBlogFragment;

import static app.insti.Constants.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION;
import static app.insti.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static app.insti.Constants.RESULT_LOAD_IMAGE;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MainActivity";
    SessionManager session;
    NotificationsResponse notificationsResponse;
    FeedFragment feedFragment;
    private User currentUser;
    private boolean showNotifications = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initPicasso();
        } catch (IllegalStateException ignored) {
        }
        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        feedFragment = new FeedFragment();
        updateFragment(feedFragment);

//        fetchNotifications();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initNavigationView();
        if (session.isLoggedIn()) {
            currentUser = User.fromString(session.pref.getString(Constants.CURRENT_USER, "Error"));
            updateNavigationView();
        }
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
                Bundle bundle = new Bundle();
                bundle.putString(Constants.USER_ID, currentUser.getUserID());
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(bundle);
                updateFragment(profileFragment);
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

//    private void fetchNotifications() {
//        NotificationsRequest notificationsRequest = new NotificationsRequest(0, 20);
//        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
//        retrofitInterface.getNotifications(notificationsRequest).enqueue(new Callback<NotificationsResponse>() {
//            @Override
//            public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
//                if (response.isSuccessful()) {
//                    notificationsResponse = response.body();
//                    if (showNotifications) {
//                        showNotifications();
//                        showNotifications = false;
//                    }
//                }
//                //Server Error
//            }
//
//            @Override
//            public void onFailure(Call<NotificationsResponse> call, Throwable t) {
//                //Network Error
//            }
//        });
//    }

    public void showNotifications() {
        String notificationsResponseJson = new Gson().toJson(notificationsResponse);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.NOTIFICATIONS_RESPONSE_JSON, notificationsResponseJson);
        NotificationsFragment notificationsFragment = new NotificationsFragment();
        notificationsFragment.setArguments(bundle);
        updateFragment(notificationsFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (feedFragment != null && feedFragment.isVisible()) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notifications) {
            showNotifications = true;
//            fetchNotifications();
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
                }
                else{
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
                QLinksFragment qLinksFragment = new QLinksFragment();
                updateFragment(qLinksFragment);
                break;
            case R.id.nav_map:
                MapFragment mapFragment = new MapFragment();
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    updateFragment(mapFragment);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
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
        Log.d(TAG, "updateFragment: " + fragment.toString());
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(Constants.SESSION_ID, session.pref.getString(Constants.SESSION_ID, "Error"));
        if (fragment instanceof MessMenuFragment)
            bundle.putString(Constants.USER_HOSTEL, session.isLoggedIn() ? currentUser.getHostel() : "1");
        if (fragment instanceof SettingsFragment && session.isLoggedIn())
            bundle.putString(Constants.USER_ID, currentUser.getUserID());
        fragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        if (fragment instanceof FeedFragment)
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = manager.beginTransaction();

        /* Animate only for ProfileFragment */
        if (fragment instanceof ProfileFragment) {
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
                    MapFragment mapFragment = new MapFragment();
                    updateFragment(mapFragment);
                } else {
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
                UnsafeOkHttpClient.getUnsafeOkHttpClient(getApplicationContext())
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
}
