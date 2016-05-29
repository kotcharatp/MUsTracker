package com.bustracker.mustracker.Database;

/**
 * Created by kotcharat on 5/23/16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_COMMENTS = "comments";
    public static final String COLUMN_ID = "_id";
    //public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_ROUTE = "route";
    public static final String COLUMN_STATION = "station";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_NOTIFYDAY = "notifyday";
    public static final String COLUMN_NOTIFYTIME = "notifytime";

    private static final String DATABASE_NAME = "commments.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_COMMENTS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_ROUTE + " text not null, " +
            COLUMN_STATION + " text, " +
            COLUMN_TIME + " text, " +
            COLUMN_NOTIFYDAY + " text, " +
            COLUMN_NOTIFYTIME + " text" + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    } //when the program runs will runs this command first and table will be created

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(db);
    }
    // is it the same version? it will create. if it is a new one it will update it.
}
