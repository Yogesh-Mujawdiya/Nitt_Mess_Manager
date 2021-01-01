package com.my.nitt_mess_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    TextInputLayout Email;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Email = findViewById(R.id.editTextEmail);
        button = findViewById(R.id.buttonResat);

        button.setOnClickListener(view -> sendResetPasswordLink());


    }

    private void sendResetPasswordLink(){
        String email = Email.getEditText().getText().toString();
        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Email Sent for Reset Password",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}