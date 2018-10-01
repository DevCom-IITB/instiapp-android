package app.insti.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Shivam Sharma on 13-08-2018.
 * <p>
 * This API updates the Google Map when the location is added.
 */

public class LocationAPIUtils {

    private static final String TAG = LocationAPIUtils.class.getSimpleName();
    GoogleMap googleMap;
    MapView mMapView;

    public LocationAPIUtils(GoogleMap googleMap, MapView mMapView) {
        this.googleMap = googleMap;
        this.mMapView = mMapView;
    }

    public void callGoogleToShowLocationOnMap(  final LatLng location, final String name, final String address, final int cursor) {
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