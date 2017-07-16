package in.ac.iitb.gymkhana.iitbapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class IITBAppContentProvider extends ContentProvider {
    public static final int LOCS = 100;
    public static final int LOC_WITH_ID = 101;
    public static final int USER_PROFILES = 200;
    public static final int USER_PROFILE_WITH_ID = 201;
    public static final int USER_FOLLOWERS = 300;
    public static final int USER_FOLLOWER_WITH_ID = 301;
    public static final int USER_FOLLOWS = 400;
    public static final int USER_FOLLOWS_WITH_ID = 401;
    public static final int NEWS_FEED = 500;
    public static final int NEWS_FEED_WITH_ID = 501;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper databaseHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_MAP, LOCS);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_MAP + "/#", LOC_WITH_ID);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_USER_PROFILE, USER_PROFILES);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_USER_PROFILE + "/#", USER_PROFILE_WITH_ID);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_USER_FOLLOWERS, USER_FOLLOWERS);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_USER_FOLLOWERS + "/#", USER_FOLLOWER_WITH_ID);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_USER_FOLLOWS, USER_FOLLOWS);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_USER_FOLLOWS + "/#", USER_FOLLOWS_WITH_ID);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_NEWS_FEED, NEWS_FEED);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_NEWS_FEED + "/#", NEWS_FEED_WITH_ID);
        return matcher;

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        databaseHelper = new DatabaseHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        String id;
        String selectionArguments[];
        switch (match) {
            case LOCS:
                cursor = db.query(DatabaseContract.MapEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case LOC_WITH_ID:
                id = uri.getPathSegments().get(1);
                selectionArguments = new String[]{id};
                cursor = db.query(DatabaseContract.MapEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case USER_PROFILES:
                cursor = db.query(DatabaseContract.UserProfileEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case USER_PROFILE_WITH_ID:
                id = uri.getPathSegments().get(1);
                selectionArguments = new String[]{id};
                cursor = db.query(DatabaseContract.UserProfileEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case USER_FOLLOWERS:
                cursor = db.query(DatabaseContract.UserFollowersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case USER_FOLLOWER_WITH_ID:
                id = uri.getPathSegments().get(1);
                selectionArguments = new String[]{id};
                cursor = db.query(DatabaseContract.UserFollowersEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case USER_FOLLOWS:
                cursor = db.query(DatabaseContract.UserFollowsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case USER_FOLLOWS_WITH_ID:
                id = uri.getPathSegments().get(1);
                selectionArguments = new String[]{id};
                cursor = db.query(DatabaseContract.UserFollowsEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case NEWS_FEED:
                cursor = db.query(DatabaseContract.NewsFeedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NEWS_FEED_WITH_ID:
                id = uri.getPathSegments().get(1);
                selectionArguments = new String[]{id};
                cursor = db.query(DatabaseContract.NewsFeedEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new SQLException("Wrong Uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case LOCS:
                return "vnd.android.cursor.dir" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_MAP;
            case LOC_WITH_ID:
                return "vnd.android.cursor.item" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_MAP;
            case USER_PROFILES:
                return "vnd.android.cursor.dir" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_USER_PROFILE;
            case USER_PROFILE_WITH_ID:
                return "vnd.android.cursor.item" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_USER_PROFILE;
            case USER_FOLLOWERS:
                return "vnd.android.cursor.dir" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_USER_FOLLOWERS;
            case USER_FOLLOWER_WITH_ID:
                return "vnd.android.cursor.item" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_USER_FOLLOWERS;
            case USER_FOLLOWS:
                return "vnd.android.cursor.dir" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_USER_FOLLOWS;
            case USER_FOLLOWS_WITH_ID:
                return "vnd.android.cursor.item" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_USER_FOLLOWS;
            case NEWS_FEED:
                return "vnd.android.cursor.dir" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_NEWS_FEED;
            case NEWS_FEED_WITH_ID:
                return "vnd.android.cursor.item" + "/" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_NEWS_FEED;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        long id;
        switch (match) {

            case LOCS:
                id = db.insert(DatabaseContract.MapEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(DatabaseContract.MapEntry.CONTENT_URI, id);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            case USER_PROFILES:
                id = db.insert(DatabaseContract.UserProfileEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(DatabaseContract.UserProfileEntry.CONTENT_URI, id);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            case USER_FOLLOWERS:
                id = db.insert(DatabaseContract.UserFollowersEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(DatabaseContract.UserFollowersEntry.CONTENT_URI, id);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            case USER_FOLLOWS:
                id = db.insert(DatabaseContract.UserFollowsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(DatabaseContract.UserFollowsEntry.CONTENT_URI, id);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            case NEWS_FEED:
                id = db.insert(DatabaseContract.NewsFeedEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(DatabaseContract.NewsFeedEntry.CONTENT_URI, id);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new SQLException("Wrong uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsInserted;
        switch (sUriMatcher.match(uri)) {

            case LOCS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {


                        long _id = db.insert(DatabaseContract.MapEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case USER_PROFILES:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {


                        long _id = db.insert(DatabaseContract.UserProfileEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case USER_FOLLOWERS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {


                        long _id = db.insert(DatabaseContract.UserFollowersEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case USER_FOLLOWS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {


                        long _id = db.insert(DatabaseContract.UserFollowsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case NEWS_FEED:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {


                        long _id = db.insert(DatabaseContract.NewsFeedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        String id;
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case LOCS:
                numRowsDeleted = databaseHelper.getWritableDatabase().delete(
                        DatabaseContract.MapEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case USER_PROFILES:
                numRowsDeleted = databaseHelper.getWritableDatabase().delete(
                        DatabaseContract.UserProfileEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case USER_FOLLOWERS:
                numRowsDeleted = databaseHelper.getWritableDatabase().delete(
                        DatabaseContract.UserFollowersEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case USER_FOLLOWS:
                numRowsDeleted = databaseHelper.getWritableDatabase().delete(
                        DatabaseContract.UserFollowsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case NEWS_FEED:
                numRowsDeleted = databaseHelper.getWritableDatabase().delete(
                        DatabaseContract.NewsFeedEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            case LOC_WITH_ID:

                id = uri.getPathSegments().get(1);

                numRowsDeleted = databaseHelper.getWritableDatabase().delete(DatabaseContract.MapEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            case USER_PROFILE_WITH_ID:

                id = uri.getPathSegments().get(1);

                numRowsDeleted = databaseHelper.getWritableDatabase().delete(DatabaseContract.UserProfileEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            case USER_FOLLOWER_WITH_ID:

                id = uri.getPathSegments().get(1);

                numRowsDeleted = databaseHelper.getWritableDatabase().delete(DatabaseContract.UserFollowersEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            case USER_FOLLOWS_WITH_ID:

                id = uri.getPathSegments().get(1);

                numRowsDeleted = databaseHelper.getWritableDatabase().delete(DatabaseContract.UserFollowsEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            case NEWS_FEED_WITH_ID:

                id = uri.getPathSegments().get(1);

                numRowsDeleted = databaseHelper.getWritableDatabase().delete(DatabaseContract.NewsFeedEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int itemsUpdated;


        int match = sUriMatcher.match(uri);
        String id;
        switch (match) {
            case LOC_WITH_ID:

                id = uri.getPathSegments().get(1);

                itemsUpdated = databaseHelper.getWritableDatabase().update(DatabaseContract.MapEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            case USER_PROFILE_WITH_ID:

                id = uri.getPathSegments().get(1);

                itemsUpdated = databaseHelper.getWritableDatabase().update(DatabaseContract.UserProfileEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            case USER_FOLLOWER_WITH_ID:

                id = uri.getPathSegments().get(1);

                itemsUpdated = databaseHelper.getWritableDatabase().update(DatabaseContract.UserFollowersEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            case USER_FOLLOWS_WITH_ID:

                id = uri.getPathSegments().get(1);

                itemsUpdated = databaseHelper.getWritableDatabase().update(DatabaseContract.UserFollowsEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            case NEWS_FEED_WITH_ID:

                id = uri.getPathSegments().get(1);

                itemsUpdated = databaseHelper.getWritableDatabase().update(DatabaseContract.NewsFeedEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (itemsUpdated != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }


        return itemsUpdated;
    }
}

