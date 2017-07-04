package in.ac.iitb.gymkhana.iitbapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "IITBAppDb.db";
    private static final int VERSION = 1;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_MAP = "CREATE TABLE " + DatabaseContract.MapEntry.TABLE_NAME + " (" +
                DatabaseContract.MapEntry._ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.MapEntry.COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                DatabaseContract.MapEntry.COLUMN_LONGITUDE + " DOUBLE NOT NULL, " +
                DatabaseContract.MapEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DatabaseContract.MapEntry.COLUMN_TYPE + " TEXT NOT NULL);";

        final String CREATE_TABLE_USER_PROFILE = "CREATE TABLE " + DatabaseContract.UserProfileEntry.TABLE_NAME + " (" +
                DatabaseContract.UserProfileEntry._ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_ROLLNO + " TEXT NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_POR + " TEXT NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_PROFILE_PICTURE + " TEXT NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_HOSTELNO + " TEXT NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_ABOUTME + " TEXT NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_FOLLOWING_COUNT + " INTEGER NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_FOLLOWERS_COUNT + " INTEGER NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_EVENTS_COUNT + " INTEGER NOT NULL, " +
                DatabaseContract.UserProfileEntry.COLUMN_IS_FOLLOWED + " BOOLEAN, " +
                DatabaseContract.UserProfileEntry.COLUMN_FOLLOWS_YOU + " BOOLEAN, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_ROOM_NO + " TEXT, " +
                DatabaseContract.UserProfileEntry.COLUMN_USER_PHONE_NO + " TEXT);";

        final String CREATE_TABLE_USER_FOLLOWERS = "CREATE TABLE " + DatabaseContract.UserFollowersEntry.TABLE_NAME + " (" +
                DatabaseContract.UserFollowersEntry._ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.UserFollowersEntry.COLUMN_USER_PROFILE_PICTURE + " TEXT NOT NULL, " +
                DatabaseContract.UserFollowersEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                DatabaseContract.UserFollowersEntry.COLUMN_USER_PROFILE + " TEXT NOT NULL);";

        final String CREATE_TABLE_USER_FOLLOWS = "CREATE TABLE " + DatabaseContract.UserFollowsEntry.TABLE_NAME + " (" +
                DatabaseContract.UserFollowsEntry._ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.UserFollowsEntry.COLUMN_USER_PROFILE_PICTURE + " TEXT NOT NULL, " +
                DatabaseContract.UserFollowsEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                DatabaseContract.UserFollowsEntry.COLUMN_USER_PROFILE + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE_MAP);
        db.execSQL(CREATE_TABLE_USER_PROFILE);
        db.execSQL(CREATE_TABLE_USER_FOLLOWERS);
        db.execSQL(CREATE_TABLE_USER_FOLLOWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MapEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserProfileEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserFollowersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserFollowsEntry.TABLE_NAME);
        onCreate(db);
    }
}
