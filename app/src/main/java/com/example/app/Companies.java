package com.example.app;
import android.os.Bundle;
import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;

public class Companies extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.company_view);
    }
}




