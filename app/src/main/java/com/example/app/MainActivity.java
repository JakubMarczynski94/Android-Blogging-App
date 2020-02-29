package com.example.app;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // for adding post
    private FloatingActionButton addPostBtn;
    // end of for adding post

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navView = (BottomNavigationView)findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.featured, R.id.dashboard, R.id.apply)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.featured:
                        // Intent a = new Intent(MainActivity.this, MainActivity.class);
                        // startActivity(a);
                        break;
                    case R.id.dashboard:
                        Intent b = new Intent(MainActivity.this, Dashboard.class);
                        startActivity(b);
                        break;
                    case R.id.apply:
                        Intent c = new Intent(MainActivity.this, Apply.class);
                        startActivity(c);
                        break;
                }
                return false;
            }
        });


        // ---------------- Post part ----------------

        // careful to click on this button only when you are signed in

        addPostBtn = findViewById(R.id.add_post_btn);

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // the button actually works, the problem is not the button itself but what happens in the class
                Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });

    }
}
    //
        /*
        Button buttonCompanies;
        buttonCompanies = (Button) findViewById(R.id.button_companies);

        // Capture button clicks
        buttonCompanies.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Start Companies.class
                Intent myIntent = new Intent(MainActivity.this,
                        Companies.class);
                startActivity(myIntent);
            }
        });

        // companies button above
         */


