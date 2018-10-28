package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.User;
import app.insti.interfaces.CardInterface;

public class GenericAdapter extends CardAdapter<CardInterface> {

    public GenericAdapter(List<CardInterface> cardInterfaceList, Fragment fragment){
        super(cardInterfaceList, fragment);
    }

    @Override
    public void onClick(CardInterface cardInterface, FragmentActivity fragmentActivity) {
        if (cardInterface instanceof Event) {
            Utils.openEventFragment((Event) cardInterface, fragmentActivity);
        } else if (cardInterface instanceof Body) {
            Utils.openBodyFragment((Body) cardInterface, fragmentActivity);
        } else if (cardInterface instanceof User) {
            Utils.openUserFragment((User) cardInterface, fragmentActivity);
        }
    }

    @Override
    public int getAvatarPlaceholder(CardInterface cardInterface) {
        if (cardInterface instanceof User) {
            return R.drawable.user_placeholder;
        }
        return R.drawable.lotus_placeholder;
    }
}
