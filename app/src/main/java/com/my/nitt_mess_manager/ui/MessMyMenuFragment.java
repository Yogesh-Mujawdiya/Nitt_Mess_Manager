package com.my.nitt_mess_manager.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_manager.Adapter.MessMyMenuAdapter;
import com.my.nitt_mess_manager.Class.MessMenu;
import com.my.nitt_mess_manager.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class MessMyMenuFragment extends Fragment {



    RecyclerView recyclerView;
    MessMyMenuAdapter recyclerAdapter;
    List<MessMenu> menuList;
    Dictionary Week;
    private String MessId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mess_my_menu, container, false);

        Week = new Hashtable();
        Week.put("Monday",1);
        Week.put("Tuesday",2);
        Week.put("Wednesday",3);
        Week.put("Thursday",4);
        Week.put("Friday",5);
        Week.put("Saturday",6);
        Week.put("Sunday",7);

        recyclerView = root.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getMessData();

        return root;
    }
    private void getMessData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String UserId = User.getEmail().split("@")[0];
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Manager").child(UserId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot manager) {
                if(manager.exists()) {
                    MessId = manager.child("MessId").getValue().toString();
                    getMessMenu(MessId);
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void getMessMenu(String MessId){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Mess/"+MessId+"/Menu");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    menuList = new ArrayList<>();
                    for (final DataSnapshot menu : dataSnapshot.getChildren()) {
                        menuList.add( new MessMenu(
                                menu.getKey(),
                                menu.child("Breakfast").getValue().toString(),
                                menu.child("Lunch").getValue().toString(),
                                menu.child("Dinner").getValue().toString(),
                                menu.child("Snack").getValue().toString()
                        ));
                    }
                }
                sortByWeekDay();
                recyclerAdapter = new MessMyMenuAdapter(menuList, getContext());
                recyclerView.setAdapter(recyclerAdapter);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void sortByWeekDay() {
        for(int i=0;i<menuList.size();i++){
            for(int j=i+1;j<menuList.size();j++){
                int x = (int) Week.get(menuList.get(j).getWeekName());
                int y = (int) Week.get(menuList.get(i).getWeekName());
                if(x<y){
                    Collections.swap(menuList, i, j);
                }
            }
        }
    }

}