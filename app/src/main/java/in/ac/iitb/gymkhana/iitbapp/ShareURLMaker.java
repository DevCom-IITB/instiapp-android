package in.ac.iitb.gymkhana.iitbapp;

import in.ac.iitb.gymkhana.iitbapp.data.Event;

/**
 * Created by varun on 03-Apr-18.
 */

public final class ShareURLMaker {
    public static final String WEB_HOST = "https://instiapp.wncc-iitb.org/";

    public static final String getEventURL(Event event) {
        return WEB_HOST + "event/" + event.getEventStrID();
    }
}
