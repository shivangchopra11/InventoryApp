package com.example.shivang.icecreaminventory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shivang.icecreaminventory.Models.Employee;
import com.example.shivang.icecreaminventory.Models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by shivang on 20/01/18.
 */

class EmpAdapter extends RecyclerView.Adapter<EmpAdapter.MyViewHolder> {
    private List<Employee> mEmps;
    private Context mContext;
    private String TAG = "TAG";
    private int mCode;

    static ViewGroup parent;

    public EmpAdapter(List<Employee> mEmps, Context mContext,int code) {
        this.mEmps = mEmps;
        this.mContext = mContext;
        this.mCode = code;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_employee, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Employee emp = mEmps.get(position);
        holder.tvEmpName.setText(emp.getName());
        holder.tvEmpQty.setText(emp.getQty()+"");



    }

    @Override
    public int getItemCount() {
        return mEmps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmpName,tvEmpQty;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvEmpName = itemView.findViewById(R.id.tvEmpName);
            tvEmpQty = itemView.findViewById(R.id.tvEmpQty);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCode==0) {
                        Intent i = new Intent(mContext,MainActivity.class);
                        i.putExtra("empName",mEmps.get(getLayoutPosition()).getName());
                        i.putExtra("code",1);
                        mContext.startActivity(i);
                    }
                    else {
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        View alertLayout = inflater.inflate(R.layout.check_pass_emp, null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(alertLayout);
                        final EditText empPass = alertLayout.findViewById(R.id.etEmpPass1);
                        Button submit = alertLayout.findViewById(R.id.empDone1);
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Query myEmpQuery1 = mDatabase.child("employees").child(mEmps.get(getLayoutPosition()).getName()).child("pass");
                                myEmpQuery1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String cur = dataSnapshot.getValue(String.class);
                                        if(cur.equals(empPass.getText().toString())) {
                                            Intent i = new Intent(mContext,MainActivity.class);
                                            i.putExtra("empName",mEmps.get(getLayoutPosition()).getName());
                                            i.putExtra("code",1);
                                            dialog.dismiss();
                                            mContext.startActivity(i);
                                        }
                                        else {
                                            empPass.setError("Wrong password entered");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                                    }

                                });
                            }
                        });


                    }



                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mCode==0) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                mContext).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Delete Employee");

                        // Setting Dialog Message
                        alertDialog.setMessage("Do you want to delete this employee");


                        // Setting OK Button
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("employees").child(mEmps.get(getLayoutPosition()).getName()).setValue(null);
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();
                    }


                    return true;
                }
            });
        }
    }
}
