package com.example.beaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beaker.ui.dashboard.DashboardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class PayAction extends AppCompatActivity {

    private static final String TAG = "PayAction";
    private String activity;
    private String[] spilt;
    private String PhoneNum;
    private String Amt;
    private EditText recieverID;
    private EditText amt;
    private EditText full_name,mob;
    private EditText pin;
    private EditText email_id;
    private Button pay;
    private dbManage manage;
    private String pinCheck;
    private String id;
    private String status;
    private PinNPay pinNPay;
    private FirebaseAuth auth;
    private String event_name;
    private boolean form_checker=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_action);
        if (!isNetworkAvailable()){
            Toast.makeText(this, "Please Connect to Internet!!!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        id="";
        pinNPay=new PinNPay(this);
        full_name=findViewById(R.id.full_name);
        mob=findViewById(R.id.mobile_number);
        recieverID=findViewById(R.id.number_id);
        amt=findViewById(R.id.amount);
        pay=findViewById(R.id.pay);
        pin=findViewById(R.id.pin);
        email_id=findViewById(R.id.email);
        manage=new dbManage();
        auth=FirebaseAuth.getInstance();

        Intent intent=getIntent();
        activity=intent.getStringExtra("EVD");


        full_name.setEnabled(false);
        full_name.setVisibility(View.GONE);
        mob.setEnabled(false);
        mob.setVisibility(View.GONE);
        email_id.setEnabled(false);
        email_id.setVisibility(View.GONE);
        if (activity!=""){
            if (activity.contains(",")){
                spilt=activity.split(",");
                PhoneNum=spilt[0];
                Amt=spilt[1];
                event_name=spilt[2];
                recieverID.setText(PhoneNum);
                recieverID.setEnabled(false);
                amt.setText(Amt);
                amt.setEnabled(false);
                full_name.setVisibility(View.VISIBLE);
                full_name.setText(auth.getCurrentUser().getDisplayName());
                mob.setVisibility(View.VISIBLE);
                mob.setEnabled(true);
                email_id.setVisibility(View.VISIBLE);
                email_id.setText(auth.getCurrentUser().getEmail());
                email_id.setEnabled(false);
                email_id.setVisibility(View.VISIBLE);
                form_checker=true;
            }
        }


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneNum=recieverID.getText().toString();
                Amt=amt.getText().toString();
                pinCheck=pin.getText().toString();
                if (form_checker)
                pinNPay.execute(pinCheck,id,PhoneNum,Amt,"YES",full_name.getText().toString(),mob.getText().toString(),event_name,email_id.getText().toString());
                else pinNPay.execute(pinCheck,id,PhoneNum,Amt,"NO");
            }
        });



    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private static class PinNPay extends AsyncTask<String,String,String>{

        private WeakReference<PayAction>  weakReference;

        public PinNPay(PayAction activity) {
            weakReference=new WeakReference<PayAction>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PayAction activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.pay.setEnabled(false);
            activity.recieverID.setEnabled(false);
            activity.amt.setEnabled(false);
            activity.pin.setEnabled(false);
        }
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        Map<String,Object> form_filler=new HashMap<>();
        Map<String,Object> transaction=new ArrayMap<>();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PayAction activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            Toast.makeText(activity, "Please Wait..."+String.valueOf(values[0]), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            final String[] b = new String[1];
            final String[] cash=new String[1];
            final String[] s = new String[1];
            final String[] sender = new String[1];
            final String[] ls = new String[1];
            final boolean[] temp = {false};
            final boolean[] eXists = new boolean[1];
            final int[] i=new int[1];
            final String[] msg=new String[1];
            //CHECKS ID
            while (temp[0]!=true){
                temp[0]=db.collection("Wallet")
                        .document(user.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot snapshot=task.getResult();
                                sender[0]=snapshot.getData().get("id").toString();
                            }
                        })
                        .isSuccessful();
                if (sender[0]!=null){
                    break;
                }
            }
            temp[0]=false;
            if (sender[0]==null){
                publishProgress("ID NOT FOUND");
            }
            else if (sender[0].equals(strings[2])){
                return "Nice !! This is Your ID !!! ";
            }

            //checks pin
            ValueEventListener listener_pin=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (s[0]!=null){
                        temp[0]=true;
                        Log.d("Async", "onDataChange: Correct PIN "+s[0]);
                    }
                    try {
                        s[0]=dataSnapshot.child("Wallet").child(sender[0]).child("PIN").getValue().toString();
                    }
                    catch (Exception e){
                        Log.d("Async", "onDataChange: "+e);
                        s[0]=null;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            while (temp[0]!=true){
                reference.addValueEventListener(listener_pin);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!strings[0].equals(s[0])){
                return "PIN INCorrect!!";
            }

            //check for enough cash
            temp[0]=false;
            ValueEventListener listener_myCash=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (cash[0]!=null){
                        temp[0] =true;
                        msg[0]="DOne!!!";
                    }
                    try {
                        cash[0]=dataSnapshot.child("Wallet").child(sender[0]).child("Cash").getValue().toString();
                    }
                    catch (NullPointerException e){
                        cash[0]=null;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            temp[0]=false;
            while (temp[0]!=true){
                reference.addValueEventListener(listener_myCash);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (Integer.parseInt(cash[0])>Integer.parseInt(strings[3])){
                publishProgress("Processing!!!");
            }
            else {
                return "Not Enough Money!!!";
            }

            //checks reciever existance
            ValueEventListener listener_adding=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (b[0]!=null){
                        temp[0] =true;
                        msg[0]="DOne!!!";
                    }
                    try {
                        b[0]=dataSnapshot.child("Wallet").child(strings[2]).child("Cash").getValue().toString();
                    }
                    catch (NullPointerException e){
                        b[0]=null;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            temp[0]=false;
            int i1=0;
            while (temp[0]!=true){
                reference.addValueEventListener(listener_adding);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i1++;
                if (i1==3){
                    return "Cannot Find Id";
                }

            }

            //removes cash from sender
            ls[0]=String.valueOf(Integer.parseInt(cash[0])-Integer.parseInt(strings[3]));
            temp[0]=false;
            i[0]=0;
            while (i[0]<3){
                temp[0]= reference.child("Wallet").child(sender[0]).child("Cash").setValue(ls[0])
                        .isSuccessful();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i[0]++;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //adds to reciever
            ls[0]=String.valueOf(Integer.parseInt(b[0])+Integer.parseInt(strings[3]));
            temp[0]=false;
            i[0]=0;
            while (i[0]<3){
                temp[0]= reference.child("Wallet").child(strings[2]).child("Cash").setValue(ls[0])
                        .isSuccessful();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (temp[0]){
                    publishProgress("Money Transferred");
                }
                i[0]++;
            }
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd  HH.mm.ss").format(new Date());

            transaction.put("id",strings[2]);
            transaction.put("Time",timeStamp);
            transaction.put("Cash","-"+strings[3]);
            temp[0]=false;
            while(temp[0]!=true){
                temp[0]=db.collection("Wallet")
                        .document(sender[0])
                        .collection("Transaction")
                        .document(timeStamp)
                        .set(transaction)
                        .isSuccessful();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                temp[0]=true;
                if (temp[0]){
                    publishProgress("Transaction Reported");
                }
            }
            transaction=null;
            /*final String[] recWriter = new String[1];
            temp[0]=false;
            while (temp[0]!=true){
                temp[0]=db.collection("Wallet")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                        
                                        try {
                                            if (documentSnapshot.getData().get("id").toString().equals(strings[2])){
                                                recWriter[0] =documentSnapshot.getId().toString();
                                            }
                                        }
                                        catch (Exception e){
                                            Log.d(TAG, "onComplete: Transction Report");
                                        }
                                    }
                                }
                            }
                        })
                        .isSuccessful();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            temp[0]=false;
            transaction=new HashMap<>();
            transaction.put("id",sender[0]);
            transaction.put("Time",timeStamp);
            transaction.put("Cash","+"+strings[3]);
            temp[0]=false;
            while(temp[0]!=true){
                temp[0]=db.collection("Wallet")
                        .document(strings[2])
                        .collection("Transaction")
                        .document(timeStamp)
                        .set(transaction)
                        .isSuccessful();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                temp[0]=true;
                if (temp[0]){
                    publishProgress("Transaction Reported");
                }
            }

            //event registration
            if (strings[4].equals("YES")){
                form_filler.put("Name",strings[5]);
                form_filler.put("Mobile",strings[6]);
                form_filler.put("EventName",strings[7]);
                form_filler.put("Email",strings[8]);
                Log.d(TAG, "doInBackground: "+form_filler);
                db.collection("Participant")
                        .document(strings[7]+strings[8])
                        .set(form_filler);
                return "Registered";
            }
            else {
                return "Paid";

            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PayAction activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (s.equals("PIN INCorrect!!")){
                activity.pin.setEnabled(true);
                //activity.pin.setError(s);
                activity.pay.setEnabled(true);
                activity.status="NOT";
            }
            Toast.makeText(activity,s, Toast.LENGTH_LONG).show();
            Intent intent_MainAct=new Intent(activity,MainActivity.class);
            activity.startActivity(intent_MainAct);

        }
    }
}
