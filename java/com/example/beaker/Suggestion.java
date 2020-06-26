package com.example.beaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

public class Suggestion extends AppCompatActivity {

    private EditText name,your_suggestion;
    private Switch aSwitch;
    private Button post;
    private String name1,your_suggestion1;



    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseFirestore fDb=FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
    Map<String,Object> doc=new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        name=findViewById(R.id.your_name);
        your_suggestion=findViewById(R.id.post_suggestion);
        post=findViewById(R.id.post_);
        aSwitch=findViewById(R.id.switch1);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name1=name.getText().toString();
                your_suggestion1=your_suggestion.getText().toString();
                if (!aSwitch.isChecked()){
                    name1="Anonymous";
                }
                doc.put("Name",name1);
                doc.put("SuggestionPosted",your_suggestion1);
                fDb.collection("Suggestion")
                        .document()
                        .set(doc);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(Suggestion.this, "Posted", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
