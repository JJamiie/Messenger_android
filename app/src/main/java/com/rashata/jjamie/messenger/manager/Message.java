package com.rashata.jjamie.messenger.manager;

/**
 * Created by JJamie on 4/21/16 AD.
 */
public class Message {

    public static final int TYPE_ME = 0;
    public static final int TYPE_FRIEND = 1;
    public static final int TYPE_DATE = 2;

    public String seqno;
    public String fromuser;
    public String touser;
    public String message;
    public String date;
    public String time;
    public int type;
}
