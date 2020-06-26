package com.example.beaker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth auth;
    private boolean Activty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
  /*      AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
  */      NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        user= FirebaseAuth.getInstance().getCurrentUser();
        auth=FirebaseAuth.getInstance();
        Intent intent1=getIntent();
        Activty=intent1.getBooleanExtra("pg",false);
        if (user==null){
            Intent intent=new Intent(MainActivity.this,launcher.class);
            startActivity(intent);
            finish();
        }
        if (!Activty){
            Intent intent=new Intent(MainActivity.this,CheckProgress.class);
            startActivity(intent);
            finish();
        }
    }

}
