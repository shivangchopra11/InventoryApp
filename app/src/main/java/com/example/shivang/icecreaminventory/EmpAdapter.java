package com.example.shivang.icecreaminventory;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shivang.icecreaminventory.Models.Employee;
import com.example.shivang.icecreaminventory.Models.Item;
import com.google.firebase.database.DatabaseReference;

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
                        Intent i = new Intent(mContext,MainActivity.class);
                        i.putExtra("empName",mEmps.get(getLayoutPosition()).getName());
                        i.putExtra("code",1);
                        mContext.startActivity(i);


                }
            });
        }
    }
}
