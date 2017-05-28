package in.ac.iitb.gymkhana.iitbapp.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import in.ac.iitb.gymkhana.iitbapp.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment gMapFragment;
    GoogleMap gMap = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_map, container, false);

        gMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.viewMap);
        gMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }
        // Position the map's camera near Mumbai
        LatLng iitb = new LatLng(19.1334, 72.9133);
        googleMap.addMarker(new MarkerOptions().position(iitb)
                .title("Marker in IITB"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(iitb));
    }




}


