package in.ac.iitb.gymkhana.iitbapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import in.ac.iitb.gymkhana.iitbapp.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment gMapFragment;
    GoogleMap gMap = null;
    Button getLocation;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);



        gMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.viewMap);
        gMapFragment.getMapAsync(this);



        return view;
    }

    public void onMapReady(final GoogleMap googleMap) {

        // Position the map's camera near Mumbai
//        PolygonOptions polygonOptions=new PolygonOptions()
//                .add(new LatLng(19.124612, 72.907672),new LatLng(19.127332, 72.908479),new LatLng(19.126061, 72.910496),new LatLng(19.129228, 72.912569),new LatLng(19.129631, 72.909186),new LatLng(19.129359, 72.904213), new LatLng(19.135205, 72.902112),new LatLng(19.137735, 72.910289),new LatLng(19.141830, 72.914795),new LatLng(19.143726, 72.920167),new LatLng(19.128968, 72.920575),new LatLng(19.125319, 72.916970),new LatLng(19.123697, 72.911134),new LatLng(19.124001, 72.907937));
//        googleMap.addPolygon(polygonOptions);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        googleMap.setMyLocationEnabled(true);
        }
        else
            Toast.makeText(getContext(), "Give location access to the map.Go to Settings", Toast.LENGTH_SHORT).show();


        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLngBounds iitbBounds = new LatLngBounds(
                new LatLng(19.125400, 72.904000), new LatLng(19.140500, 72.920000));

        googleMap.setLatLngBoundsForCameraTarget(iitbBounds);
        LatLng iitb = new LatLng(19.1334, 72.9133);


        googleMap.setMaxZoomPreference(30);
        googleMap.setMinZoomPreference(18);

        googleMap.addMarker(new MarkerOptions().position(iitb)
                .title("Marker in IITB"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(iitb));

        //googleMap.setMyLocationEnabled(true);




    }
}