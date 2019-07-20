package app.insti.adapter;

import androidx.fragment.app.Fragment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.Achievement;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.Role;
import app.insti.api.model.User;
import app.insti.interfaces.CardInterface;

public class GenericAdapter extends CardAdapter<CardInterface> {
    public GenericAdapter(List<CardInterface> cardInterfaceList, Fragment fragment){
        super(cardInterfaceList, fragment);
    }

    @Override
    public void onClick(CardInterface cardInterface, Fragment fragment, View view) {
        if (cardInterface instanceof Event) {
            Utils.openEventFragment((Event) cardInterface, fragment, view.findViewById(R.id.object_picture));
        } else if (cardInterface instanceof Body) {
            Utils.openBodyFragment((Body) cardInterface, fragment, view.findViewById(R.id.object_picture));
        } else if (cardInterface instanceof User) {
            Utils.openUserFragment((User) cardInterface, fragment, view.findViewById(R.id.object_picture));
        } else if (cardInterface instanceof Role) {
            Utils.openBodyFragment(((Role) cardInterface).getRoleBodyDetails(), fragment, view.findViewById(R.id.object_picture));
        } else if (cardInterface instanceof Achievement) {
            Achievement a = (Achievement) cardInterface;
            if (a.getAchievementEvent() != null) {
                a.getAchievementEvent().setEventBodies(new ArrayList<>());
                a.getAchievementEvent().getEventBodies().add(a.getAchievementBody());
                Utils.openEventFragment(a.getAchievementEvent(), fragment, view.findViewById(R.id.object_picture));
            } else {
                Utils.openBodyFragment(a.getAchievementBody(), fragment, view.findViewById(R.id.object_picture));
            }
        }
    }

    @Override
    public int getAvatarPlaceholder(CardInterface cardInterface) {
        if (cardInterface instanceof User) {
            return R.drawable.user_placeholder;
        }
        return Utils.isDarkTheme ? R.drawable.lotus_placeholder_dark : R.drawable.lotus_placeholder;    
    }
}
