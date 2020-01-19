package app.insti;

import com.mrane.data.Marker;

import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.User;
import app.insti.fragment.MapFragment;

/**
 * Created by varun on 03-Apr-18.
 */

public final class ShareURLMaker {
    public static final String WEB_HOST = "https://www.insti.app/";

    public static String getEventURL(Event event) {
        return WEB_HOST + "event/" + event.getEventStrID();
    }

    public static String getBodyURL(Body body) {
        return WEB_HOST + "org/" + body.getBodyStrID();
    }

    public static String getUserURL(User user) {
        return WEB_HOST + "user/" + user.getUserLDAPId();
    }

    public static String getMapURL(Marker marker) {
        return WEB_HOST + "map/" + MapFragment.getPassableName(marker.getShortName());
    }
}
