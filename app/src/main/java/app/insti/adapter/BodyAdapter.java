package app.insti.adapter;

import android.view.View;

import androidx.fragment.app.Fragment;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.Body;


public class BodyAdapter extends CardAdapter<Body> {

    public BodyAdapter(List<Body> bodyList, Fragment mFragment) {
        super(bodyList, mFragment);
    }

    public void onClick(Body body, Fragment fragment, View view) {
        Utils.openBodyFragment(body, fragment, view.findViewById(R.id.object_picture));
    }
}
