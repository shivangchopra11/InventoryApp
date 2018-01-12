package com.example.shivang.icecreaminventory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.shivang.icecreaminventory.Models.Flavour;
import com.example.shivang.icecreaminventory.Models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_sub_types);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        mName = i.getStringExtra("item");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.v(TAG,mName);
        mListView = findViewById(R.id.rvFlavours);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAdapter = new FlavourAdapter(mFlavourList,ItemSubTypes.this,mDatabase,mName);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ItemSubTypes.this);
        mListView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mAdapter);



        mListView.addItemDecoration(new DividerItemDecoration(ItemSubTypes.this,DividerItemDecoration.VERTICAL));



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                        writeNewItem(name,desc);
//                        mItemList.clear();
                        dialog.dismiss();
                    }
                });
                flClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });
                dialog.show();

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v(TAG+"1",requestCode+"");
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            curUri = (Uri) data.getExtras().get("data");
//            imageView.setImageBitmap(photo);
//            curUri = getImageUri(MainActivity.this,photo);
            curPic=photo;
            curPicView.setImageBitmap(photo);
            flClick.setVisibility(View.GONE);
//            Log.v(TAG,curUri.toString());
        }

    }
    private void writeNewItem(String name, String desc) {
        Flavour flavour = new Flavour(name,desc);
        mDatabase.child("items").child(mName).child("flavours").child(name).setValue(flavour);
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
