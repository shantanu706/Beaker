package com.example.beaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class EventFullDesc extends AppCompatActivity {

    private String activity,eventname;
    private Button button;
    private TextView head,desc;
    getDescription description=new getDescription(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_full_desc);
        button=findViewById(R.id.button);
        head=findViewById(R.id.head);
        desc=findViewById(R.id.desc);
        Intent intent1=getIntent();
        activity=intent1.getStringExtra("homeact");
        Toast.makeText(this, activity, Toast.LENGTH_SHORT).show();
        description.execute(activity);
        button.setEnabled(false);
    }

    private static class getDescription extends AsyncTask<String,String,String> {

        private static final String TAG ="getDescription";
        FirebaseFirestore fDb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        Map<String,Object> doc=new HashMap<>();
        boolean temp = false;
        boolean task = false;
        private WeakReference<EventFullDesc> weakReference;
        public getDescription(EventFullDesc activity) {
            weakReference = new WeakReference<EventFullDesc>(activity);
        }



        @Override
        protected String doInBackground(String... strings) {
            task = fDb.collection("Events")
                    .document(strings[0])
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            doc=task.getResult().getData();
                            publishProgress(doc.get("Head").toString(),doc.get("Description").toString(),doc.get("Cost").toString(),doc.get("ID").toString());
                        }
                    })
                    .isSuccessful();


            if (!task) {
                temp = true;
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate(final String... values) {
            super.onProgressUpdate(values);
            final EventFullDesc activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            String s=values[1]+"\n\nCost: "+values[2];
            activity.head.setText(values[0]);
            activity.desc.setText(s);
            activity.button.setEnabled(true);
            activity.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(activity,PayAction.class);
                    intent.putExtra("EVD",values[3]+","+values[2]+","+values[0]);
                    activity.startActivity(intent);
                }
            });

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            EventFullDesc activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
        }
    }
}
