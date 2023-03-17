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
    private EditText api_link;
    private UserRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        name = findViewById(R.id.name);
        api_link = findViewById(R.id.api_link);

    }

    /**
     * Creates a new user in the remote database and locally given a name
     **/
    public void onSubmit(View view) {
        if (name.getText().toString().equals("")) {
            name.setError("User must have a name.");
            return;
        }

        UserDao dao = Database.getInstance(this).getUserDao();
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        String link = preferences.getString("API_Link", "");
        Log.d("test", link);
        if (link.equals("")) {
            repo = new UserRepository(dao);
        } else {
            Log.d("test", link);
            repo = new UserRepository(dao, link);
        }

        String public_code = UUID.randomUUID().toString();
        String private_code = UUID.randomUUID().toString();
        User new_user = new User(name.getText().toString(), public_code, 0, 0);
        repo.upsertSynced(private_code, new_user);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Private", private_code);
        editor.putString("Public", public_code);
        editor.apply();

        System.out.println(public_code);

        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    public void onSubmitNewLink(View view) {
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("API_Link", api_link.getText().toString());
        editor.apply();
        Log.d("test2", api_link.getText().toString());
    }
}