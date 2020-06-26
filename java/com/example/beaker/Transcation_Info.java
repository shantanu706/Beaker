package com.example.beaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beaker.ui.dashboard.DashboardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Transcation_Info extends AppCompatActivity {

    private Adapter adapter;
    private String[] list={"Hello","Hello","Hello","Hello","Hello","Hello","Hello","Hello","Hello","Hello"};
    private ListView acBal;
    private Transc transc;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcation__info);
        Intent intent=getIntent();
        id = intent.getStringExtra("id");
        acBal =findViewById(R.id.account_state);
        adapter = new Adapter();
        transc=new Transc(this);
        transc.execute(id);
    }


    class Adapter extends BaseAdapter {


        private String TAG="Adapter ";
        private ArrayList<String> id=new ArrayList<>();
        private ArrayList<String> amt=new ArrayList<>();
        private ArrayList<String> date_time=new ArrayList<>();

        @Override
        public int getCount() {
            return id.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view=getLayoutInflater().inflate(R.layout.ac,null);

            ((TextView)view.findViewById(R.id.item_id)).setText(id.get(i));
            ((TextView)view.findViewById(R.id.amt)).setText(amt.get(i));
            if (amt.get(i).contains("-")){
                ((TextView)view.findViewById(R.id.amt)).setTextColor(Color.RED);
            }
            else {
                ((TextView)view.findViewById(R.id.amt)).setTextColor(Color.GREEN);

            }
            ((TextView)view.findViewById(R.id.datetime)).setText(date_time.get(i));

            return view;
        }
    }

    private static class Transc extends AsyncTask<String,ArrayList<String>,String>{


        private WeakReference<Transcation_Info> weakReference;

        public Transc(Transcation_Info activity) {
            weakReference=new WeakReference<Transcation_Info>(activity);
        }

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        private ArrayList<String> id=new ArrayList<>();
        private ArrayList<String> amt=new ArrayList<>();
        private ArrayList<String> date_time=new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            boolean temp=false;

            while (!temp){
                db.collection("Wallet")
                        .document(strings[0])
                        .collection("Transaction")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot snapshot:task.getResult()){
                                           try {
                                               id.add(snapshot.getData().get("id").toString());
                                               amt.add(snapshot.getData().get("Cash").toString());
                                               date_time.add(snapshot.getData().get("Time").toString());
                                           }
                                           catch (Exception e){

                                           }
                                        try {
                                            Thread.sleep(5);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    publishProgress(id,amt,date_time);

                                }
                            }
                        });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                temp=true;
                if (temp){
                    temp=true;
                    return "Done";
                }

            }
            return "Failed";
        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... values) {
            super.onProgressUpdate(values);
            Transcation_Info activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.adapter.id=values[0];
            activity.adapter.amt=values[1];
            activity.adapter.date_time=values[2];
            activity.acBal.setAdapter(activity.adapter);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Transcation_Info activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();

        }
    }


}
