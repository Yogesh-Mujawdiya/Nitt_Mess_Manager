package com.my.nitt_mess_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditMessMenuActivity extends AppCompatActivity {
    String WeekDay, MessId;
    private FirebaseUser User;
    AppCompatButton buttonUpdate;
    EditText Breakfast, Lunch, Snack, Dinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mess_menu);
        WeekDay = getIntent().getStringExtra("WeekDay");
        User = FirebaseAuth.getInstance().getCurrentUser();
        buttonUpdate = findViewById(R.id.buttonUpdate);
        Breakfast = findViewById(R.id.Breakfast);
        Lunch = findViewById(R.id.Lunch);
        Snack = findViewById(R.id.Snack);
        Dinner = findViewById(R.id.Dinner);
        getMessData();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Data/Mess").child(MessId).child("Menu").child(WeekDay).child("Breakfast").setValue(Breakfast.getText().toString());
                FirebaseDatabase.getInstance().getReference("Data/Mess").child(MessId).child("Menu").child(WeekDay).child("Lunch").setValue(Breakfast.getText().toString());
                FirebaseDatabase.getInstance().getReference("Data/Mess").child(MessId).child("Menu").child(WeekDay).child("Snack").setValue(Breakfast.getText().toString());
                FirebaseDatabase.getInstance().getReference("Data/Mess").child(MessId).child("Menu").child(WeekDay).child("Dinner").setValue(Breakfast.getText().toString());
                finish();
            }
        });
    }

    private void getMessData() {
        final ProgressDialog progressDialog = new ProgressDialog(EditMessMenuActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        String UserId = User.getEmail().split("@")[0];
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Manager").child(UserId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot manager) {
                if(manager.exists()) {
                    MessId = manager.child("MessId").getValue().toString();
                    getMessMenu(MessId);
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void getMessMenu(final String messId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Mess").child(messId).child("Menu").child(WeekDay);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mess) {
                if(mess.exists()) {
                    Breakfast.setText(mess.child("Breakfast").getValue().toString());
                    Lunch.setText(mess.child("Lunch").getValue().toString());
                    Dinner.setText(mess.child("Dinner").getValue().toString());
                    Snack.setText(mess.child("Snack").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}