package in.ac.iitb.gymkhana.iitbapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static final String CONTENT_AUTHORITY = "in.ac.iitb.gymkhana.iitbapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MAP = "map";
    public static final String PATH_USER_PROFILE = "userProfile";
    public static final String PATH_USER_FOLLOWERS = "userFollowers";
    public static final String PATH_USER_FOLLOWS = "userFollows";
    public static final String PATH_NEWS_FEED = "newsFeed";

    public static final class MapEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MAP)
                .build();
        public static final String TABLE_NAME = "map";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";


    }

    public static final class UserProfileEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_PROFILE)
                .build();
        public static final String TABLE_NAME = "userProfile";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_USER_ROLLNO = "user_rollno";
        public static final String COLUMN_USER_POR = "user_por";
        public static final String COLUMN_USER_PROFILE_PICTURE = "user_profile_picture";
        public static final String COLUMN_USER_HOSTELNO = "user_hostelno";
        public static final String COLUMN_USER_ABOUTME = "user_aboutme";
        public static final String COLUMN_USER_FOLLOWING_COUNT = "user_following_count";
        public static final String COLUMN_USER_FOLLOWERS_COUNT = "user_follwers_count";
        public static final String COLUMN_USER_EVENTS_COUNT = "user_events_count";
        public static final String COLUMN_IS_FOLLOWED = "isFollowed";
        public static final String COLUMN_FOLLOWS_YOU = "followsYou";
        public static final String COLUMN_USER_ROOM_NO = "user_roomno";
        public static final String COLUMN_USER_PHONE_NO = "user_phoneno";

    }

    public static final class UserFollowersEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_FOLLOWERS)
                .build();
        public static final String TABLE_NAME = "userFollowers";
        public static final String COLUMN_USER_PROFILE_PICTURE = "user_profile_picture";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_USER_PROFILE = "userProfile";

    }

    public static final class UserFollowsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_FOLLOWS)
                .build();
        public static final String TABLE_NAME = "userFollows";
        public static final String COLUMN_USER_PROFILE_PICTURE = "user_profile_picture";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_USER_PROFILE = "userProfile";


    }

    public static final class NewsFeedEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NEWS_FEED)
                .build();
        public static final String TABLE_NAME = "newsFeed";
        public static final String COLUMN_EVENT_NAME = "event_name";
        public static final String COLUMN_EVENT_DESCRIPTION = "event_description";
        public static final String COLUMN_EVENT_IMAGE = "event_image";
        public static final String COLUMN_EVENT_CREATOR_NAME = "event_creator_name";
        public static final String COLUMN_EVENT_CREATOR_ID = "event_creator_id";
        public static final String COLUMN_EVENT_GOING_STATUS = "event_going_status";


    }


}
