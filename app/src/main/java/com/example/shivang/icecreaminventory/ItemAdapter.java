package com.example.shivang.icecreaminventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shivang on 08/01/18.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private List<Item> mItems;
    private Context mContext;
    private String TAG = "TAG";
    DatabaseReference mDatabase;
    StorageReference mStorage;
    private int mCode;
    public static final HashMap<String,SoftReference<Bitmap>> imageCache =
            new HashMap<String,SoftReference<Bitmap>>();
//    File cacheDir;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public ItemAdapter(List<Item> items, final Context mContext, DatabaseReference ref, int code, StorageReference storage) {
//        imageCache.clear();
        mItems = items;
        this.mContext = mContext;
        this.mDatabase=ref;
        this.mCode=code;
        this.mStorage = storage;
//        cacheDir = StorageUtils.getCacheDirectory(mContext);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        // Create configuration for ImageLoader (all options are optional)
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
//                // You can pass your own memory cache implementation
//                // You can pass your own disc cache implementation
//                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
//                .build();
//        // Initialize ImageLoader with created configuration. Do it once.
//        imageLoader.init(config);
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.cherry)//display stub image
//                .cacheInMemory()
//                .cacheOnDisc()
//                .displayer(new RoundedBitmapDisplayer(20))
//                .build();
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
    public void onBindViewHolder(final ItemAdapter.MyViewHolder holder, int position) {
        final Item item = mItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDesc());
        if(imageCache.containsKey(item.getName())) {
            Log.d(TAG,"Caching Done");
            Bitmap myBitmap = imageCache.get(item.getName()).get();
//                Bitmap myBitmap = imageLoader.loadImageSync(finalLocalFile.getAbsolutePath());
            holder.imgItem.setImageBitmap(myBitmap);
            return;
        }
        StorageReference filePath = mStorage.child("images").child(item.getName());
        File localFile = new File(Environment.getExternalStorageDirectory(),item.getName());
        try {
            localFile = File.createTempFile(item.getName(), ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File finalLocalFile = localFile;
        filePath.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Bitmap myBitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                Log.d(TAG,finalLocalFile.getAbsolutePath());
//                Bitmap myBitmap = imageLoader.loadImageSync(finalLocalFile.getAbsolutePath());
                holder.imgItem.setImageBitmap(myBitmap);
                SoftReference softRef = new SoftReference(myBitmap);
                imageCache.put(item.getName(),softRef);
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