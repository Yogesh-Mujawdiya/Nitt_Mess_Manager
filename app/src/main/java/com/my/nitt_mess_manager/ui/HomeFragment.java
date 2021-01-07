package com.my.nitt_mess_manager.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.my.nitt_mess_manager.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class HomeFragment extends Fragment {

    Button btnScan;
    TextView textView;
    String MessId;
    FirebaseUser User;
    AlertDialog.Builder alertDialog;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    GregorianCalendar calendar;
    String FoodType;
    StorageReference storageRef = storage.getReferenceFromUrl("gs://nitt-mess.appspot.com/User/");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        calendar = new GregorianCalendar();

        alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Invalid Info")
                .setMessage("QR Code Information is Invalid")
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Continue with delete operation
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        User = FirebaseAuth.getInstance().getCurrentUser();
        getMessData();
        btnScan = root.findViewById(R.id.ScanButton);
        textView = root.findViewById(R.id.tvResult);

        btnScan.setOnClickListener(view -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            intentIntegrator.setCameraId(0);
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setPrompt("scanning");
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setBarcodeImageEnabled(true);
            IntentIntegrator.forSupportFragment(HomeFragment.this).initiateScan();
        });

        return root;
    }

    @SuppressLint("SetTextI18n")
    private void getQRData(final String[] s) {
        int Hours = calendar.get(Calendar.HOUR);
        if(Hours<11)
            FoodType = "Breakfast";
        else if(Hours<15)
            FoodType = "Lunch";
        else if(Hours<18)
            FoodType ="Snack";
        else
            FoodType = "Dinner";
        if(s[0].equals(MessId)){
            final int Year = calendar.get(Calendar.YEAR);
            final int Month = calendar.get(Calendar.MONTH);
            final int Day = calendar.get(Calendar.DAY_OF_MONTH);
            final String Key = Year+"/"+Month+"/"+Day;
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/User").child(s[2]).child("LastTakenFood");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists() && dataSnapshot.child("date").getValue(String.class).equals(Key) &&
                            dataSnapshot.child("type").getValue().equals(FoodType))
                        textView.setText("Complete his "+s[1]+" at Time "+ dataSnapshot.child("time").getValue().toString());
                    else {
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.user_info_dilog);

                        final String UserId = s[2];
                        final String MessId = s[0];
                        final String FoodType = s[1];
                        Calendar calendar = Calendar.getInstance();
                        final int Hours = calendar.get(Calendar.HOUR_OF_DAY);
                        final int Minutes = calendar.get(Calendar.MINUTE);
                        final int Year = calendar.get(Calendar.YEAR);
                        final int Month = calendar.get(Calendar.MONTH);
                        final int Day = calendar.get(Calendar.DAY_OF_MONTH);
                        final String Key = Year+"/"+Month+"/"+Day;

                        final TextView textViewName = dialog.findViewById(R.id.UserName);
                        final TextView textViewMobile = dialog.findViewById(R.id.UserMobile);
                        final TextView textViewRollNo = dialog.findViewById(R.id.UserRollNo);
                        final TextView textViewError = dialog.findViewById(R.id.textViewError);
                        final Button btnSubmit = dialog.findViewById(R.id.SubmitButton);
                        final ImageView userProfile = dialog.findViewById(R.id.UserProfile);
                        storageRef.child(UserId).getDownloadUrl().addOnSuccessListener(uri ->
                                Glide.with(getContext()).load(uri).into(userProfile)).addOnFailureListener(Throwable::printStackTrace);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("AllUser").child(UserId);
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    textViewName.setText(dataSnapshot.child("Student Name").getValue().toString());
                                    textViewMobile.setText(dataSnapshot.child("Contact No").getValue().toString());
                                    textViewRollNo.setText(dataSnapshot.child("Student Id").getValue().toString());
                                    textViewError.setVisibility(View.GONE);
                                }
                                else {
                                    btnSubmit.setEnabled(false);
                                    textViewError.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void onCancelled(@NotNull DatabaseError databaseError) {

                            }
                        });
                        btnSubmit.setOnClickListener(view -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data/User").child(UserId).child("LastTakenFood");
                            databaseReference.child("date").setValue(Year+"/"+Month+"/"+Day);
                            databaseReference.child("time").setValue(Hours+":"+Minutes);
                            databaseReference.child("type").setValue(FoodType);
                            databaseReference.child("feedback").setValue(false);
                            databaseReference.child("MessId").setValue(MessId);
                            dialog.dismiss();
                        });
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                }
                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {

                }
            });
        }
        else {
            textView.setText("Invalid Mess Id");
        }
    }

    @SuppressLint("SetTextI18n")
    private void getGuestQRData(final String[] s) {
        int Hours = calendar.get(Calendar.HOUR);
        if(Hours<11)
            FoodType = "Breakfast";
        else if(Hours<15)
            FoodType = "Lunch";
        else if(Hours<18)
            FoodType ="Snack";
        else
            FoodType = "Dinner";
        if(s[0].equals(MessId)){
            final int Year = calendar.get(Calendar.YEAR);
            final int Month = calendar.get(Calendar.MONTH);
            final int Day = calendar.get(Calendar.DAY_OF_MONTH);
            final String Key = Year+"/"+Month+"/"+Day;
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/GuestQR").child(s[0]).child(Key).child(s[1]);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()  && dataSnapshot.hasChild(FoodType))
                        textView.setText("Complete his "+s[1]+" at Time"+ dataSnapshot.child("time").getValue().toString());
                    else {
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.guest_info_dilog);
                        Calendar calendar = Calendar.getInstance();
                        final int Hours = calendar.get(Calendar.HOUR_OF_DAY);
                        final int Minutes = calendar.get(Calendar.MINUTE);
                        final int Year = calendar.get(Calendar.YEAR);
                        final int Month = calendar.get(Calendar.MONTH);
                        final int Day = calendar.get(Calendar.DAY_OF_MONTH);
                        final String Key = Year+"/"+Month+"/"+Day;

                        final TextView textViewName = dialog.findViewById(R.id.GuestName);
                        final TextView textViewId = dialog.findViewById(R.id.GuestId);
                        final TextView textViewGeneratedBy = dialog.findViewById(R.id.UserRollNo);
                        final Button btnSubmit = dialog.findViewById(R.id.SubmitButton);
                        textViewId.setText(dataSnapshot.getKey());
                        textViewName.setText(dataSnapshot.child("Name").getValue(String.class));
                        textViewGeneratedBy.setText(dataSnapshot.child("GenerateBy").getValue(String.class));
                        btnSubmit.setOnClickListener(view -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data/GuestQR").child(s[0]).child(Key).child(s[1]);
                            databaseReference.child(FoodType).setValue(Hours+":"+Minutes);
                            String key = Year+"/"+Month+"/"+Day;
                            FirebaseDatabase.getInstance().getReference("Data/Guest").child(MessId).child(key).child(dataSnapshot.child("GenerateBy").getValue(String.class)).child(FoodType)
                                    .runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                            if(currentData.getValue(Integer.class)!=null)
                                                currentData.setValue(currentData.getValue(Integer.class)+1);
                                            else
                                                currentData.setValue(1);
                                            return Transaction.success(currentData);
                                        }

                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                                        }
                                    });
                            dialog.dismiss();
                        });
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                }
                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {

                }
            });
        }
        else {
            textView.setText("Invalid Mess Id");
        }
    }

    private void getMessData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        String UserId = User.getEmail().split("@")[0];
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Manager").child(UserId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot manager) {
                if(manager.exists()) {
                    MessId = manager.child("MessId").getValue().toString();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null && result.getContents()!=null){

            final String Data = result.getContents();
            StringBuilder DecryptData = new StringBuilder();
            for(int i=0;i<Data.length();i++){
                int c = Data.charAt(i);
                DecryptData.append((char) (c ^ 4));
            }
            final String[] S = DecryptData.toString().split("\n");
            if(S.length==4){
                getQRData(S);
            }
            else if(S.length==2){
                getGuestQRData(S);
            }
            else {
                textView.setText("Invalid QR Code");
            }
        }

    }
}