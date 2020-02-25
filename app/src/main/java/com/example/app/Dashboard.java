package com.example.app;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

public class Dashboard extends AppCompatActivity {

    // the navigation toolbar which we already know works
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_view);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.featured:
                        Intent a = new Intent(Dashboard.this, MainActivity.class);
                        startActivity(a);
                        break;
                    case R.id.dashboard:
                        break;
                    case R.id.apply:
                        Intent b = new Intent(Dashboard.this, Apply.class);
                        startActivity(b);

                        break;
                }
                return false;
            }
        });
        // above navigation toolbar which we know exists
    }
}

