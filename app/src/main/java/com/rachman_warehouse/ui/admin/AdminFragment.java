package com.rachman_warehouse.ui.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rachman_warehouse.Login;
import com.rachman_warehouse.MainActivity;
import com.rachman_warehouse.R;
import com.rachman_warehouse.connect.AppController;
import com.rachman_warehouse.connect.Server;
import com.rachman_warehouse.ui.suplier.DataSuplier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    private static final String TAG = Login.class.getSimpleName();
    public static final String url = Server.URL + "data_admin/data_admin.php";
    public static final String url_insert = Server.URL + "data_admin/tambah_admin.php";
    public static final String url_update = Server.URL + "data_admin/edit_admin.php";
    public static final String url_delete = Server.URL + "data_admin/del_admin.php";
    public static final String url_by_id = Server.URL + "data_admin/by_id_admin.php";
    public final static String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String session_status = "session_status";
    String tag_json_obj = "json_obj_req";
    List<DataAdmin> dataAdminList = new ArrayList<>();
    List<String> valueSTATUS = new ArrayList<>();
    SharedPreferences sharedpreferences;
    ConnectivityManager conMgr;
    ProgressDialog pDialog;
    String id,username;
    Boolean session = false;
    SwipeRefreshLayout swipe;
    FloatingActionButton fab;
    AdapterAdmin adapter;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    Spinner sp_status;
    int success;
    View dialogView;
    String id_admin,nam_admin,user_admin,pass_admin,spinner_posisi,spinner_pilih;
    EditText edit_id,edit_nam,edit_user,edit_pass;
    TextView txt_id,txt_nam,txt_user,txt_pass,txt_status;

    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admin_fragment, container, false);

        conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        sharedpreferences = getActivity().getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        id = getActivity().getIntent().getStringExtra(TAG_ID);
        username = getActivity().getIntent().getStringExtra(TAG_USERNAME);

        fab = (FloatingActionButton) root.findViewById(R.id.fabAdmin);
        swipe = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshAll);
        listView = (ListView) root.findViewById(R.id.listViewAll);

        adapter = new AdapterAdmin(dataAdminList,getContext());
        listView.setAdapter(adapter);

        swipe.setOnRefreshListener(this);
        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
                dataAdminList.clear();
                adapter.notifyDataSetChanged();
                loadDataAdmin();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAdmin("","","","","","SIMPAN");
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long id) {
                final String idadmin = dataAdminList.get(position).getIdlogin();
                final CharSequence[] dialogitem = {"Detail","Delete"};
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                detailAdmin(idadmin);
                                break;
                            case 1:
                                deleteAdmin(idadmin);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });

        return root;
    }
    @Override
    public void onRefresh() {
        dataAdminList.clear();
        adapter.notifyDataSetChanged();
        loadDataAdmin();
    }

    private void kosong(){
        edit_id.setText(null);
        edit_nam.setText(null);
        edit_user.setText(null);
        edit_pass.setText(null);
    }
    private void kosong2(){
        txt_id.setText(null);
        txt_nam.setText(null);
        txt_user.setText(null);
        txt_pass.setText(null);
        txt_status.setText(null);
    }

    private void DialogAdmin(String idAdmin, String namaAdmin, String userAdmin, String passww,String stt, String button){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.admin_edit,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Form Admin");

        edit_id = (EditText) dialogView.findViewById(R.id.txt_id);
        edit_nam = (EditText) dialogView.findViewById(R.id.adminNamaEdit);
        edit_user = (EditText) dialogView.findViewById(R.id.adminUserEdit);
        edit_pass = (EditText) dialogView.findViewById(R.id.adminPassEdit);
        sp_status = (Spinner) dialogView.findViewById(R.id.spinner_status);

        spinner_posisi = stt;
        loadDataSpinner();
        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                spinner_pilih = valueSTATUS.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(!idAdmin.isEmpty()){
            edit_id.setText(idAdmin);
            edit_nam.setText(namaAdmin);
            edit_user.setText(userAdmin);
            edit_pass.setText(passww);
        }else {
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                id_admin = edit_id.getText().toString();
                nam_admin = edit_nam.getText().toString();
                user_admin = edit_user.getText().toString();
                pass_admin = edit_pass.getText().toString();

                simpan_update();
                detailAdmin(id_admin);
                dialog.dismiss();

            }
        });
        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                id_admin = edit_id.getText().toString();
                detailAdmin(id_admin);
                dialog.dismiss();
                kosong();
            }
        });
        dialog.show();
    }

    private void DetailDialogAdmin(String idAdmin, String namaAdmin, String userAdmin, String passww,String stat ,String button){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.admin_detail,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Form Admin");

        txt_id = (TextView) dialogView.findViewById(R.id.txt_idDetail);
        txt_nam = (TextView) dialogView.findViewById(R.id.adminNamaDetail);
        txt_user = (TextView) dialogView.findViewById(R.id.adminUserDetail);
        txt_pass = (TextView) dialogView.findViewById(R.id.adminPassDetail);
        txt_status = (TextView) dialogView.findViewById(R.id.adminStatusDetail);
        if(!idAdmin.isEmpty()){
            txt_id.setText(idAdmin);
            txt_nam.setText("Nama : "+namaAdmin);
            txt_user.setText("Username : "+userAdmin);
            txt_pass.setText("Password : "+passww);
            txt_status.setText("Status : "+stat);
        }else {
            kosong2();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                String textID = txt_id.getText().toString();
                editAdmin(textID);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                kosong2();
            }
        });
        dialog.show();
    }

    private void loadDataSpinner(){
        valueSTATUS.clear();
        valueSTATUS.add("ADMIN");
        valueSTATUS.add("PEGAWAI");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, valueSTATUS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_status.setAdapter(adapter);

        sp_status.setSelection(adapter.getPosition(spinner_posisi));

    }

    private void loadDataAdmin(){
        dataAdminList.clear();
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(true);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray JKArray = obj.getJSONArray("result");

                            for (int i = 0; i < JKArray.length(); i++) {
                                JSONObject JKObject = JKArray.getJSONObject(i);
//
                                DataAdmin dataAdmin = new DataAdmin();
                                 dataAdmin.setIdlogin(JKObject.getString("idlogin"));
                                 dataAdmin.setNama(JKObject.getString("nama"));
                                 dataAdmin.setUsernama(JKObject.getString("usernama"));
                                 dataAdmin.setPass(JKObject.getString("pass"));
                                 dataAdmin.setStatus(JKObject.getString("status"));
                                dataAdminList.add(dataAdmin);
                            }
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            swipe.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hideDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog(); }
                });
        requestQueue.add(stringRequest);
    }

    private void simpan_update(){
        String url;

        if(id_admin.isEmpty()){
            url = url_insert;
        }else {
            url = url_update;
        }
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("Add/update", jObj.toString());

                        loadDataAdmin();
                        kosong();

                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                // jika id kosong maka simpan, jika id ada nilainya maka update
                if (id_admin.isEmpty()){
                    params.put("nama", nam_admin);
                    params.put("usernama", user_admin);
                    params.put("pass", pass_admin);
                    params.put("status", spinner_pilih);
                } else {
                    params.put("id", id_admin);
                    params.put("nama", nam_admin);
                    params.put("usernama", user_admin);
                    params.put("pass", pass_admin);
                    params.put("status", spinner_pilih);
                }
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void detailAdmin(final String idadmin){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_by_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());
                        String idAdmin = jObj.getString("idlogin");
                        String namaAdmin = jObj.getString("nama");
                        String userAdmin = jObj.getString("usernama");
                        String passAdmin  = jObj.getString("pass");
                        String stat  = jObj.getString("status");

                        DetailDialogAdmin(idAdmin, namaAdmin, userAdmin, passAdmin, stat,"Edit");
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idadmin);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void editAdmin(final String idadmin){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_by_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());
                        String idAdmin = jObj.getString("idlogin");
                        String namaAdmin = jObj.getString("nama");
                        String userAdmin = jObj.getString("usernama");
                        String passAdmin  = jObj.getString("pass");
                        String stat  = jObj.getString("status");

                        DialogAdmin(idAdmin, namaAdmin, userAdmin,passAdmin,stat, "UPDATE");
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idadmin);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void deleteAdmin(final String idadmin){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("delete", jObj.toString());
                        loadDataAdmin();
                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idadmin);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}