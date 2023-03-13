package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import database.Database;
import java.util.UUID;
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

    /**
     * Creates a new user in the remote database and locally given a name
     **/
    public void onSubmit(View view) {
        if (name.getText().toString().equals("")) {
            name.setError("User must have a name.");
            return;
        }
        String public_code = UUID.randomUUID().toString();
        String private_code = UUID.randomUUID().toString();
        User new_user = new User(name.getText().toString(), public_code, 0, 0);
        //SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Private", private_code);
        editor.putString("Public", public_code);
        editor.apply();
        repo.upsertSynced(private_code, new_user);
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }
}