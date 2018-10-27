package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.User;

public class UserAdapter extends CardAdapter<User> {

    public UserAdapter(List<User> userList, Fragment mFragment) {
        super(userList, mFragment);
    }

    @Override
    public void onClick(User user, FragmentActivity fragmentActivity) {
        Utils.openUserFragment(user, fragmentActivity);
    }

    @Override
    public String getTitle(User user) {
        return user.getUserName();
    }

    @Override
    public String getSubtitle(User user) {
        if (user.getCurrentRole() == null || user.getCurrentRole().equals("")) {
            return user.getUserLDAPId();
        } else {
            return user.getCurrentRole();
        }
    }

    @Override
    public String getAvatarUrl(User user) {
        return user.getUserProfilePictureUrl();
    }

    @Override
    public int getAvatarPlaceholder() {
        return R.drawable.user_placeholder;
    }
}
