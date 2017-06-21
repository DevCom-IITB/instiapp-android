package in.ac.iitb.gymkhana.iitbapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MapDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mapDb.db";
    private static final int VERSION = 1;

    MapDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + DatabaseContract.MapEntry.TABLE_NAME + " (" +
                DatabaseContract.MapEntry._ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.MapEntry.COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                DatabaseContract.MapEntry.COLUMN_LONGITUDE + " DOUBLE NOT NULL, " +
                DatabaseContract.MapEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DatabaseContract.MapEntry.COLUMN_TYPE + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MapEntry.TABLE_NAME);
        onCreate(db);
    }
}
