package in.ac.iitb.gymkhana.iitbapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.R;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment gMapFragment;
    GoogleMap googleMap;
    FloatingActionButton locationButton;
    Location currentLocation;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        gMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.viewMap);
        gMapFragment.getMapAsync(this);

        return view;
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {

        //TODO: Handle Location permissions in MainActivity, permission required to view fragment
        googleMap = gMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }


        LatLngBounds iitbBounds = new LatLngBounds(new LatLng(19.1249000, 72.9046000), new LatLng(19.143522, 72.920000));
        googleMap.setLatLngBoundsForCameraTarget(iitbBounds);
        googleMap.setMaxZoomPreference(30);
        googleMap.setMinZoomPreference(14.5f);
        // Position the map's camera near Mumbai
        LatLng iitb = new LatLng(19.1334, 72.9133);
        googleMap.addMarker(new MarkerOptions().position(iitb).title("Marker in IITB"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(iitb));

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                locationButton.getDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGray), PorterDuff.Mode.SRC_IN);
            }
        });

        locationButton = (FloatingActionButton) getActivity().findViewById(R.id.location);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLocation = getLastKnownLocation();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 17);
                googleMap.animateCamera(cameraUpdate);
                locationButton.getDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        } else {
            Toast toast = Toast.makeText(getActivity(), "Need Permission", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
