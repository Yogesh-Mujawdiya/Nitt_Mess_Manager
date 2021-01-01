package com.my.nitt_mess_manager.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_manager.R;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;

public class MessReportFragment extends Fragment {

    EditText Month,Year;
    private File filePath;
    FirebaseUser User;
    AppCompatButton button;
    String MessId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mess_report, container, false);
        filePath = new File(Environment.getExternalStorageDirectory() + "/Report.xls");
        Month = root.findViewById(R.id.picker_month);
        Year = root.findViewById(R.id.picker_year);
        User = FirebaseAuth.getInstance().getCurrentUser();
        button = root.findViewById(R.id.GenerateReport);
        getMessData();
        button.setOnClickListener(view -> {
            button.setVisibility(View.GONE);
            if (!hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Permission ask
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            } else {
                buttonCreateExcel();
            }

        });
        return root;
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

    public void buttonCreateExcel(){
        String month = Integer.toString(Integer.parseInt(Month.getText().toString())-1);
        String year = Year.getText().toString();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/AllocatedMessHistory").child(MessId).child(year).child(month);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot data) {
                if(data.exists()) {
                    HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                    HSSFSheet hssfSheet = hssfWorkbook.createSheet("Mess Report");
                    HSSFRow hssfRow = hssfSheet.createRow(0);
                    hssfRow.createCell(0).setCellValue("Roll No");
                    hssfRow.createCell(1).setCellValue("Name");
                    int i=1;
                    for(DataSnapshot R : data.getChildren()){
                        HSSFRow row = hssfSheet.createRow(i);
                        row.createCell(0).setCellValue(R.getKey());
                        row.createCell(1).setCellValue(R.getValue().toString());
                        i+=1;
                    }
                    Environment.getExternalStorageState();
                    try {
                        if (!filePath.exists()){
                            filePath.createNewFile();
                        }
                        FileOutputStream fileOutputStream= new FileOutputStream(filePath);
                        hssfWorkbook.write(fileOutputStream);

                        if (fileOutputStream!=null){
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }


                        // Get URI and MIME type of file
//                        Uri uri = FileProvider.getUriForFile(getContext(), "com.my.nitt_mess" + ".provider", filePath);
                        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", filePath);
                        String mime = getActivity().getContentResolver().getType(uri);

                        // Open file with user selected app
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, mime);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                button.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

                button.setVisibility(View.VISIBLE);
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 111) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buttonCreateExcel();
            }
        }
    }

}