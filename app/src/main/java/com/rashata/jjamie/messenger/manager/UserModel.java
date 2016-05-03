package com.rashata.jjamie.messenger.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rashata.jjamie.messenger.util.DBHelper;

import java.util.ArrayList;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class UserModel {

    private static UserModel instance;

    public static UserModel getInstance() {
        if (instance == null)
            instance = new UserModel();
        return instance;
    }

    private Context mContext;

    private UserModel() {
        mContext = Contextor.getInstance().getContext();
    }

    public void addSessionId(String username, String password, String sessionId) {
        DBHelper dbhelper = new DBHelper(mContext);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_SESSION + " (username,password,sessionId) VALUES('" + username + "','" + password + "','" + sessionId + "')";
        db.execSQL(sql);
        db.close();
    }

    public String loadSessionId(String username, String password) {
        DBHelper dbhelper = new DBHelper(mContext);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT " + DBHelper.COL_SESSION_SESSIONID + " FROM " + DBHelper.TABLE_SESSION
                + " WHERE " + DBHelper.COL_SESSION_USERNAME + "='" + username + "'" + " AND "
                + DBHelper.COL_SESSION_PASSWORD + "='" + password + "'";
        Cursor cursor = db.rawQuery(sql, null);
        String sessionId = null;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            sessionId = cursor.getString(cursor.getColumnIndex(DBHelper.COL_SESSION_SESSIONID));
        }

        cursor.close();
        db.close();
        return sessionId;

    }

    public ArrayList<String> getContact(String sessionId) {
        ArrayList<String> friends = new ArrayList<String>();
        DBHelper dbhelper = new DBHelper(mContext);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT " + DBHelper.COL_CONTACT_USERNAMEFRIEND + " FROM " + DBHelper.TABLE_CONTACT
                + " WHERE " + DBHelper.COL_CONTACT_SESSIONID + "='" + sessionId + "'";
        Cursor sqlList = db.rawQuery(sql, null);
        sqlList.moveToFirst();
        if (sqlList.moveToFirst()) {
            do {
                String friend = sqlList.getString(sqlList.getColumnIndex(DBHelper.COL_CONTACT_USERNAMEFRIEND));
                friends.add(friend);
            } while (sqlList.moveToNext());
        }
        sqlList.close();
        db.close();

        return friends;
    }

    public void addContact(String sessionId, String friendID) {
        DBHelper dbhelper = new DBHelper(mContext);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_CONTACT + " (sessionId,usernamefriend) VALUES('" + sessionId
                + "','" + friendID + "')";
        db.execSQL(sql);
        System.out.println("add contactttttttttttttt");
        db.close();
    }




}
