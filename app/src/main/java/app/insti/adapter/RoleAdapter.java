package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import app.insti.Utils;
import app.insti.api.model.Role;


public class RoleAdapter extends CardAdapter<Role> {


    public RoleAdapter(List<Role> roleList, Fragment mFragment) {
        super(roleList, mFragment);
    }

    @Override
    public void onClick(Role role, FragmentActivity fragmentActivity) {
        Utils.openBodyFragment(role.getRoleBodyDetails(), fragmentActivity);
    }

    @Override
    public String getTitle(Role role) {
        return role.getRoleBodyDetails().getBodyName();
    }

    @Override
    public String getSubtitle(Role role) {
        return role.getRoleName();
    }

    @Override
    public String getAvatarUrl(Role role) {
        return role.getRoleBodyDetails().getBodyImageURL();
    }
}
