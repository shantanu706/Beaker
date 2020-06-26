package com.example.beaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class participated extends AppCompatActivity {

    private SwipeMenuListView eventList;
    public ListAdapter1 adapter = new ListAdapter1();
    private EventListCard card;
    private Map<String,Object> doc=new HashMap<>();
    private String getDocumentId;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseFirestore fDb = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
    private String TAG="MainActivity";
    boolean task=false;
    private String s;
    SwipeMenuCreator creator;

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,HomeAct.class);
        startActivity(intent);
        finish();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participated);
        card=new EventListCard(this);
        fDb.setFirestoreSettings(settings);
        eventList = findViewById(R.id.list_participated);
        creator=new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(150);
                // set a icon
                deleteItem.setTitle("Delete Form");
                deleteItem.setTitleSize(14);
                deleteItem.setTitleColor(Color.BLACK);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        try {
            eventList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    Object item=null;
                    if (index==0) {
                        item = eventList.getItemAtPosition(position);
                        fDb.collection("Participant")
                                .document(item.toString()+auth.getCurrentUser().getEmail())
                                .delete();
                    Intent intent=getIntent();
                    finish();
                    startActivity(intent);
                        return true;
                    }

                    return false;
                }
            });
        }
        catch (Exception e){

        }

        card.execute();

    }


    private class ListAdapter1 extends BaseAdapter {


        ArrayList<String> strings = new ArrayList<>();

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public Object getItem(int i) {
            Log.d(TAG, "getItem: "+i);
            return strings.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.event_participated_name, viewGroup, false);
            ((TextView) view.findViewById(R.id.eve_name)).setText(strings.get(i));
            return view;
        }
    }

    private static class EventListCard extends AsyncTask<String, ArrayList<String>,String> {


        private WeakReference<participated> weakReference;
        private int count = 0;
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseFirestore fDb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        public EventListCard(participated activity) {
            weakReference = new WeakReference<participated>(activity);
        }

        private static final String TAG = "EventListCard";


        ArrayList<String> add_list_head = new ArrayList<>();
        Map<String,Object> doc=new HashMap<>();
        boolean temp = false;
        boolean task = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final participated activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            fDb.setFirestoreSettings(settings);
            while (temp != true) {
                task = fDb.collection("Participant")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                        doc=document.getData();
                                        try {
                                            if (doc.get("Email").toString().equals(auth.getCurrentUser().getEmail()))
                                            add_list_head.add(doc.get("EventName").toString());
                                        }
                                        catch (Exception e){
                                            Log.d(TAG, "onComplete: "+e);
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                                /*add_list_title.size();
                                add_list_head.size();*/

                            }
                        })
                        .isSuccessful();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                temp = true;
                publishProgress(add_list_head);

            }

            return "Done";
        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... values) {
            super.onProgressUpdate(values);
            participated activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (values[0]!=null) {
                activity.adapter.strings = values[0];
                activity.eventList.setAdapter(activity.adapter);
                activity.eventList.setMenuCreator(activity.creator);
            }

        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            participated activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            // Log.d(TAG, "onPostExecute: "+strings.toString());
            // Toast.makeText(activity, "Done!!!!", Toast.LENGTH_SHORT).show();

        }
    }
}
