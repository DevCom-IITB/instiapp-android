package app.insti.adapter;

import androidx.fragment.app.Fragment;
import android.view.View;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.Role;


public class RoleAdapter extends CardAdapter<Role> {

    public RoleAdapter(List<Role> roleList, Fragment mFragment) {
        super(roleList, mFragment);
    }

    @Override
    public void onClick(Role role, Fragment fragment, View view) {
        Utils.openBodyFragment(role.getRoleBodyDetails(), fragment, view.findViewById(R.id.object_picture));
    }
}
