package com.my.nitt_mess_manager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_manager.Class.Leave;
import com.my.nitt_mess_manager.R;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class StudentsLeaveRequestAdapter extends RecyclerView.Adapter<StudentsLeaveRequestAdapter.ViewHolder> {

    List<Leave> leaveList;
    Context context;

    public StudentsLeaveRequestAdapter(List<Leave> leaveList, Context context) {

        this.leaveList = leaveList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.leave_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Leave leave = leaveList.get(position);
        holder.textViewLeaveReason.setText(leave.getReason());
        holder.textViewFrom.setText(leave.getFrom());
        holder.textViewTo.setText(leave.getTo());
        holder.UserRollNo.setText(leave.getRollNo());
        holder.textViewLeaveStatus.setText(leave.getStatus());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("AllUser").child(leave.getRollNo());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    holder.textViewUserName.setText(dataSnapshot.child("Student Name").getValue().toString());
                    holder.UserMobile.setText(dataSnapshot.child("Contact No").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (leaveList==null)
            return 0;
        return leaveList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewLeaveReason, textViewFrom, textViewTo, textViewUserName;
        TextView UserRollNo, UserMobile, textViewLeaveStatus;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLeaveReason = itemView.findViewById(R.id.textViewLeaveReason);
            textViewFrom = itemView.findViewById(R.id.textViewFrom);
            textViewTo = itemView.findViewById(R.id.textViewTo);
            textViewUserName = itemView.findViewById(R.id.UserName);
            UserRollNo = itemView.findViewById(R.id.UserRollNo);
            UserMobile = itemView.findViewById(R.id.UserMobile);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            cardView = itemView.findViewById(R.id.cardView);
            textViewLeaveStatus = itemView.findViewById(R.id.textViewLeaveStatus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(view.getContext(), leaveList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }
}