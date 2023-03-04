package com.team34.cse_110_project_team_34;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import model.Database;
import model.UserDao;

public class MainActivity extends AppCompatActivity {

    private UserDao dao;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = Database.getInstance(this).getUserDao();
        // Remove this during demo
        Database.getInstance(this).clearAllTables();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        if (!dao.existsMain()) {
            Intent intent = new Intent(this, NewUserActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, NewFriendActivity.class);
            startActivity(intent);
        }

    }
}