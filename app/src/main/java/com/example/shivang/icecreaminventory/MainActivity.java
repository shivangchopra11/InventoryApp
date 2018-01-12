package com.example.shivang.icecreaminventory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView mListView;
    List<Item> mItemList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String TAG = "TAG";
    private ItemAdapter mAdapter;
    private ValueEventListener itemListener;
    private static final int CAMERA_REQUEST = 1888;
    Bitmap curPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = findViewById(R.id.lvItems);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAdapter = new ItemAdapter(mItemList,MainActivity.this,mDatabase);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mListView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mAdapter);



        mListView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
//
//        mListView.setItemAnimator(new DefaultItemAnimator());


//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(MainActivity.this,ItemSubTypes.class);
//                i.putExtra("item",mItemList.get(position));
//                startActivity(i);
//            }
//        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new dialogFragment().setCallBack(dialogCallback).show(MainActivity.this.getSupportFragmentManager(),"");
//                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
//                View alertLayout = inflater.inflate(R.layout.activity_add_item, null);
//                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setView(alertLayout);
//                final EditText etItemName = alertLayout.findViewById(R.id.etItemName);
//                final EditText etItemDesc = alertLayout.findViewById(R.id.etItemDesc);
//                Button btnAddItem = alertLayout.findViewById(R.id.itemDone);
//                Button clickItem = alertLayout.findViewById(R.id.clickItem);
//                final AlertDialog dialog = builder.create();
//                btnAddItem.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String name = etItemName.getText().toString();
//                        String desc = etItemDesc.getText().toString();
//                        //Item cur = new Item(etItem.getText().toString());
////                        mItemList.add(etItem.getText().toString());
////                        adapter.notifyDataSetChanged();
//                        writeNewItem(name,desc);
////                        mItemList.clear();
//                        dialog.dismiss();
//                    }
//                });
//                clickItem.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                    }
//                });
//                dialog.show();
            }
        });

//        Log.w(TAG, mItemList.size()+"h");

    }



    DialogCallback dialogCallback = new DialogCallback() {
        @Override
        public void getResults(int results, ImageView iv) {
//            Log.v(TAG,CAMERA_REQUEST+"");
//            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(cameraIntent, CAMERA_REQUEST);

//            if(results==CAMERA_REQUEST){
//                iv.setImageBitmap(curPic);
//            }
            iv.setImageBitmap(curPic);
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v(TAG+"1",requestCode+"");
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(photo);
            curPic=photo;
            Log.v(TAG,curPic.toString());
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

    private void writeNewItem(String name, String desc) {
//        DatabaseReference itemRef = mDatabase.child("items");
//        Item item = new Item(name,desc);
//        itemRef.push().setValue(item);
        Item item = new Item(name,desc);
        mDatabase.child("items").child(name).setValue(item);
    }

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
