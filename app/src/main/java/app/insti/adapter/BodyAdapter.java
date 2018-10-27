package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import app.insti.Utils;
import app.insti.api.model.Body;


public class BodyAdapter extends CardAdapter<Body> {

    public BodyAdapter(List<Body> bodyList, Fragment mFragment) {
        super(bodyList, mFragment);
    }

    public void onClick(Body body, FragmentActivity fragmentActivity) {
        Utils.openBodyFragment(body, fragmentActivity);
    }
}
