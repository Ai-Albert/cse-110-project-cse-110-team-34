package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import database.Database;
import database.UserDao;
import database.UserRepository;
import model.User;

public class NewFriendActivity extends AppCompatActivity {

    private EditText public_code;
    private EditText api_link;
    private TextView last_added;
    private UserRepository repo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        public_code = findViewById(R.id.public_code);
        UserDao dao = Database.getInstance(this).getUserDao();
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        String link = preferences.getString("API_Link", "");
        if (link.equals("")) {
            repo = new UserRepository(dao);
        } else {
            repo = new UserRepository(dao, link);
        }
        last_added = findViewById(R.id.last_added);
        api_link = findViewById(R.id.api_link);
    }

    /**
     * Gets the friend's data and stores it into the database, given a valid friend code.
     **/
    public void onSubmit(View view) {
        String uid = public_code.getText().toString();
        repo.getSynced(uid);
        if (!repo.existsLocal(uid)) {
            public_code.setError("Invalid friend code.");
            return;
        }
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    /**
     * Goes back to compass view without adding a friend
     **/
    public void onBack(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    public void onSubmitNewLink(View view) {
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("API_Link", api_link.getText().toString());
        editor.apply();
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }
}