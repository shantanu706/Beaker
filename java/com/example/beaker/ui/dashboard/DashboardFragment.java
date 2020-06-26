package com.example.beaker.ui.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import com.example.beaker.MainActivity;
import com.example.beaker.PayAction;
import com.example.beaker.R;
import com.example.beaker.Transcation_Info;
import com.example.beaker.dbManage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private int position;
    private String TAG="ERROR CATHCHER";

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DatabaseReference reference;
    private Button payBut,transaction;
    private ImageButton refresh_button;
    private TextView wall_id;
    private TextView cash;
    private String temp;
    private boolean flag;
    Casher casher;//=new Casher(this);//=new Casher(this);

 /*   @Override
    public void onLowMemory() {
        super.onLowMemory();
        casher=null;


    }*/

   /* @Override
    protected void finalize() throws Throwable {
        super.finalize();
        casher=null;
    }*/

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        flag=false;
        reference=FirebaseDatabase.getInstance().getReference();
        wall_id=root.findViewById(R.id.wall_id);
        refresh_button=root.findViewById(R.id.refresh);
        payBut = root.findViewById(R.id.but_pay);
        transaction=root.findViewById(R.id.transc);
        cash=root.findViewById(R.id.cash);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        auth.useAppLanguage();
        transaction.setEnabled(false);
        payBut.setEnabled(false);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag){
                    casher=new Casher(DashboardFragment.this);
                    casher.execute(auth.getCurrentUser().getEmail());
                    flag=true;
                }
                else if (casher.getStatus()!= AsyncTask.Status.RUNNING){
                    casher=new Casher(DashboardFragment.this);
                    casher.execute(auth.getCurrentUser().getEmail());
                }
                Toast.makeText(getActivity(), "Refreshing!!!...Please Wait", Toast.LENGTH_SHORT).show();
            }
        });




        payBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),PayAction.class);
                intent.putExtra("EVD", "");
                startActivity(intent);
            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), Transcation_Info.class);
                intent.putExtra("id",wall_id.getText().toString());
                startActivity(intent);
            }
        });
        return root;


    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
        if (flag)
        casher.cancel(false);
       // casher=null;
    }



    private static class Casher extends AsyncTask<String,String,String>{

        private WeakReference<DashboardFragment> weakReference;


        public Casher(DashboardFragment activity) {
            weakReference = new WeakReference<DashboardFragment>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DashboardFragment activity = weakReference.get();
            if (activity == null || activity.isRemoving()) {
                return;
            }
            activity.refresh_button.setEnabled(false);
            activity.payBut.setEnabled(false);
            activity.transaction.setEnabled(false);
        }

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Map<String,Object> temp1=new HashMap<>();

        @Override
        protected String doInBackground(final String... strings) {
            final String[] b = new String[1];
            final String[] s = new String[1];
            final boolean[] temp = {false};
            while (temp[0]!=true){
                db.collection("Wallet")
                        .document(strings[0])
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot snapshot=task.getResult();

                                try{
                                    temp1=snapshot.getData();
                                    s[0]= String.valueOf(temp1.get("id"));
                                    temp[0]=true;
                                }
                                catch (NullPointerException e){
                                    s[0]=null;
                                }
                            }
                        });
            }
            publishProgress(s[0]);
            temp[0]=false;
            ValueEventListener listener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {

                       b[0]=dataSnapshot.child("Wallet").child(s[0]).child("Cash").getValue().toString();
                   }
                   catch (NullPointerException e){
                       Log.d("DashboardFragment", "onDataChange: "+String.valueOf(e));
                       b[0]=null;
                   }
                    if (b[0]!=null){
                        temp[0] =true;
                    }
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
            }
            return b[0];
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            DashboardFragment activity = weakReference.get();
            if (activity == null || activity.isRemoving()) {
                return;
            }
            activity.wall_id.setText(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DashboardFragment activity = weakReference.get();
            if (activity == null || activity.isRemoving()) {
                return;
            }
            activity.refresh_button.setEnabled(true);
            activity.cash.setText(s);
            activity.payBut.setEnabled(true);
            activity.transaction.setEnabled(true);
        }
    }



}