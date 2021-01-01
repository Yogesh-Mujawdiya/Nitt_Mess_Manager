package com.my.nitt_mess_manager.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_manager.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;


public class MyMessRatingFragment extends Fragment {

    TextView textView;
    RatingBar Food, Quantity, FoodOnTime, Service, Cleanliness, OverAll;
    private String Key;
    private String MessId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_mess_rating, container, false);
        GregorianCalendar calendar = new GregorianCalendar();
        int Year = calendar.get(Calendar.YEAR);
        int Month = calendar.get(Calendar.MONTH);
        Key = Year + "/" + Month;

        textView = root.findViewById(R.id.MessName);
        Food = root.findViewById(R.id.Food);
        Quantity = root.findViewById(R.id.Quantity);
        FoodOnTime = root.findViewById(R.id.FoodOnTime);
        Service = root.findViewById(R.id.Service);
        Cleanliness = root.findViewById(R.id.Cleanliness);
        OverAll = root.findViewById(R.id.OverAll);
        getMessData();
        return root;
    }

    private void getMessData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        String UserId = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Manager").child(UserId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot manager) {
                if(manager.exists()) {
                    MessId = manager.child("MessId").getValue().toString();
                    getMessRating(MessId);
                    getMessData(MessId);
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }


    private void getMessRating(String MessId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data/MessRating").child(MessId).child(Key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot currentData) {
                if(currentData.exists()) {
                    Float FoodData = currentData.child("Food").getValue(Float.class);
                    Float QuantityData = currentData.child("Quantity").getValue(Float.class);
                    Float FoodOnTimeData = currentData.child("FoodOnTime").getValue(Float.class);
                    Float ServiceData = currentData.child("Service").getValue(Float.class);
                    Float CleanlinessData = currentData.child("Cleanliness").getValue(Float.class);
                    Float OverAllData = (FoodData + QuantityData + FoodOnTimeData + ServiceData + CleanlinessData) / 5;

                    Food.setRating(FoodData);
                    Quantity.setRating(QuantityData);
                    FoodOnTime.setRating(FoodOnTimeData);
                    Service.setRating(ServiceData);
                    Cleanliness.setRating(CleanlinessData);
                    OverAll.setRating(OverAllData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getMessData(final String messId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Mess").child(messId).child("Name");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mess) {
                if(mess.exists()) {
                    textView.setText(mess.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}