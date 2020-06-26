package com.example.beaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class Start extends AppCompatActivity {

    private ProgressBar bar;
    FirebaseAuth auth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        bar=findViewById(R.id.progressBar);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent12=new Intent(Start.this,MainActivity.class);
                startActivity(intent12);
                finish();
            }
        },5000);
        if (auth==null){
            Intent intent=new Intent(this,launcher.class);
            startActivity(intent);
            finish();
        }


    }

}
