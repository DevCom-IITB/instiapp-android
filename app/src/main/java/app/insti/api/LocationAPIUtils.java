package app.insti.api;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static app.insti.Constants.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by Shivam Sharma on 13-08-2018.
 * <p>
 * This API updates the Google Map when the location is added.
 */

public class LocationAPIUtils {

    private static final String TAG = LocationAPIUtils.class.getSimpleName();

    GoogleMap googleMap;
    MapView mMapView;

    public LocationAPIUtils(GoogleMap googleMap,
                            MapView mMapView) {

        this.googleMap = googleMap;
        this.mMapView = mMapView;

    }

    public void callGoogleToShowLocationOnMap(final Activity activity, final Context context, final LatLng location, final String name, final String address, final int cursor) {
        mMapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;

                if (cursor != 0) {
                    googleMap.clear();
                }


                googleMap.addMarker(new MarkerOptions().position(location).title(name).snippet(address));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                Log.i(TAG, "curser = " + cursor);
            }
        });
    }
}
