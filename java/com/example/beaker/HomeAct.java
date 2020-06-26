package com.example.beaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeAct extends AppCompatActivity {

    private ListView listView;
    private Button eve_participated;
    myAdapter adapter=new myAdapter();

    EventListCard card;
    FirebaseFirestore db;
//    boolean change=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        card=new EventListCard(this);
        Toast.makeText(this, "Please Wait!!", Toast.LENGTH_SHORT).show();
        db=FirebaseFirestore.getInstance();
        eve_participated=findViewById(R.id.participated);
        listView=findViewById(R.id.list_view);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o=adapterView.getItemAtPosition(i);
                Intent intent=new Intent(HomeAct.this,EventFullDesc.class);
                intent.putExtra("homeact",o.toString());
                startActivity(intent);
            }
        });
        eve_participated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeAct.this,participated.class);
                startActivity(intent);
            }
        });
        card.execute();


    }

    private class myAdapter extends BaseAdapter{

        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> titleString=new ArrayList<>();
        ArrayList<String> ends=new ArrayList<>();
        ArrayList<String> starts=new ArrayList<>();
        String c="";

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public Object getItem(int i) {
            return strings.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list, viewGroup, false);
            ((TextView) view.findViewById(R.id.evename)).setText(strings.get(i));
            ((TextView) view.findViewById(R.id.desc)).setText(titleString.get(i));
            c="Registration Ends On\n"+ends.get(i);
            ((TextView) view.findViewById(R.id.ends_in_)).setText(c);
            c="Events Starts On\n"+starts.get(i);
            ((TextView) view.findViewById(R.id.starts_on)).setText(c);
            return view;
        }
    }

    private static class EventListCard extends AsyncTask<String, ArrayList<String>,String> {


        private WeakReference<HomeAct> weakReference;
        private int count = 0;
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseFirestore fDb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        public EventListCard(HomeAct activity) {
            weakReference = new WeakReference<HomeAct>(activity);
        }

        private static final String TAG = "EventListCard";


        ArrayList<String> add_list_head = new ArrayList<>();
        ArrayList<String> add_list_title = new ArrayList<>();
        ArrayList<String> add_list_ends = new ArrayList<>();
        ArrayList<String> add_list_starts = new ArrayList<>();
        ArrayList<String> eve_participat_ = new ArrayList<>();
        Map<String,Object> doc=new HashMap<>();
        boolean temp = false;
        boolean task = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HomeAct activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            while (temp != true) {
                task = fDb.collection("Events")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                        doc=document.getData();
                                        try {
                                            add_list_head.add(document.getData().get("Head").toString());
                                            add_list_title.add(document.getData().get("Title").toString());
                                            add_list_ends.add(document.getData().get("Ends").toString());
                                            add_list_starts.add(document.getData().get("Starts").toString());
                                        }
                                        catch (Exception e){
                                            Log.d(TAG, "onComplete: "+e);
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                                publishProgress(add_list_head,add_list_title,add_list_ends,add_list_starts);
                            }
                        })
                        .isSuccessful();


                if (!task) {
                    temp = true;
                }
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... values) {
            super.onProgressUpdate(values);
            HomeAct activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (values[0]!=null) {
                activity.adapter.strings = values[0];
            }
            if (values[1]!=null){
                activity.adapter.titleString=values[1];
            }
            if (values[2]!=null){
                activity.adapter.ends=values[2];
            }
            if (values[3]!=null){
                activity.adapter.starts=values[3];
            }
            activity.listView.setAdapter(activity.adapter);
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            HomeAct activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            Toast.makeText(activity, "Here All the Events", Toast.LENGTH_SHORT).show();
        }
    }

}