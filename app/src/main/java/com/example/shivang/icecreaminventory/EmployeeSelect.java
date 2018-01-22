package com.example.shivang.icecreaminventory;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.shivang.icecreaminventory.Models.Employee;
import com.example.shivang.icecreaminventory.Models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSelect extends AppCompatActivity {
    RecyclerView mListView;
    List<Employee> mEmpList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String TAG = "TAG";
    private EmpAdapter mAdapter;
    private ValueEventListener empListener;
    private int mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_select);
        mListView = findViewById(R.id.lvEmployees);
        mCode = getIntent().getIntExtra("code",0);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAdapter = new EmpAdapter(mEmpList,EmployeeSelect.this,mCode);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EmployeeSelect.this);
        mListView.setLayoutManager(mLayoutManager);
        final ProgressDialog pd = new ProgressDialog(EmployeeSelect.this);
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
//        mListView.addItemDecoration(new DividerItemDecoration(EmployeeSelect.this,DividerItemDecoration.VERTICAL));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Query myItemsQuery = mDatabase.child("employees");
        empListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mEmpList.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    Employee emp = itemSnapshot.getValue(Employee.class);
                    mEmpList.add(emp);
                    Log.w(TAG, mEmpList.size()+"");
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
        myItemsQuery.addValueEventListener(empListener);
    }
}
