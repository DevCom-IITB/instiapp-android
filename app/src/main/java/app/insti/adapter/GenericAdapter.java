package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.view.View;

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
    public void onClick(CardInterface cardInterface, Fragment fragment, View view) {
        if (cardInterface instanceof Event) {
            Utils.openEventFragment((Event) cardInterface, fragment, view.findViewById(R.id.object_picture));
        } else if (cardInterface instanceof Body) {
            Utils.openBodyFragment((Body) cardInterface, fragment, view.findViewById(R.id.object_picture));
        } else if (cardInterface instanceof User) {
            Utils.openUserFragment((User) cardInterface, fragment, view.findViewById(R.id.object_picture));
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
