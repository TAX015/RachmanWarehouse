package com.rachman_warehouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MenuAdmin extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    SharedPreferences sharedpreferences;
    String id, username, name;
    TextView txt_id, txt_username, txt_name;
    ImageButton imb;
    ConnectivityManager conMgr;
    Intent intent;

    Boolean session = false;
    public static final String session_status = "session_status";

    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

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

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        name = sharedpreferences.getString(TAG_NAME, null);

        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        name = getIntent().getStringExtra(TAG_NAME);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_produk, R.id.nav_suplier,
                R.id.nav_admin, R.id.nav_transaksi,R.id.nav_laporan)
                .setDrawerLayout(drawer)
                .build();
        String isi, temp;
        temp = "PEGAWAI";
        isi = String.valueOf(username);
        Menu menu = navigationView.getMenu();
        MenuItem item_admin = menu.findItem(R.id.nav_admin);
        switch (isi){
            case "ADMIN":
                item_admin.setVisible(true);
                break;
            case "PEGAWAI":
                item_admin.setVisible(false);
                break;
        }
//        if(isi == temp) {
////            item_admin.setVisible(false);
//            Toast.makeText(getApplicationContext(), isi+" + "+temp,
//                    Toast.LENGTH_LONG).show();
//        }else {

//        }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        View lay = findViewById(R.id.nav_header);

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        name = sharedpreferences.getString(TAG_NAME, null);

        txt_id = lay.findViewById(R.id.txt_id_admin);
        txt_username = lay.findViewById(R.id.txt_textView);
        txt_name = lay.findViewById(R.id.txt_name_admin);
        imb = lay.findViewById(R.id.btn_exit);

//        id = getIntent().getStringExtra(TAG_ID);
//        username = getIntent().getStringExtra(TAG_USERNAME);

        txt_id.setText("ID : " + id);
        txt_name.setText("Nama : " + name);
        txt_username.setText("Status : " + username);

        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();

                intent = new Intent(getApplicationContext(), Login.class);
                finish();
                startActivity(intent);
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}