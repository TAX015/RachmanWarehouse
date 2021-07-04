package com.rachman_warehouse.ui.suplier;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuplierFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    public static SuplierFragment newInstance() {
        return new SuplierFragment();
    }

    private static final String TAG = Login.class.getSimpleName();
    public static final String url = Server.URL + "data_suplier/data_suplier.php";
    public static final String url_insert = Server.URL + "data_suplier/tambah_suplier.php";
    public static final String url_update = Server.URL + "data_suplier/edit_suplier.php";
    public static final String url_delete = Server.URL + "data_suplier/del_suplier.php";
    public static final String url_by_id = Server.URL + "data_suplier/by_id_suplier.php";
    public final static String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String session_status = "session_status";
    String tag_json_obj = "json_obj_req";
    List<DataSuplier> dataSuplierList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    ConnectivityManager conMgr;
    ProgressDialog pDialog;
    String id,username;
    Boolean session = false;
    SwipeRefreshLayout swipe;
    FloatingActionButton fab;
    AdapterSuplier adapter;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    int success;
    View dialogView;
    String id_suplier,suplier,saless,kontakk,jeniss;
    EditText edit_idsuplier,edit_perusahaan,edit_sales,edit_kontak,edit_jenis;
    TextView txt_idsuplier,txt_perusahaan,txt_sales,txt_kontak,txt_jenis;

    ListView listView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.suplier_fragment, container, false);

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

        fab = (FloatingActionButton) root.findViewById(R.id.fabSuplier);
        swipe = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshAll);
        listView = (ListView) root.findViewById(R.id.listViewAll);

        adapter = new AdapterSuplier(dataSuplierList,getContext());
        listView.setAdapter(adapter);

        swipe.setOnRefreshListener(this);
        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
                dataSuplierList.clear();
                adapter.notifyDataSetChanged();
                loadDataSuplier();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSuplier("","","","","","SIMPAN");
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long id) {
                final String idsuplier = dataSuplierList.get(position).getIdsuplier();
                final CharSequence[] dialogitem = {"Detail","Delete"};
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                detailSuplier(idsuplier);
                                break;
                            case 1:
                                deleteSuplier(idsuplier);
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
        dataSuplierList.clear();
        adapter.notifyDataSetChanged();
        loadDataSuplier();
    }

    private void kosong(){
        edit_idsuplier.setText(null);
        edit_perusahaan.setText(null);
        edit_sales.setText(null);
        edit_kontak.setText(null);
        edit_jenis.setText(null);
    }
    private void kosong2(){
        txt_idsuplier.setText(null);
        txt_perusahaan.setText(null);
        txt_sales.setText(null);
        txt_kontak.setText(null);
        txt_jenis.setText(null);
    }

    private void DialogSuplier(final String idSuplier, String namaPerusahaan, String sls, String knt, String keterangan, String button){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.suplier_edit,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Form Suplier");

        edit_idsuplier = (EditText) dialogView.findViewById(R.id.edit_Id_Suplier);
        edit_perusahaan = (EditText) dialogView.findViewById(R.id.suplier1);
        edit_sales = (EditText) dialogView.findViewById(R.id.suplierSales);
        edit_kontak = (EditText) dialogView.findViewById(R.id.suplierKontak);
        edit_jenis = (EditText) dialogView.findViewById(R.id.suplierjenis);

        if(!idSuplier.isEmpty()){
            edit_idsuplier.setText(idSuplier);
            edit_perusahaan.setText(namaPerusahaan);
            edit_sales.setText(sls);
            edit_kontak.setText(knt);
            edit_jenis.setText(keterangan);
        }else {
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                id_suplier = edit_idsuplier.getText().toString();
                suplier = edit_perusahaan.getText().toString();
                saless = edit_sales.getText().toString();
                kontakk = edit_kontak.getText().toString();
                jeniss = edit_jenis.getText().toString();

                simpan_update();
                detailSuplier(idSuplier);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                detailSuplier(idSuplier);
                dialog.dismiss();
                kosong();
            }
        });
        dialog.show();
    }

    private void DetailDialogSuplier(String idSuplier, String namaPerusahaan, String sls,String knt,String keterangan,String button){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.suplier_detail,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Detail Suplier");

        txt_idsuplier = (TextView) dialogView.findViewById(R.id.txtId_Suplier);
        txt_perusahaan = (TextView) dialogView.findViewById(R.id.txtsuplier1);
        txt_sales = (TextView) dialogView.findViewById(R.id.txtsuplierSales);
        txt_kontak = (TextView) dialogView.findViewById(R.id.txtsuplierKontak);
        txt_jenis = (TextView) dialogView.findViewById(R.id.txtsuplierjenis);

        if(!idSuplier.isEmpty()){
            txt_idsuplier.setText(idSuplier);
            txt_perusahaan.setText("Perusahaan : "+namaPerusahaan);
            txt_sales.setText("Sales : "+sls);
            txt_kontak.setText("Kontak : "+knt);
            txt_jenis.setText("Jenis Produk :"+keterangan);
        }else {
            kosong2();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                id_suplier = txt_idsuplier.getText().toString();

                editSuplier(id_suplier);
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

    private void loadDataSuplier(){
        dataSuplierList.clear();
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
                                DataSuplier dataSuplier = new DataSuplier();
                                dataSuplier.setIdsuplier(JKObject.getString("idsuplier"));
                                dataSuplier.setPerusahaan(JKObject.getString("perusahaan"));
                                dataSuplier.setSales(JKObject.getString("sales"));
                                dataSuplier.setNo(JKObject.getString("kontak"));
                                dataSuplier.setJenisP(JKObject.getString("jenis"));
                                dataSuplierList.add(dataSuplier);
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

        if(id_suplier.isEmpty()){
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

                        loadDataSuplier();
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
                if (id_suplier.isEmpty()){
                    params.put("perusahaan", suplier);
                    params.put("sales", saless);
                    params.put("kontak", kontakk);
                    params.put("jenis", jeniss);
                } else {
                    params.put("id", id_suplier);
                    params.put("perusahaan", suplier);
                    params.put("sales", saless);
                    params.put("kontak", kontakk);
                    params.put("jenis", jeniss);
                }
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void detailSuplier(final String idsuplier){
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
                        String idSuplier = jObj.getString("idsuplier");
                        String namaPerusahaan = jObj.getString("perusahaan");
                        String ssales = jObj.getString("sales");
                        String kkontak = jObj.getString("kontak");
                        String ket = jObj.getString("jenis");

                        DetailDialogSuplier(idSuplier,namaPerusahaan,ssales,kkontak,ket, "EDIT");
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
                params.put("id", idsuplier);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void editSuplier(final String idsuplier){
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
                        String idSuplier = jObj.getString("idsuplier");
                        String namaPerusahaan = jObj.getString("perusahaan");
                        String ssales = jObj.getString("sales");
                        String kkontak = jObj.getString("kontak");
                        String ket = jObj.getString("jenis");

                        DialogSuplier(idSuplier,namaPerusahaan,ssales,kkontak,ket, "UPDATE");
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
                params.put("id", idsuplier);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void deleteSuplier(final String idsuplier){
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
                        loadDataSuplier();
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
                params.put("id", idsuplier);

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