package com.example.shivang.icecreaminventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shivang.icecreaminventory.Models.Flavour;
import com.example.shivang.icecreaminventory.Models.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shivang on 12/01/18.
 */

public class FlavourAdapter extends RecyclerView.Adapter<FlavourAdapter.MyViewHolder> {
    private List<Flavour> mFlavours;
    private Context mContext;
    private String TAG = "TAG";
    DatabaseReference mDatabase;
    StorageReference mStorage;
    String itemName;
    public static final HashMap<String,SoftReference<Bitmap>> flavourCache =
            new HashMap<String,SoftReference<Bitmap>>();

    public FlavourAdapter(List<Flavour> flavours, final Context mContext, DatabaseReference ref,String name,StorageReference reference) {
        mFlavours = flavours;
        this.mContext = mContext;
        this.mDatabase=ref;
        this.mStorage = reference;
        itemName = name;
        Query myItemsQuery = mDatabase.child("items").child(name).child("flavours");
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
    public FlavourAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FlavourAdapter.MyViewHolder holder, int position) {
        final Flavour flavour = mFlavours.get(position);
        holder.tvName.setText(flavour.getFlName());
        holder.tvDesc.setText(flavour.getFlDesc());
        if(flavourCache.containsKey(itemName+"-"+flavour.getFlName())) {
            Log.d(TAG,"Caching Done");
            Bitmap myBitmap = flavourCache.get(itemName+"-"+flavour.getFlName()).get();
//                Bitmap myBitmap = imageLoader.loadImageSync(finalLocalFile.getAbsolutePath());
            holder.imgItem.setImageBitmap(myBitmap);
            return;
        }
        StorageReference filePath = mStorage.child("flavours").child(itemName+"-"+flavour.getFlName());
        File localFile = new File(Environment.getExternalStorageDirectory(),itemName+"-"+flavour.getFlName()+".jpg");
        try {
            localFile = File.createTempFile(itemName+"-"+flavour.getFlName(), "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File finalLocalFile = localFile;
        filePath.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Bitmap myBitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                holder.imgItem.setImageBitmap(myBitmap);
                SoftReference softRef = new SoftReference(myBitmap);
                flavourCache.put(itemName+"-"+flavour.getFlName(),softRef);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }



    @Override
    public int getItemCount() {
        return mFlavours.size();
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
//                    Intent i = new Intent(mContext,ItemSubTypes.class);
//                    i.putExtra("item",mFlavours.get(getLayoutPosition()).getFlName());
//                    mContext.startActivity(i);
                    final int pos = getLayoutPosition();
                    final String name = mFlavours.get(pos).getFlName();
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    View alertLayout = inflater.inflate(R.layout.activity_change_qty, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(alertLayout);
                    final TextView tvQty = alertLayout.findViewById(R.id.tvQty);
                    tvQty.setText(mFlavours.get(pos).getFlQty()+"");
                    Button addQty = alertLayout.findViewById(R.id.addQty);
                    Button subQty = alertLayout.findViewById(R.id.subQty);
                    final AlertDialog dialog = builder.create();
                    Button btnSubQty = alertLayout.findViewById(R.id.btnSubQty);
                    addQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String qty = tvQty.getText().toString();
                            int qt = Integer.parseInt(qty);
                            qt++;
                            tvQty.setText(qt+"");
                        }
                    });
                    subQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String qty = tvQty.getText().toString();
                            int qt = Integer.parseInt(qty);
                            if(qt>0)
                                qt--;
                            tvQty.setText(qt+"");
                        }
                    });
                    btnSubQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String qty = tvQty.getText().toString();
                            int qt = Integer.parseInt(qty);
                            mDatabase.child("items").child(itemName).child("flavours").child(name).child("flQty").setValue(qt);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }
            });

        }
    }
}