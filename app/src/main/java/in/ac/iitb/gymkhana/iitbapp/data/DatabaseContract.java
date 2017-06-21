package in.ac.iitb.gymkhana.iitbapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static final String CONTENT_AUTHORITY = "in.ac.iitb.gymkhana.iitbapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MAP = "map";

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
}
