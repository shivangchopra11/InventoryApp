package com.example.shivang.icecreaminventory;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shivang.icecreaminventory.Models.Item;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

/**
 * Created by shivang on 08/01/18.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private List<Item> mItems;
    private Context mContext;
    private String TAG = "TAG";
    DatabaseReference mDatabase;
    private int mCode;

    public ItemAdapter(List<Item> items, final Context mContext, DatabaseReference ref,int code) {
        mItems = items;
        this.mContext = mContext;
        this.mDatabase=ref;
        this.mCode=code;
        Query myItemsQuery = mDatabase.child("items");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new item has been added, add it to the displayed list
//                Item item = dataSnapshot.getValue(Item.class);
//                mItems.add(item);
//                notifyItemInserted(mItems.size()-1);
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
//                Item newItem = dataSnapshot.getValue(Item.class);
//                String itemKey = dataSnapshot.getKey();


                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String itemKey = dataSnapshot.getKey();


                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A item has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
//                Comment movedComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load items.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        myItemsQuery.addChildEventListener(childEventListener);

    }
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.MyViewHolder holder, int position) {
        Item item = mItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDesc());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    static ViewGroup parent;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgItem;
        TextView tvDesc;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            imgItem = itemView.findViewById(R.id.imgItem);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext,ItemSubTypes.class);
                    i.putExtra("item",mItems.get(getLayoutPosition()).getName());
                    i.putExtra("code",mCode);
                    mContext.startActivity(i);
                }
            });

        }
    }
}