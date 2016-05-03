package com.rashata.jjamie.messenger.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "NoteDB.sqlite";
    public static final String TABLE_SESSION = "Session";
    public static final String COL_SESSION_USERNAME = "username";
    public static final String COL_SESSION_PASSWORD = "password";
    public static final String COL_SESSION_SESSIONID = "sessionId";

    public static final String TABLE_MESSAGE = "Message";
    public static final String COL_MESSAGE_SEQNO = "seqno";
    public static final String COL_MESSAGE_FROMUSER = "fromuser";
    public static final String COL_MESSAGE_TOUSER = "touser";
    public static final String COL_MESSAGE_MESSAGE = "message";
    public static final String COL_MESSAGE_DATE = "date";
    public static final String COL_MESSAGE_TIME = "time";


    public static final String TABLE_CONTACT = "Contact";
    public static final String COL_CONTACT_SESSIONID = "sessionId";
    public static final String COL_CONTACT_USERNAMEFRIEND = "usernamefriend";

    public static final int DB_VER = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String sql;
        sql = ("CREATE TABLE " + TABLE_SESSION + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SESSION_USERNAME + " TEXT, "
                + COL_SESSION_PASSWORD + " TEXT, "
                + COL_SESSION_SESSIONID + " TEXT);");
        database.execSQL(sql);

        sql = ("CREATE TABLE " + TABLE_MESSAGE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MESSAGE_SEQNO + " TEXT, "
                + COL_MESSAGE_FROMUSER + " TEXT, "
                + COL_MESSAGE_TOUSER + " TEXT, "
                + COL_MESSAGE_MESSAGE + " TEXT, "
                + COL_MESSAGE_DATE + " TEXT, "
                + COL_MESSAGE_TIME + " TEXT);");
        database.execSQL(sql);

        sql = ("CREATE TABLE " + TABLE_CONTACT + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SESSION_SESSIONID + " TEXT, "
                + COL_CONTACT_USERNAMEFRIEND + " TEXT);");
        database.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldver, int newver) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        onCreate(database);
    }

}


