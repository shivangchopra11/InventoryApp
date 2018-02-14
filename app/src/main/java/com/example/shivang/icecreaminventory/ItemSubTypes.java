package com.example.shivang.icecreaminventory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shivang.icecreaminventory.Models.Flavour;
import com.example.shivang.icecreaminventory.Models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;

public class ItemSubTypes extends AppCompatActivity {
    RecyclerView mListView;
    List<Flavour> mFlavourList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String TAG = "TAG";
    private FlavourAdapter mAdapter;
    private ValueEventListener flavourListener;
    private static final int CAMERA_REQUEST = 1888;
    Bitmap curPic;
    ImageView curPicView;
    Button flClick;
    String mName;
    String empName;
    private File output=null;
    private File output1=null;
    public static int ctr=0;
    private Uri curUri;
    private int mCode;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_sub_types);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        mName = i.getStringExtra("item");
        mCode = i.getIntExtra("code",0);
        empName = i.getStringExtra("empName");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        Log.v(TAG,mName);
        mListView = findViewById(R.id.rvFlavours);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.v("EMP",empName);
        mAdapter = new FlavourAdapter(mFlavourList,ItemSubTypes.this,mDatabase,mName,mStorage,empName,mCode);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int num = (int)dpWidth/150;
        int mar = (int) (dpWidth-num);
        int marfin = mar/(num*2);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,num,LinearLayoutManager.VERTICAL,false);
        mListView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final ProgressDialog pd = new ProgressDialog(ItemSubTypes.this);
        pd.setTitle("Recieving Data");
        pd.setMessage("Please wait, data is being recieved");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                pd.dismiss();
//                mAdapter.unregisterAdapterDataObserver(this);
//            }

            @Override
            public void onChanged() {
                super.onChanged();
                if (pd.isShowing() && pd!=null) {
                    pd.dismiss();
                }
            }
        });

        mListView.setAdapter(mAdapter);



//        mListView.addItemDecoration(new DividerItemDecoration(ItemSubTypes.this,DividerItemDecoration.VERTICAL));



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(mCode==1)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(ItemSubTypes.this);
                View alertLayout = inflater.inflate(R.layout.activity_add_subtype, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ItemSubTypes.this);
                builder.setView(alertLayout);
//                final EditText etItem = alertLayout.findViewById(R.id.etItem);
                Button btnAddItem = alertLayout.findViewById(R.id.flDone);
                final EditText etFlName = alertLayout.findViewById(R.id.etFlName);
                final EditText etFlDesc = alertLayout.findViewById(R.id.etFlDesc);
                flClick = alertLayout.findViewById(R.id.flClick);
                curPicView = alertLayout.findViewById(R.id.ivFl);
                final AlertDialog dialog = builder.create();
                btnAddItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Item cur = new Item(etItem.getText().toString());
//                        mItemList.add(etItem.getText().toString());
//                        adapter.notifyDataSetChanged();
                        String name = etFlName.getText().toString();
                        String desc = etFlDesc.getText().toString();
                        //Item cur = new Item(etItem.getText().toString());
//                        mItemList.add(etItem.getText().toString());
//                        adapter.notifyDataSetChanged();
                        if(!name.equals(""))
                            writeNewItem(name,desc);
//                        mItemList.clear();
                        dialog.dismiss();
                    }
                });
                flClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        ctr++;
                        output=new File(dir, ctr+".jpeg");
                        showPictureDialog();

                    }
                });
                dialog.show();

            }
        });
    }
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        Log.v(TAG+"1",requestCode+"");
//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
////            Bitmap photo = BitmapFactory.decodeFile(output.getAbsolutePath());
////            curUri = data.getData();
//            output1=new File(dir, ctr+"s.jpeg");
//            try {
//                output1 = new Compressor(this).compressToFile(output);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Bitmap photo = BitmapFactory.decodeFile(output1.getAbsolutePath());
//            curPic=photo;
//            curUri = Uri.fromFile(output1);
//            curPicView.setImageBitmap(photo);
//            flClick.setVisibility(View.GONE);
//        }
//
//    }



    private int GALLERY = 1, CAMERA = 2;


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
//                        dialog.dismiss();
        startActivityForResult(cameraIntent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    FileOutputStream fo = new FileOutputStream(output);
                    fo.write(bytes.toByteArray());
                    MediaScannerConnection.scanFile(this,
                            new String[]{output.getPath()},
                            new String[]{"image/jpeg"}, null);
                    fo.close();
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    output1=new File(dir, ctr+"s.jpeg");
                    try {
                        output1 = new Compressor(this).compressToFile(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap photo = null;
                    try {
                        photo = new Compressor(this).compressToBitmap(output1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    curPic=photo;
                    curUri = Uri.fromFile(output1);
                    curPicView.setImageBitmap(photo);
                    flClick.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ItemSubTypes.this, "Failed!", Toast.LENGTH_SHORT).show();
                }

            }

        } else if (requestCode == CAMERA) {
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
            flClick.setVisibility(View.GONE);
            Log.v(TAG,curUri.toString());
        }
    }



    private void writeNewItem(final String name, final String desc) {

        StorageReference filePath = mStorage.child("flavours").child(mName+"-"+name);
        final ProgressDialog pd = new ProgressDialog(ItemSubTypes.this);
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
                    Flavour flavour = new Flavour(name,desc);
                    mDatabase.child("items").child(mName).child("flavours").child(name).setValue(flavour);
                    Toast.makeText(ItemSubTypes.this, "Flavour Added", Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        Query myFlavoursQuery = mDatabase.child("items").child(mName).child("flavours");
        flavourListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mFlavourList.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    Flavour flavour = itemSnapshot.getValue(Flavour.class);
                    mFlavourList.add(flavour);
                    Log.w(TAG, mFlavourList.size()+"");
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
        myFlavoursQuery.addValueEventListener(flavourListener);

    }
}
