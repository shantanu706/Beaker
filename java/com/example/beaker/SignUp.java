package com.example.beaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SIGNUPACTIVITY";
    private EditText email,pass,cpass;
    private Button button;
    private String sign;
    FirebaseAuth auth;
    dbManage manage;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firestore.setFirestoreSettings(settings);
        manage = new dbManage();
        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();
        button = findViewById(R.id.register1);
        email = findViewById(R.id.email_id);
        pass = findViewById(R.id.passw);
        cpass = findViewById(R.id.c_pass);
        Intent intent=getIntent();
        sign = intent.getStringExtra("sign");
        if (sign.equals("signin")){
            cpass.setVisibility(View.GONE);
            button.setText("Sign In");
        }
    }


    public void SignUp_Register(View view) {

        if (sign.equals("signup")){
            int count=0;
            if (email.getText().toString().isEmpty()){
                email.setError("Empty");
            }
            else if (!checkerClass.isValid(email.getText().toString())){
                email.setError("Not A Valid Email-ID");
            }
            else {
                count++;
            }
            if (pass.getText().toString().isEmpty()){
                pass.setError("Empty");
            }
            else if (!checkerClass.passwordCheck(pass.getText().toString())){
                pass.setError("Must contain Number and Special Chracter");
            }
            else{
                count++;

            }
            if (cpass.getText().toString().equals(pass.getText().toString())){
                count++;
            }
            else{
                cpass.setError("Password did Not Match");
            }


            if (count==3){
                auth.createUserWithEmailAndPassword(email.getText().toString(),cpass.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    updateUI();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUp.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI();
                                }

                                // ...
                            }
                        });
            }
        }
        else{
            int count=0;
            if (email.getText().toString().isEmpty()){
                email.setError("Empty");
            }
            else if (!checkerClass.isValid(email.getText().toString())){
                email.setError("Not A Valid Email-ID");
            }
            else {
                count++;
            }
            if (pass.getText().toString().isEmpty()){
                pass.setError("Empty");
            }
            else if (!checkerClass.passwordCheck(pass.getText().toString())){
                pass.setError("Must contain Number and Special Chracter");
            }
            else{
                count++;
            }
            if (count==2){
                auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    Intent intent=new Intent(SignUp.this,CheckProgress.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignUp.this, "Authentication failed. Register with Email \n or Check Email Password ",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI();
                                }

                                // ...
                            }
                        });
            }
        }

    }

    private void updateUI() {
        Intent intent=new Intent(this,launcher.class);
        startActivity(intent);
        finish();
    }
}
