package com.rachman_warehouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    String id, username, name;
    Boolean session = false;
    public static final String session_status = "session_status";
    ConnectivityManager conMgr;

    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        //cek session login
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        name = getIntent().getStringExtra(TAG_NAME);

        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        name = sharedpreferences.getString(TAG_NAME, null);

        Thread thread = new Thread(){
            public void run(){
                try{
                    sleep(2000);

                }catch (InterruptedException e){
                    e.printStackTrace();

                }finally {

                    if (session) {
                        Intent intent = new Intent(MainActivity.this, MenuAdmin.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_USERNAME, username);
                        intent.putExtra(TAG_NAME, name);
                        finish();
                        startActivity(intent);

                    }else{
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                    }

                    finish();
                }
            }
        };
        thread.start();
    }
}