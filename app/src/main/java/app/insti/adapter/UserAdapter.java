package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.view.View;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.User;

public class UserAdapter extends CardAdapter<User> {

    public UserAdapter(List<User> userList, Fragment mFragment) {
        super(userList, mFragment);
    }

    @Override
    public void onClick(User user, Fragment fragment, View view) {
        Utils.openUserFragment(user, fragment, view.findViewById(R.id.object_picture));
    }

    @Override
    public int getAvatarPlaceholder(User user) {
        return R.drawable.user_placeholder;
    }
}
