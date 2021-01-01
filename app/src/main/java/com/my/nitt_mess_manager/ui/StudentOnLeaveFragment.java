package com.my.nitt_mess_manager.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_manager.Adapter.StudentsLeaveRequestAdapter;
import com.my.nitt_mess_manager.Class.Leave;
import com.my.nitt_mess_manager.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class StudentOnLeaveFragment extends Fragment {
    FirebaseUser User;

    RecyclerView recyclerView;
    StudentsLeaveRequestAdapter recyclerAdapter;
    List<Leave> leaveList;
    TextView TotalStudents;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_student_on_leave, container, false);


        User = FirebaseAuth.getInstance().getCurrentUser();

        TotalStudents = root.findViewById(R.id.TotalStudents);
        recyclerView = root.findViewById(R.id.recyclerViewLeaveRequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TotalStudents.setText("0");

        String UserId = User.getEmail().split("@")[0];
        DatabaseReference Database = FirebaseDatabase.getInstance().getReference("Data/Warden").child(UserId);
        Database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot data) {
                if (data.exists()) {
                    getData(data.child("MessId").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void getData(String MessId) {

        GregorianCalendar today = new GregorianCalendar();
        int Year = today.get(Calendar.YEAR);
        int Month = today.get(Calendar.MONTH);
        int Day = today.get(Calendar.DAY_OF_MONTH);
        String Key = Year + "/" + Month + "/" + Day;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/StudentOnLeave").child(Key).child(MessId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    leaveList = new ArrayList<>();
                    for (final DataSnapshot leaveRequest : dataSnapshot.getChildren()) {
                        getLeaveRequest(leaveRequest.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void getLeaveRequest(String RequestId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/LeaveRequest").child(RequestId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot leaveRequest) {
                if (leaveRequest.exists()) {
                    Leave leave = new Leave(
                            leaveRequest.getKey(),
                            leaveRequest.child("from").getValue().toString(),
                            leaveRequest.child("to").getValue().toString(),
                            leaveRequest.child("reason").getValue().toString(),
                            leaveRequest.child("rollNo").getValue().toString(),
                            leaveRequest.child("messId").getValue().toString(),
                            leaveRequest.child("status").getValue().toString()
                    );
                    leaveList.add(leave);
                    recyclerAdapter = new StudentsLeaveRequestAdapter(leaveList, getContext());
                    recyclerView.setAdapter(recyclerAdapter);
                    TotalStudents.setText(recyclerAdapter.getItemCount() + "");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }


}