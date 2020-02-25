package com.example.app;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class Apply extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_view);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.featured:
                        Intent a = new Intent(Apply.this, MainActivity.class);
                        startActivity(a);
                        break;
                    case R.id.dashboard:
                        Intent b = new Intent(Apply.this, Dashboard.class);
                        startActivity(b);
                        break;
                    case R.id.apply:
                        break;
                }
                return false;
            }
        });
    }
}
