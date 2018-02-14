package com.example.shivang.icecreaminventory;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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

import com.example.shivang.icecreaminventory.Models.Employee;
import com.example.shivang.icecreaminventory.Models.Flavour;
import com.example.shivang.icecreaminventory.Models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;
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
    private String mEmpName="";
    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        Intent intent = new Intent("someIntent");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1111, intent, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 12);
//        calendar.set(Calendar.MINUTE,5);
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+6*1000,
//                60*1000, pendingIntent);

//            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10*1000), pendingIntent);



        Intent i = getIntent();
        mCode = i.getIntExtra("code",0);
        if(mCode==1) {
            mEmpName = i.getStringExtra("empName");
            Log.v("EMP",mEmpName);
        }
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
        mAdapter = new ItemAdapter(mItemList,MainActivity.this,mDatabase,mCode,mStorage,mEmpName);
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
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
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



//        mListView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));

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
                        showPictureDialog();

                    }
                });
                dialog.show();
            }
        });

//        Log.w(TAG, mItemList.size()+"h");

    }

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
                    clickItem.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
            clickItem.setVisibility(View.GONE);
            Log.v(TAG,curUri.toString());
        }
    }



//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        Log.v(TAG+"1",requestCode+"");
//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//            output1=new File(dir, ctr+"s.jpeg");
//            try {
//                output1 = new Compressor(this).compressToFile(output);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            Bitmap photo = BitmapFactory.decodeFile(output1.getAbsolutePath());
//            Bitmap photo = null;
//            try {
//                photo = new Compressor(this).compressToBitmap(output1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            curUri = data.getData();
//            curPic=photo;
//            curUri = Uri.fromFile(output1);
//            curPicView.setImageBitmap(photo);
//            clickItem.setVisibility(View.GONE);
//            Log.v(TAG,curUri.toString());
//        }
//
//    }


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
                    Toast.makeText(MainActivity.this, "Item Added", Toast.LENGTH_LONG).show();
                }
            });
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mCode==0) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addEmployee) {
            if(mCode==0) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View alertLayout = inflater.inflate(R.layout.activity_add_user, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(alertLayout);
                final EditText etEmpName = alertLayout.findViewById(R.id.etEmpName);
                final EditText etEmpPass = alertLayout.findViewById(R.id.etEmpPass);
                Button btnAddEmp = alertLayout.findViewById(R.id.empDone);
                final AlertDialog dialog = builder.create();
                btnAddEmp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String empName = etEmpName.getText().toString();
                        String empPass = etEmpPass.getText().toString();
                        if(!empName.equals("")) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            Employee employee = new Employee(empName,0,empPass);
                            mDatabase.child("employees").child(empName).setValue(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                }
                            });

                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        }
        else if(id == R.id.showEmps) {
            if(mCode==0) {
                Intent i = new Intent(MainActivity.this,EmployeeSelect.class);
                i.putExtra("code",0);
                startActivity(i);
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
