package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import database.Database;
import model.User;
import database.UserDao;
import database.UserRepository;

public class NewUserActivity extends AppCompatActivity {

    private EditText name;
    private UserRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        name = findViewById(R.id.name);
        UserDao dao = Database.getInstance(this).getUserDao();
        repo = new UserRepository(dao);
    }

    public void onSubmit(View view) {
        User new_user = new User(name.getText().toString(), 0, 0, true);
        repo.upsertSynced(new_user);

        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }
}