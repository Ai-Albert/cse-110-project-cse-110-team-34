package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class NewUserActivity extends AppCompatActivity {

    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        name = findViewById(R.id.name);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }
}