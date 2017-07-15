package in.ac.iitb.gymkhana.iitbapp;

import android.Manifest;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import in.ac.iitb.gymkhana.iitbapp.fragment.AboutFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.CMSFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.CalendarFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.ContactsFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.FeedFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.GCRankingsFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.MapFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.MessMenuFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.MyEventsFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.PTCellFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.TimetableFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        if (id == R.id.action_settings) {
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
                FeedFragment feedFragment = new FeedFragment();
                updateFragment(feedFragment);
                break;
            case R.id.nav_my_events:
                MyEventsFragment myeventsFragment = new MyEventsFragment();
                updateFragment(myeventsFragment);
                break;
            case R.id.nav_pt_cell:
                PTCellFragment ptcellFragment = new PTCellFragment();
                updateFragment(ptcellFragment);
                break;
            case R.id.nav_mess_menu:
                MessMenuFragment messmenuFragment = new MessMenuFragment();
                updateFragment(messmenuFragment);
                break;
            case R.id.nav_gc_rankings:
                GCRankingsFragment gcrankingsFragment = new GCRankingsFragment();
                updateFragment(gcrankingsFragment);
                break;
            case R.id.nav_calendar:
                CalendarFragment calendarFragment = new CalendarFragment();
                updateFragment(calendarFragment);
                break;
            case R.id.nav_cms:
                CMSFragment cmsFragment = new CMSFragment();
                updateFragment(cmsFragment);
                break;
            case R.id.nav_timetable:
                TimetableFragment timetableFragment = new TimetableFragment();
                updateFragment(timetableFragment);
                break;
            case R.id.nav_map:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {


                } else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

                break;

            case R.id.nav_contacts:
                ContactsFragment contactsFragment = new ContactsFragment();
                updateFragment(contactsFragment);
                break;
            case R.id.nav_about:
                AboutFragment aboutFragment = new AboutFragment();
                updateFragment(aboutFragment);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.framelayout_for_fragment, fragment, fragment.getTag());
        transaction.commit();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MapFragment mapFragment = new MapFragment();
            updateFragment(mapFragment);
        } else {
            Toast toast = Toast.makeText(MainActivity.this, "Need Permission", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
