package com.rashata.jjamie.messenger.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rashata.jjamie.messenger.util.DBHelper;

import java.util.ArrayList;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MessageModel {

    private static MessageModel instance;

    public static MessageModel getInstance() {
        if (instance == null)
            instance = new MessageModel();
        return instance;
    }

    private Context mContext;

    private MessageModel() {
        mContext = Contextor.getInstance().getContext();
    }


    public ArrayList<Message> loadMessage(String usernameFriend) {
        ArrayList<Message> messages = new ArrayList<Message>();
        DBHelper dbhelper = new DBHelper(mContext);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT * FROM " + DBHelper.TABLE_MESSAGE +
                " WHERE " + DBHelper.COL_MESSAGE_TOUSER + "='" + usernameFriend + "'" + " OR "
                + DBHelper.COL_MESSAGE_FROMUSER + "='" + usernameFriend + "' ORDER BY seqno";
        Cursor sqlList = db.rawQuery(sql, null);
        if (sqlList.moveToFirst()) {
            do {
                Message msg = new Message();
                msg.seqno = sqlList.getString(sqlList.getColumnIndex(DBHelper.COL_MESSAGE_SEQNO));
                msg.fromuser = sqlList.getString(sqlList.getColumnIndex(DBHelper.COL_MESSAGE_FROMUSER));
                msg.touser = sqlList.getString(sqlList.getColumnIndex(DBHelper.COL_MESSAGE_TOUSER));
                msg.message = sqlList.getString(sqlList.getColumnIndex(DBHelper.COL_MESSAGE_MESSAGE));
                msg.date = sqlList.getString(sqlList.getColumnIndex(DBHelper.COL_MESSAGE_DATE));
                msg.time = sqlList.getString(sqlList.getColumnIndex(DBHelper.COL_MESSAGE_TIME));
                messages.add(msg);
            } while (sqlList.moveToNext());
        }
        sqlList.close();
        db.close();
        return messages;
    }

    public String getLastSequence() {
        DBHelper dbhelper = new DBHelper(mContext);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT * FROM " + DBHelper.TABLE_MESSAGE + " ORDER BY seqno";
        Cursor sqlList = db.rawQuery(sql, null);
        String seq = "0";
        if(sqlList.moveToLast()) {
            seq = sqlList.getString(sqlList.getColumnIndex(DBHelper.COL_MESSAGE_SEQNO));
        }
        sqlList.close();
        db.close();
        return seq;
    }

    public void addMessage(Message msg) {
        DBHelper dbhelper = new DBHelper(mContext);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_MESSAGE + " (seqno,fromuser,touser,message,date,time) VALUES('" + msg.seqno
                + "','" + msg.fromuser + "','" + msg.touser + "','" + msg.message + "','" + msg.date + "','" + msg.time + "')";
        db.execSQL(sql);
        db.close();
    }

}