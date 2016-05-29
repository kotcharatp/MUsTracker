package com.bustracker.mustracker.Database;

/**
 * Created by Cat on 22-May-16.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommentDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;


    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ROUTE,
            MySQLiteHelper.COLUMN_STATION,
            MySQLiteHelper.COLUMN_TIME,
            MySQLiteHelper.COLUMN_NOTIFYDAY,
            MySQLiteHelper.COLUMN_NOTIFYTIME };

    public CommentDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //if click add new in the program will go to the function "createComment"
    public Comment createComment(String route, String station, String time, String notifyday, String notifytime) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ROUTE, route);
        values.put(MySQLiteHelper.COLUMN_STATION, station);
        values.put(MySQLiteHelper.COLUMN_TIME, time);
        values.put(MySQLiteHelper.COLUMN_NOTIFYDAY, notifyday);
        values.put(MySQLiteHelper.COLUMN_NOTIFYTIME, notifytime);

        long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,null, null, null);
        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteComment(Comment comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<Comment>();

        //Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS, allColumns, null, null, null, null, null);
        Cursor cursor = database.rawQuery("SELECT * FROM comments", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Comment comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Comment cursorToComment(Cursor cursor) {
        Comment comment = new Comment();

        comment.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        //comment.setComment(cursor.getString(1));
        comment.setRoute(cursor.getString(1));
        comment.setStation(cursor.getString(2));
        comment.setTime(cursor.getString(3));
        comment.setNotifyday(cursor.getString(4));
        comment.setNotifyTime(cursor.getString(5));

        return comment;
    }
}
