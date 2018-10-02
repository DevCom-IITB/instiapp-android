package app.insti;

public class Constants {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 2;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 3;
    public static final int RESULT_LOAD_IMAGE = 11;
    public static final int REQUEST_CAMERA_INT_ID = 101;
    public static final String NOTIFICATIONS_RESPONSE_JSON = "notifications_json";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_LATITUDE = "event_latitude";
    public static final String EVENT_LONGITUDE = "event_longitude";
    public static final String EVENT_JSON = "event_json";
    public static final String EVENT_LIST_JSON = "event_list_json";
    public static final String USER_ID = "user_id";
    public static final String USER_HOSTEL = "user_hostel";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PREF_NAME = "LoggedInPref";
    public static final String IS_LOGGED_IN = "IsLoggedIn";
    public static final String GCM_ID = "GcmId";
    public static final String CURRENT_USER = "current_user";
    public static final String SESSION_ID = "session_id";
    public static final int STATUS_GOING = 2;
    public static final int STATUS_INTERESTED = 1;
    public static final int STATUS_NOT_GOING = 0;
    public static final String BODY_JSON = "body_json";
    public static final String BODY_LIST_JSON = "body_list_json";
    public static final String ROLE_LIST_JSON = "role_list_json";

    public static final String LOGIN_MESSAGE = "Please login to continue!";

    public static final String MAIN_INTENT_EXTRAS = "MAIN_EXTRA";
    public static final String FCM_BUNDLE_TYPE = "type";
    public static final String FCM_BUNDLE_ID = "id";
    public static final String FCM_BUNDLE_EXTRA = "extra";
    public static final String FCM_BUNDLE_NOTIFICATION_ID = "notification_id";
    public static final String FCM_BUNDLE_ACTION = "action";
    public static final String FCM_BUNDLE_RICH = "rich";
    public static final String FCM_BUNDLE_TITLE = "title";
    public static final String FCM_BUNDLE_VERB = "verb";
    public static final String FCM_BUNDLE_IMAGE = "image_url";
    public static final String FCM_BUNDLE_LARGE_ICON = "large_icon";
    public static final String FCM_BUNDLE_LARGE_CONTENT = "large_content";

    public static final String FCM_BUNDLE_ACTION_STARTING = "starting";

    public static final String DATA_TYPE_EVENT = "event";
    public static final String DATA_TYPE_BODY = "body";
    public static final String DATA_TYPE_USER = "userprofile";
    public static final String DATA_TYPE_NEWS = "newsentry";
    public static final String DATA_TYPE_PT = "blogentry";

    /* Map */
    public static final double MAP_Xn = 19.134417, MAP_Yn = 72.901229, MAP_Zn = 1757, MAP_Zyn = 501;
    public static final double[] MAP_WEIGHTS_X = {-11.392001766454612, -36.31634553309953, 73.91269388324432, -24.14021153064087, 3.4508817531539115, -0.1462262375477863, 5.532505074667804, -1.542391995870977, 36.14211738142935};
    public static final double[] MAP_WEIGHTS_Y = {0.09738953520399705, -4.519868444089616, 62.38493718381985, 16.664561869057696, -2.168377988768651, 0.0919143297622087, 0.32304266159540823, 0.21688067854428716, -12.81393255320748};
}
