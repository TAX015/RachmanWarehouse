package com.rachman_warehouse.ui.transaksi;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import com.rachman_warehouse.R;
import com.rachman_warehouse.connect.AppController;
import com.rachman_warehouse.connect.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PemesananFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static PemesananFragment newInstance() {
        return new PemesananFragment();
    }

    private static final String TAG = Login.class.getSimpleName();
    public static final String url = Server.URL + "transaksi/data_pesanan.php";
    public static final String url_insert = Server.URL + "transaksi/tambah_pesanan.php";
    public static final String url_update = Server.URL + "transaksi/edit_pesanan.php";
    public static final String url_by_id = Server.URL + "transaksi/by_id_pesanan.php";
    public static final String url_delete = Server.URL + "transaksi/del_pesanan.php";
    public final static String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String session_status = "session_status";
    String tag_json_obj = "json_obj_req";
    List<DataPemesanan> dataPemesananList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    ConnectivityManager conMgr;
    ProgressDialog pDialog;
    String id,username;
    Boolean session = false;
    SwipeRefreshLayout swipe;
    FloatingActionButton fab;

    AdapterPemesanan adapterPemesanan;

    Intent intent;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    int success;
    View dialogView;
    String id_transaksi, admin, beli, tgl;
    EditText edit_pembeli,edit_transaksi, edit_admin, edit_tgl;

    ListView listView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pemesanan_fragment, container, false);

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

        fab = (FloatingActionButton) root.findViewById(R.id.fabpemesanan);
        swipe = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshAll);
        listView = (ListView) root.findViewById(R.id.listViewAll);

        Collections.sort(dataPemesananList, new TransaksiComparator().reversed());
        adapterPemesanan = new AdapterPemesanan(dataPemesananList,getContext());
        listView.setAdapter(adapterPemesanan);

        swipe.setOnRefreshListener(this);
        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
                dataPemesananList.clear();
                adapterPemesanan.notifyDataSetChanged();
                loadDataTransaksi();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogTransaksi("","","","","SIMPAN");
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long idd) {
                final String tgltransaksi = dataPemesananList.get(position).getTgljual();
                final String transaksi = dataPemesananList.get(position).getIdpenjualan();
                beli = dataPemesananList.get(position).getPembeli();
                final CharSequence[] dialogitem = {"Detail","Delete"};
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                if (session) {
                                    Bundle bundle = new Bundle();
                                    intent = new Intent(getContext(),ListTransaksi.class);
                                    intent.putExtra(TAG_ID, id);
                                    intent.putExtra(TAG_USERNAME, username);
                                    intent.putExtra("data_id", transaksi);
                                    intent.putExtra("pelanggan",beli);
                                    getActivity().finish();
                                    getActivity().startActivity(intent);
                                }

                                break;
                            case 1:
                                deleteTransaksi(transaksi);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
        return  root;
    }

    public void onRefresh() {
        dataPemesananList.clear();
        adapterPemesanan.notifyDataSetChanged();
        loadDataTransaksi();
    }

    private void kosong(){
        edit_transaksi.setText(null);
        edit_admin.setText(null);
        edit_pembeli.setText(null);
        edit_tgl.setText(null);
    }

    private String getRandomString(int i){
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        while (i > 0){
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i--;
        }
        return result.toString();
    }

    private void DialogTransaksi(final String idtransaksi, String idadmin, String pembeli, final String tgljual, String button){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.pemesanan_edit,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Form Transaksi");

        edit_transaksi = (EditText) dialogView.findViewById(R.id.txtTransaksi);
        edit_admin = (EditText) dialogView.findViewById(R.id.txtAdmin);
        edit_pembeli = (EditText) dialogView.findViewById(R.id.editPembeli);
        edit_tgl = (EditText) dialogView.findViewById(R.id.txtTgl);

        if(!tgljual.isEmpty()){
            edit_transaksi.setText(idtransaksi);
            edit_admin.setText(id);
            edit_pembeli.setText(pembeli);
            edit_tgl.setText(tgljual);
        }else {
            kosong();
            edit_transaksi.setText(getRandomString(7));
            edit_admin.setText(id);
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                id_transaksi = edit_transaksi.getText().toString();
                admin = edit_admin.getText().toString();
                beli = edit_pembeli.getText().toString();
                tgl = edit_tgl.getText().toString();

                if (tgl.isEmpty()) {
                    Bundle bundle = new Bundle();
                    intent = new Intent(getContext(),ListTransaksi.class);
                    bundle.putString("data_id", id_transaksi);
                    bundle.putString("pelanggan",beli);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);

                    dialog.dismiss();
                }
                simpan_update();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                kosong();
            }
        });
        dialog.show();
    }

    private void loadDataTransaksi(){
        dataPemesananList.clear();
        adapterPemesanan.notifyDataSetChanged();
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
                                DataPemesanan dataPemesanan = new DataPemesanan();
                                dataPemesanan.setIdpenjualan(JKObject.getString("idpenjualan"));
                                dataPemesanan.setIdadmin(JKObject.getString("nama"));
                                dataPemesanan.setPembeli(JKObject.getString("pembeli"));
                                dataPemesanan.setTgljual(JKObject.getString("tgl"));

                                dataPemesananList.add(dataPemesanan);
                            }
//                            adapter = new AdapterAdmin(dataAdminList,getContext());
                            Collections.sort(dataPemesananList, new TransaksiComparator().reversed());
                            listView.setAdapter(adapterPemesanan);
                            adapterPemesanan.notifyDataSetChanged();
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

        if(tgl.isEmpty()){
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

                        loadDataTransaksi();
                        kosong();

                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

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

                if (tgl.isEmpty()){
                    params.put("jualid", id_transaksi);
                    params.put("admin", admin);
                    params.put("pembeli", beli);
                } else {
                    params.put("tgl", tgl);
                    params.put("admin", admin);
                    params.put("pembeli", beli);
                }

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void editTransaksi(final String transaksi){
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
                        String idpemesanan = jObj.getString("idpenjualan");
                        String adminlogin = jObj.getString("nama");
                        String pembeli = jObj.getString("pembeli");
                        String tgljual = jObj.getString("tgl");

                        DialogTransaksi(idpemesanan,adminlogin,pembeli,tgljual, "UPDATE");
                        adapterPemesanan.notifyDataSetChanged();

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
                params.put("tgl", transaksi);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void deleteTransaksi(final String tgltransaksi){
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
                        loadDataTransaksi();
                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapterPemesanan.notifyDataSetChanged();
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
                params.put("tgl", tgltransaksi);

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