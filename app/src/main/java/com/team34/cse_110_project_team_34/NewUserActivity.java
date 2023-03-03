package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import model.Database;
import model.User;
import model.UserDao;
import model.UserRepository;

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
        User new_user = new User(name.getText().toString(), 0, 0);
        repo.upsertLocal(new_user); // not testing remote yet because not needed
    }
}