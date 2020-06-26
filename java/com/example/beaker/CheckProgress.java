package com.example.beaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;

public class CheckProgress extends AppCompatActivity {


    private ImageView check_r,check_w;
    private ProgressBar progress_r,progress_w;
    private boolean status_R=false;
    private boolean status_W=false;
    private FirebaseAuth auth;
    private CheckForm form=new CheckForm(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_progress);
        auth=FirebaseAuth.getInstance();
        check_r=findViewById(R.id.r_c);
        check_w=findViewById(R.id.wall_c);
        progress_r=findViewById(R.id.progressBar_r);
        progress_w=findViewById(R.id.progressBar_w);
        check_r.setVisibility(View.GONE);
        check_w.setVisibility(View.GONE);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        form.execute(acct.getEmail().toString());
    }

    private static class CheckForm extends AsyncTask<String,String,String>{

        FirebaseFirestore fDb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        private WeakReference<CheckProgress> weakReference;
        private boolean temp=false;
        public CheckForm(CheckProgress activity) {
            weakReference=new WeakReference<CheckProgress>(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            while (temp!=true) {
                temp = fDb.collection("People")
                        .document(strings[0])
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        publishProgress("RY");
                                        temp=true;
                                    }
                                    else {
                                        publishProgress("RN");
                                        temp=true;
                                    }
                                }
                            }
                        })
                        .isSuccessful();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            temp=false;
            while (temp!=true) {
                fDb.collection("Wallet")
                        .document(strings[0])
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        publishProgress("WY");
                                        temp=true;
                                    }
                                    else {
                                        publishProgress("WN");
                                        temp=true;
                                    }
                                }
                            }
                        });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return "Please Click on Red Cross to Fill Form Complete";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            CheckProgress activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (values[0].equals("RY")){
                activity.progress_r.setVisibility(View.GONE);
                activity.check_r.setVisibility(View.VISIBLE);
                activity.check_r.setImageResource(R.drawable.ic_check_black_24dp);
                activity.check_r.setEnabled(false);
                activity.status_R=true;
            }
             if (values[0].equals("RN")){
                activity.progress_r.setVisibility(View.GONE);
                activity.check_r.setVisibility(View.VISIBLE);
                activity.check_r.setImageResource(R.drawable.ic_close_black_24dp);
                activity.status_R=false;
            }
             if (values[0].equals("WY")){
                activity.progress_w.setVisibility(View.GONE);
                activity.check_w.setVisibility(View.VISIBLE);
                activity.check_w.setImageResource(R.drawable.ic_check_black_24dp);
                activity.check_w.setEnabled(false);
                activity.status_W=true;
            }
             if (values[0].equals("WN")){
                activity.progress_w.setVisibility(View.GONE);
                activity.check_w.setVisibility(View.VISIBLE);
                activity.check_w.setImageResource(R.drawable.ic_close_black_24dp);
                 activity.status_W=false;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CheckProgress activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            if (activity.status_R && activity.status_W){
                Toast.makeText(activity, "Welcome", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(activity,MainActivity.class);
                intent.putExtra("pg",true);
                activity.startActivity(intent);
                activity.finish();
            }
            else{
                Toast.makeText(activity, "Please Wait", Toast.LENGTH_SHORT).show();
            }
            if (!activity.status_R){
                Intent intent=new Intent(activity,RegistrationForm.class);
                activity.startActivity(intent);
                activity.finish();
            }
            else if(!activity.status_W){
                Intent intent=new Intent(activity,WalletCreation.class);
                activity.startActivity(intent);
                activity.finish();
            }

        }
    }






}
