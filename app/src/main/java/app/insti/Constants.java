package app.insti;

public class Constants {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 3;
    public static final int RESULT_LOAD_IMAGE = 11;
    public static final int REQUEST_CAMERA_INT_ID = 101;
    public static final String DARK_THEME = "dark_theme";
    public static final String CALENDAR_DIALOG = "calendar_dialog";
    public static final String CALENDAR_DIALOG_YES = "Yes";
    public static final String CALENDAR_DIALOG_NO = "No";
    public static final String CALENDAR_DIALOG_ALWAYS_ASK = "Always ask";
    public static final String NOTIFICATIONS_RESPONSE_JSON = "notifications_json";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_LATITUDE = "event_latitude";
    public static final String EVENT_LONGITUDE = "event_longitude";
    public static final String EVENT_JSON = "event_json";
    public static final String EVENT_LIST_JSON = "event_list_json";
    public static final String USER_ID = "user_id";
    public static final String USER_JSON = "user_json";
    public static final String USER_HOSTEL = "user_hostel";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PREF_NAME = "LoggedInPref";
    public static final String IS_LOGGED_IN = "IsLoggedIn";
    public static final String GCM_ID = "GcmId";
    public static final String CURRENT_USER = "current_user";
    public static final String CURRENT_USER_PROFILE_PICTURE = "current_user_profile_picture";
    public static final String SESSION_ID = "session_id";
    public static final int STATUS_GOING = 2;
    public static final int STATUS_INTERESTED = 1;
    public static final int STATUS_NOT_GOING = 0;
    public static final String BODY_JSON = "body_json";
    public static final String BODY_LIST_JSON = "body_list_json";
    public static final String ROLE_LIST_JSON = "role_list_json";
    public static final String NO_SHARED_ELEM = "no_shared_elem";

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
    public static final String FCM_BUNDLE_TOTAL_COUNT = "total_count";
    public static final String NOTIF_CANCELLED = "notif_cancelled";

    public static final String FCM_BUNDLE_ACTION_STARTING = "starting";

    public static final String DATA_TYPE_EVENT = "event";
    public static final String DATA_TYPE_BODY = "body";
    public static final String DATA_TYPE_USER = "userprofile";
    public static final String DATA_TYPE_NEWS = "newsentry";
    public static final String DATA_TYPE_PT = "blogentry";
    public static final String DATA_TYPE_MAP= "map";

    public static final String CARD_TYPE_TITLE = "card_type_title";
    public static final String CARD_TYPE_BODY_HEAD = "card_type_body_head";

    /* Webview */
    public static final String WV_TYPE = "webview_type";
    public static final String WV_TYPE_ADD_EVENT = "add_event";
    public static final String WV_TYPE_UPDATE_EVENT = "update_event";
    public static final String WV_TYPE_UPDATE_BODY = "update_body";
    public static final String WV_TYPE_ACHIEVEMENTS = "achievements";
    public static final String WV_TYPE_NEW_OFFERED_ACHIEVEMENT = "achievements_new_offered";
    public static final String WV_TYPE_URL = "url_type";
    public static final String WV_ID = "id";
    public static final String WV_URL = "url";

    /* Map */
    public static final double MAP_Xn = 19.133691, MAP_Yn = 72.916984, MAP_Zn = 4189, MAP_Zyn = 1655;
    public static final double[] MAP_WEIGHTS_X = {-7.769917472065843, 159.26978694839946, 244.46989575495544, -6.003894110679995, -0.28864271213341297, 0.010398324019718075, 4.215508849724247, -0.6078830146963545, -7.0400449629241395};
    public static final double[] MAP_WEIGHTS_Y = {14.199431377059842, -158.80601990819815, 68.9630034040724, 5.796703402034644, 1.1348242200568706, 0.11891051684489184, -0.2930832938484276, 0.1448231125788526, -5.282895700923075};
    public static final String MAP_INITIAL_MARKER = "initial_marker";
}
