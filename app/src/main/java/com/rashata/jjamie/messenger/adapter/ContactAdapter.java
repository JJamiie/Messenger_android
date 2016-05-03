package com.rashata.jjamie.messenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rashata.jjamie.messenger.R;
import com.rashata.jjamie.messenger.activity.MessageActivity;

import java.util.ArrayList;

/**
 * Created by JJamie on 4/18/16 AD.
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> contactList;
    Context mContext;
    String sessionId;

    public ContactAdapter(ArrayList<String> contactList, Context context, String sessionId) {
        this.contactList = contactList;
        this.mContext = context;
        this.sessionId = sessionId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        ContactHolder ch = new ContactHolder(view);
        return ch;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactHolder ch = (ContactHolder) holder;
        ch.txt_contacts_username.setText(contactList.get(position));
        ch.rel_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMessageActivity(ch.txt_contacts_username.getText().toString());
            }
        });
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {
        TextView txt_contacts_username;
        RelativeLayout rel_contact;

        ContactHolder(View itemView) {
            super(itemView);
            txt_contacts_username = (TextView) itemView.findViewById(R.id.txt_contacts_username);
            rel_contact = (RelativeLayout) itemView.findViewById(R.id.rel_contact);
        }


    }

    public void openMessageActivity(String usernameFriend) {
        Intent intent = new Intent(mContext, MessageActivity.class);
        intent.putExtra("usernameFriend", usernameFriend);
        intent.putExtra("sessionId", sessionId);
        mContext.startActivity(intent);
    }


}
