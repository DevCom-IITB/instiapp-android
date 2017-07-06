package in.ac.iitb.gymkhana.iitbapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import in.ac.iitb.gymkhana.iitbapp.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment gMapFragment;
    GoogleMap googleMap ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        gMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.viewMap);
        gMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {

        googleMap=gMap;
       
           googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        


        LatLngBounds iitbBounds=new LatLngBounds(new LatLng(19.1249000, 72.9046000),new LatLng(19.143522, 72.920000));
        googleMap.setLatLngBoundsForCameraTarget(iitbBounds);
        googleMap.setMaxZoomPreference(30);
        googleMap.setMinZoomPreference((float) 14.5);
        // Position the map's camera near Mumbai
        LatLng iitb = new LatLng(19.1334, 72.9133);
        googleMap.addMarker(new MarkerOptions().position(iitb)
                .title("Marker in IITB"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(iitb));
    }
   }
