package com.tkd.twinkledhanak.intellidj.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by twinkle dhanak on 9/20/2017.
 */

public class LocalDatabaseHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "localRecordsForUser.db";


    // sapid made as unique key to avoid duplicate records
    public static final String CREATE_USER = "CREATE TABLE IF NOT EXISTS user (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "cloud_id TEXT," +
            "name TEXT," + "email TEXT,"  + "department TEXT," + "type TEXT," + "year TEXT,"
            +"image TEXT," +"sapid TEXT" +
            ")";


    public static final String CREATE_ANSWER = "CREATE TABLE IF NOT EXISTS answer (" +
            "id integer PRIMARY KEY," +
            "cloud_id TEXT," +
            "content TEXT,"  +"question_id integer(10)," + "user_id integer(10),"
            +"upvote integer(10)" +
            ")";

    public static final String CREATE_UPVOTELIST = "CREATE TABLE IF NOT EXISTS upvotelist (" +
            "id integer PRIMARY KEY," +
            "answer_id TEXT" +
            ")";


    public static final String CREATE_BOOKMARK = "CREATE TABLE IF NOT EXISTS bookmark (" +
            "id integer PRIMARY KEY," +
            "question_id TEXT" +
            ")";

    public static final String CREATE_NOTICE = "CREATE TABLE IF NOT EXISTS notice (" +
            "id integer PRIMARY KEY," +
            "notice_id TEXT" +
            ")";

    public static final String CREATE_DOCUMENT = "CREATE TABLE IF NOT EXISTS document (" +
            "id integer PRIMARY KEY," +
            "document_id TEXT" +
            ")";

    public LocalDatabaseHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) throws SQLiteException{ // CALLED WHEN DB IS CREATED FOR FIRST TIME


        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_ANSWER);
        db.execSQL(CREATE_BOOKMARK);
        db.execSQL(CREATE_NOTICE);
        db.execSQL(CREATE_UPVOTELIST);
        db.execSQL(CREATE_DOCUMENT);

        db.setLockingEnabled(false);
        db.execSQL("PRAGMA read_uncommitted = true;");
      //  db.execSQL("PRAGMA synchronous=OFF");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
