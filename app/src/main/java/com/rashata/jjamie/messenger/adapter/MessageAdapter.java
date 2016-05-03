package com.rashata.jjamie.messenger.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rashata.jjamie.messenger.R;
import com.rashata.jjamie.messenger.manager.Message;

import java.util.ArrayList;

/**
 * Created by JJamie on 4/18/16 AD.
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String usernameFriend;
    ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages, String usernameFriend) {
        this.messages = messages;
        this.usernameFriend = usernameFriend;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Message.TYPE_ME:
                View v_me = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_me, parent, false);
                MessageMeHolder mmh = new MessageMeHolder(v_me);
                return mmh;
            case Message.TYPE_FRIEND:
                View v_fri = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_fri, parent, false);
                MessageFriendHolder mfh = new MessageFriendHolder(v_fri);
                return mfh;
            default:
                View v_date = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_date, parent, false);
                MessageDateHolder mdh = new MessageDateHolder(v_date);
                return mdh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message m = messages.get(position);
        switch (messages.get(position).type) {
            case Message.TYPE_ME:
                MessageMeHolder mmh = (MessageMeHolder) holder;
                mmh.txt_contacts_me.setText(m.message);
                mmh.txt_time_me.setText(m.time);
                break;
            case Message.TYPE_FRIEND:
                MessageFriendHolder mfh = (MessageFriendHolder) holder;
                mfh.txt_contacts_fri.setText(m.message);
                mfh.txt_time_fri.setText(m.time);
                break;
            default:
                MessageDateHolder mdh = (MessageDateHolder) holder;
                mdh.txt_message_date.setText(m.date);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageMeHolder extends RecyclerView.ViewHolder {
        TextView txt_contacts_me;
        TextView txt_time_me;

        public MessageMeHolder(View itemView) {
            super(itemView);
            txt_contacts_me = (TextView) itemView.findViewById(R.id.txt_contacts_me);
            txt_time_me = (TextView) itemView.findViewById(R.id.txt_time_me);

        }
    }

    public static class MessageFriendHolder extends RecyclerView.ViewHolder {
        TextView txt_contacts_fri;
        TextView txt_time_fri;

        public MessageFriendHolder(View itemView) {
            super(itemView);
            txt_contacts_fri = (TextView) itemView.findViewById(R.id.txt_contacts_fri);
            txt_time_fri = (TextView) itemView.findViewById(R.id.txt_time_fri);

        }
    }

    public static class MessageDateHolder extends RecyclerView.ViewHolder {
        TextView txt_message_date;

        public MessageDateHolder(View itemView) {
            super(itemView);
            txt_message_date = (TextView) itemView.findViewById(R.id.txt_message_date);
        }
    }


}
