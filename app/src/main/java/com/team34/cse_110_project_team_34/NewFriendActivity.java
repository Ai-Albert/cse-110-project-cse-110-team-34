package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import model.Database;
import model.User;
import model.UserDao;
import model.UserRepository;

public class NewFriendActivity extends AppCompatActivity {

    private EditText public_code;
    private TextView last_added;
    private UserRepository repo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        public_code = findViewById(R.id.public_code);
        UserDao dao = Database.getInstance(this).getUserDao();
        repo = new UserRepository(dao);
        last_added = findViewById(R.id.last_added);
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
        last_added.setText(getString(R.string.added));
    }
}