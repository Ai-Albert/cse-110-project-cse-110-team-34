package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import utilities.CoordinateDao;
import utilities.Database;
import utilities.User;
import utilities.UserDao;

public class NewUserActivity extends AppCompatActivity {

    private EditText name;
    private UserDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        name = findViewById(R.id.name);
        dao = Database.getInstance(this).getUserDao();
    }

    public void onSubmit(View view) {
        User new_user = new User(name.getText().toString(), 0, 0);
        dao.insert(new_user);
        Log.d("users", dao.getAll().toString());
    }
}