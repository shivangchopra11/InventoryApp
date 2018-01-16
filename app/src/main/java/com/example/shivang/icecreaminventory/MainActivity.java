package com.example.shivang.icecreaminventory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shivang.icecreaminventory.Models.Flavour;
import com.example.shivang.icecreaminventory.Models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    RecyclerView mListView;
    List<Item> mItemList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String TAG = "TAG";
    private ItemAdapter mAdapter;
    private ValueEventListener itemListener;
    private static final int CAMERA_REQUEST = 1888;
    Bitmap curPic;
    public static int ctr=0;
    ImageView curPicView;
    private File output=null;
    private File output1=null;
    private Button clickItem;
    private Uri curUri;
    private StorageReference mStorage;
    private int mCode;
    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        mCode = i.getIntExtra("code",0);
        if (!EasyPermissions.hasPermissions(this, galleryPermissions)) {
            EasyPermissions.requestPermissions(this, "Access for storage",
                    101, galleryPermissions);
        }
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = findViewById(R.id.lvItems);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAdapter = new ItemAdapter(mItemList,MainActivity.this,mDatabase,mCode,mStorage);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mListView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mAdapter);



        mListView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(mCode==1)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new dialogFragment().setCallBack(dialogCallback).show(MainActivity.this.getSupportFragmentManager(),"");

                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View alertLayout = inflater.inflate(R.layout.activity_add_item, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(alertLayout);
                final EditText etItemName = alertLayout.findViewById(R.id.etItemName);
                final EditText etItemDesc = alertLayout.findViewById(R.id.etItemDesc);
                Button btnAddItem = alertLayout.findViewById(R.id.itemDone);
                curPicView = alertLayout.findViewById(R.id.ivClick);
                clickItem = alertLayout.findViewById(R.id.clickItem);
                final AlertDialog dialog = builder.create();

                btnAddItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String curName = etItemName.getText().toString();
                        String desc = etItemDesc.getText().toString();

                        //Item cur = new Item(etItem.getText().toString());
//                        mItemList.add(etItem.getText().toString());
//                        adapter.notifyDataSetChanged();
//
                        if(!curName.equals("")) {
//                            new AsyncSender(curName,desc,curUri,dialog).execute();
                              writeNewItem(curName,desc,curUri);
                        }
                        dialog.dismiss();
//                            writeNewItem(curName,desc,curUri);
//                        mItemList.clear();

                    }
                });

                clickItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        ctr++;
                        output=new File(dir, ctr+".jpeg");
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
//                        dialog.dismiss();
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);

                    }
                });
                dialog.show();
            }
        });

//        Log.w(TAG, mItemList.size()+"h");

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v(TAG+"1",requestCode+"");
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            output1=new File(dir, ctr+"s.jpeg");
            try {
                output1 = new Compressor(this).compressToFile(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Bitmap photo = BitmapFactory.decodeFile(output1.getAbsolutePath());
            Bitmap photo = null;
            try {
                photo = new Compressor(this).compressToBitmap(output1);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            curUri = data.getData();
            curPic=photo;
            curUri = Uri.fromFile(output1);
            curPicView.setImageBitmap(photo);
            clickItem.setVisibility(View.GONE);
            Log.v(TAG,curUri.toString());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (itemListener != null) {
//            itemListener.removeEventListener(itemListener);
//        }
//
//        // Clean up comments listener
//        mAdapter.cleanupListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query myItemsQuery = mDatabase.child("items");
        itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mItemList.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    mItemList.add(item);
                    Log.w(TAG, mItemList.size()+"");
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                // ...
            }
        };
        myItemsQuery.addValueEventListener(itemListener);

    }

    private void writeNewItem(final String name, final String desc, Uri pic) {
//        DatabaseReference itemRef = mDatabase.child("items");
//        Item item = new Item(name,desc);
//        itemRef.push().setValue(item);

//        mAdapter.notifyDataSetChanged();

        StorageReference filePath = mStorage.child("images").child(name);
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        if(curUri!=null) {
            filePath.putFile(curUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.setTitle("Sending Data");
                    pd.setMessage("Please wait, data is sending");
                    pd.setCancelable(false);
                    pd.setIndeterminate(true);
                    pd.show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    pd.dismiss();
                    Item item = new Item(name,desc);
                    mDatabase.child("items").child(name).setValue(item);
                }
            });
        }



    }

//    private final class AsyncSender extends AsyncTask<Void, Void, Void> {
//
//        ProgressDialog pd;
//        String name;
//        String desc;
//        Uri pic;
//        AlertDialog dialog;
//
//        AsyncSender(String name, String desc,Uri pic,AlertDialog dialog) {
//            this.name = name;
//            this.desc = desc;
//            this.pic = pic;
//            this.dialog = dialog;
//        }
//
//        @Override
//        protected void onPreExecute() {
////            super.onPreExecute();
//
//            pd = new ProgressDialog(MainActivity.this);
//            pd.setTitle("Sending Data");
//            pd.setMessage("Please wait, data is sending");
//            pd.setCancelable(false);
//            pd.setIndeterminate(true);
//            pd.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            // You probably have to try/catch this
//            try {
//                wait(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            StorageReference filePath = mStorage.child("images").child(name);
//            if(curUri!=null) {
//                filePath.putFile(curUri);
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            if (pd != null) {
//                Log.v(TAG,"Upload complete");
//                writeNewItem(name,desc,pic);
//                pd.dismiss();
//                dialog.dismiss();
//                mAdapter.notifyDataSetChanged();
//            }
////            pd.dismiss();
////            super.onPostExecute(result);
//
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
