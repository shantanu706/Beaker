package com.example.beaker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


public class RegistrationForm extends AppCompatActivity {

    private EditText first_name, last_name, mob, email, dob;
    private Spinner gender;
    private String first_name1, last_name1, mob1, email1, pass1, dob1, gender1;
    private FirebaseUser auth;
    private FirebaseFirestore db;
    private dbManage manage;
    Map<String, Object> user_state = new HashMap<>();
    private String TAG = "ACTIVITY REGISTER";

    private String que;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);
        /*Intent intent=new Intent(this,SignUp.class);
        startActivity(intent);*/
        auth=FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            first_name1 = acct.getGivenName();
            //String personGivenName = acct.getGivenName();
            last_name1 = acct.getFamilyName();
            email1 = acct.getEmail();
            //String personId = acct.getId();
            //Uri personPhoto = acct.getPhotoUrl();

        }
        /*else {
            email.setText(auth.getEmail());
            email.setEnabled(false);
        }*/
        manage = new dbManage();
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        mob = findViewById(R.id.mobile_number);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        first_name.setText(first_name1);
        first_name.setEnabled(false);
        last_name.setText(last_name1);
        last_name.setEnabled(false);
        email.setText(email1);
        email.setEnabled(false);
        manage=new dbManage();
    }

    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        EditText edittext = findViewById(R.id.dob);
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    public void EditTextClicked(View view) {
        new DatePickerDialog(RegistrationForm.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    /*public boolean passwordCheck(String password) {
        boolean check1 = false;
        char[] pass = password.toCharArray();
        check1 = password.matches(".*[0-9]{1,}.*") && password.matches(".*[@#$]{1,}.*") && password.length() >= 6 && password.length() <= 20;
        return check1;
    }*/

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void clickRegister(View view) {
        int count = 0;
        first_name1 = first_name.getText().toString();
        last_name1 = last_name.getText().toString();
        mob1 = mob.getText().toString();
        email1 = email.getText().toString();
//        pass1 = pass.getText().toString();
        dob1 = dob.getText().toString();
        gender1 = gender.getSelectedItem().toString();


        if (first_name1.isEmpty()) {
            first_name.setError("Cannot Be Empty");
        } else {
            count++;
        }

        if (last_name1.isEmpty()) {
            last_name.setError("Cannot Be Empty");
        } else {
            count++;
        }
        if (mob1.isEmpty()) {
            mob.setError("Cannot Be Empty");
        } else if (manage.checkMobile(mob1)) {
            mob.setError("Mobile Number is Used!!!");
        } else {
            count++;
        }

        if (email1.isEmpty()) {
            email.setError("Cannot Be Empty");
        } else if (!isValid(email1)) {
            email.setError("InValid Email-id!!");
        }
        else if (manage.checkEmail(email1)){
            email.setError("Email Already Used!!!");
        }
        else {
            count++;
        }

        /*if (pass1.isEmpty()) {
            pass.setError("Cannot Be Empty");

        } else if (!passwordCheck(pass1)) {
            pass.setError("Must contain @,#,1");

        } else {
            count++;
        }*/

        if (gender.getSelectedItemPosition() == 0) {
            gender.setBackgroundColor(Color.RED);
        } else {
            gender.setBackgroundColor(Color.WHITE);
            count++;
        }

        if (dob1.isEmpty()) {
            dob.setError("Empty!!");
            Toast.makeText(this, dob1, Toast.LENGTH_SHORT).show();
        } else {
            count++;
        }

        if (count == 6) {
            //Succesfull Registered New Page
            manage.setDetils(first_name1, last_name1, mob1, email1, pass1, dob1, gender1);
            updateUI();
        } else {
            Toast.makeText(this, "Fill Form Complete", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(RegistrationForm.this,CheckProgress.class);
        startActivity(intent);
        finish();
    }


}

