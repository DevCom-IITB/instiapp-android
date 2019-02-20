package app.insti.utils;

import androidx.fragment.app.Fragment;

import app.insti.Constants;
import app.insti.api.model.Body;
import app.insti.interfaces.CardInterface;

public class BodyHeadCard implements CardInterface {

    private Body body;
    public BodyHeadCard(Body mBody) {
        body = mBody;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSubtitle() {
        return Constants.CARD_TYPE_BODY_HEAD;
    }

    @Override
    public String getAvatarUrl() {
        return null;
    }

    public void bindView(BodyHeadViewHolder viewHolder, Fragment fragment) {
        viewHolder.bindView(body, fragment);
    }
}
