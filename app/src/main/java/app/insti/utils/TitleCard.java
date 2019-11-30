package app.insti.utils;

import app.insti.Constants;
import app.insti.interfaces.CardInterface;

public class TitleCard implements CardInterface {
    private String title;

    public TitleCard(String mTitle) {
        title = mTitle;
    }

    @Override
    public long getId() {
        return (getSubtitle() + getTitle()).hashCode();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return Constants.CARD_TYPE_TITLE;
    }

    @Override
    public String getAvatarUrl() {
        return null;
    }

    @Override
    public int getBadge() { return 0; }
}
