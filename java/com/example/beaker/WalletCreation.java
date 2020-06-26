package com.example.beaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;

public class WalletCreation extends AppCompatActivity {

    private EditText wallet_id,wallet_PIN;
    private Button create_wallet_id;
    private TextView check_avail;
    private String wallet_id_s,wallet_PIN_s,wallet_id_c;
    private Boolean checkAval=false;
    private int count=0;
    private dbManage manage;
    private boolean Activty;
    private FirebaseUser auth;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_creation);
        Intent intent=getIntent();
        Activty=intent.getBooleanExtra("Activity Complete",false);
        manage=new dbManage();
        wallet_id=findViewById(R.id.wallet_id);
        bar=findViewById(R.id.progressBar4);
        wallet_PIN=findViewById(R.id.w_pin);
        check_avail=findViewById(R.id.chk_avail);
        create_wallet_id=findViewById(R.id.cred_wallet);
        auth=FirebaseAuth.getInstance().getCurrentUser();
        bar.setVisibility(View.INVISIBLE);
        check_avail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bar.setVisibility(View.VISIBLE);
                if (wallet_id.getText().toString().isEmpty()){
                    wallet_id.setError("Empty!!");
                    bar.setVisibility(View.INVISIBLE);
                }
                else if (wallet_id.getText().toString().length()<10){
                    wallet_id.setError("Least 10 Chars");
                    bar.setVisibility(View.INVISIBLE);
                }
                else if (wallet_id.getText().toString().contains(" ")){
                    wallet_id.setError("No Spaces !!");
                    bar.setVisibility(View.INVISIBLE);
                }
                else {
                    checkAD task=new checkAD(WalletCreation.this);
                    task.execute(wallet_id.getText().toString());
                }
            }
        });
        /*wallet_id.setText(auth.getEmail());
        wallet_id.setEnabled(false);*/
     /*   check_avail.setText("Available!!");
        check_avail.setTextColor(Color.GREEN);*/

    }

    public void cred_wallet_click(View view){
        count=0;

        wallet_id_s=wallet_id.getText().toString();
        wallet_PIN_s=wallet_PIN.getText().toString();
        wallet_id_c=check_avail.getText().toString();
        if(wallet_id_c.equals("Its Available! Go Ahead")) {
            if (wallet_id_s.isEmpty()) {
                wallet_id.setError("Empty!!");
            } else if (wallet_id_s.length() < 10) {
                wallet_id.setError("Least 10 Character");
            } else {
                count++;
            }
            if (wallet_PIN_s.isEmpty()) {
                wallet_PIN.setError("Empty!!");
            } else if (wallet_PIN_s.length() > 6) {
                wallet_PIN.setError("MAX 6 digit");
            } else {
                count++;
            }
        }
        else {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show();
        }

        if(count!=2){
            Toast.makeText(this, "Invalid Input!!\nFill Form Complete", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                manage.CreateWalletProfile(wallet_id_s,wallet_PIN_s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateUI();
        }
    }

    private void updateUI() {
        Intent intent=new Intent(WalletCreation.this,CheckProgress.class);
        startActivity(intent);
        finish();
    }

    private static class checkAD extends AsyncTask<String,Boolean,Boolean>{

        private WeakReference<WalletCreation> weakReference;

        public checkAD(WalletCreation activity) {
            weakReference = new WeakReference<WalletCreation>(activity);
        }

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WalletCreation activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.check_avail.setText("Please Wait...");
            activity.bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(final String... strings) {
            final boolean[] b = new boolean[1];
            final boolean[] temp = {false};
            ValueEventListener listener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    b[0] =dataSnapshot.child("Wallet").child(strings[0]).exists();
                    temp[0] =true;
                    publishProgress(b[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            while (temp[0] !=true){
                reference.addValueEventListener(listener);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("ASY : ", "doInBackground: "+String.valueOf(b[0]));

            }
            return b[0];
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            WalletCreation activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.bar.setVisibility(View.INVISIBLE);
            if (aBoolean){
                activity.check_avail.setText("Opps! Try Different - Click Here to Check");
                activity.check_avail.setTextColor(Color.RED);
            }
            else{
                activity.check_avail.setText("Its Available! Go Ahead");
                activity.check_avail.setTextColor(Color.GREEN);
            }
        }
    }

}
