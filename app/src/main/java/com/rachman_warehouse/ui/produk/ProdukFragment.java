package com.rachman_warehouse.ui.produk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rachman_warehouse.Login;
import com.rachman_warehouse.R;
import com.rachman_warehouse.connect.AppController;
import com.rachman_warehouse.connect.Server;
import com.rachman_warehouse.ui.suplier.DataSuplier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdukFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static ProdukFragment newInstance() {
        return new ProdukFragment();
    }

    private static final String TAG = Login.class.getSimpleName();
    public static final String url = Server.URL + "data_produk/data_produk.php";
    public static final String url_insert = Server.URL + "data_produk/tambah_produk.php";
    public static final String url_update = Server.URL + "data_produk/edit_produk.php";
    public static final String url_delete = Server.URL + "data_produk/del_produk.php";
    public static final String url_by_id = Server.URL + "data_produk/by_id_produk.php";
    public static final String url_stok = Server.URL + "data_produk/tambah_stok.php";
    public static final String urlsuplier = Server.URL + "data_suplier/data_suplier.php";
    public final static String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String session_status = "session_status";
    String tag_json_obj = "json_obj_req";
    List<DataProduk> dataProdukList = new ArrayList<>();
    List<DataSuplier> dataSuplierList = new ArrayList<>();
    List<String> valueSuplier = new ArrayList<>();
    List<String> valueIDsuplier = new ArrayList<>();
    SharedPreferences sharedpreferences;
    ConnectivityManager conMgr;
    ProgressDialog pDialog;
    String id,username;
    Boolean session = false;
    SwipeRefreshLayout swipe;
    FloatingActionButton fab;

    AdapterProduk adapterProduk;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    int success;
    View dialogView;
    String posisi_suplier,posisi_idJenis;
    Spinner spinner_id_suplier,spinner_id_jenis;
    Integer suplier_pilih,jenis_pilih;
    String id_barang,id_suplier,id_jenis,id_login,barang,stok,harga_beli,harga_jual;
    EditText edit_id_barang,edit_id_login,edit_barang,edit_stok,edit_harga_beli,edit_harga_jual,stok_id,tambah_stok;
    TextView detail_id_barang,detail_id_login,detail_suplier,detail_jenis,detail_barang,
            detail_stok,detail_harga_beli,detail_harga_jual,nama_barang;

    ListView listView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.produk_fragment, container, false);

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

        fab = (FloatingActionButton) root.findViewById(R.id.fabProduk);
        swipe = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshAll);
        listView = (ListView) root.findViewById(R.id.listViewAll);

        Collections.sort(dataProdukList, new ProdukComparator());
        adapterProduk = new AdapterProduk(dataProdukList,getContext());
        listView.setAdapter(adapterProduk);

        swipe.setOnRefreshListener(this);
        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
                dataProdukList.clear();
                adapterProduk.notifyDataSetChanged();
                loadDataProduk();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogProduk("","","","","","","","SIMPAN");
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long id) {
                final String idproduk = dataProdukList.get(position).getIdbarang();
                final String nm_produk = dataProdukList.get(position).getBarang();
                final CharSequence[] dialogitem = {"Tambah Stok","Detail","Delete"};
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                stokBarang(idproduk,nm_produk);
                                break;
                            case 1:
                                detailBarang(idproduk);
                                break;
                            case 2:
                                deleteBarang(idproduk);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });

        return root;
    }

    public void onRefresh() {
        dataProdukList.clear();
        adapterProduk.notifyDataSetChanged();
        loadDataProduk();
    }

    private void kosong(){
        edit_id_barang.setText(null);
        edit_barang.setText(null);
        edit_stok.setText(null);
        edit_id_login.setText(null);
        edit_harga_beli.setText(null);
        edit_harga_jual.setText(null);
    }

    private void kosong2(){
        detail_id_barang.setText(null);
        detail_barang.setText(null);
        detail_suplier.setText(null);
        detail_stok.setText(null);
        detail_harga_beli.setText(null);
        detail_harga_jual.setText(null);
    }

    private void DialogProduk(final String idProduk, final String idPerusahaan, final String idAdmin,
                              String namaProduk, String stokProduk, String hargaBeli, final String hargaJual, String button){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.produk_edit,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Form Produk");

        edit_id_barang = (EditText) dialogView.findViewById(R.id.edit_Id_produk);
        spinner_id_suplier = (Spinner) dialogView.findViewById(R.id.edit_spinner_perusahaan);
        edit_id_login = (EditText) dialogView.findViewById(R.id.edit_Id_admin);
        edit_barang = (EditText) dialogView.findViewById(R.id.edit_nama_produk);
        edit_stok = (EditText) dialogView.findViewById(R.id.edit_stok_produk);
        edit_harga_beli = (EditText) dialogView.findViewById(R.id.edit_beli);
        edit_harga_jual = (EditText) dialogView.findViewById(R.id.edit_jual);

        posisi_suplier = idPerusahaan;
        loadSpinnerSuplier();
        spinner_id_suplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                    String pilih = dataSuplierList.get(position).getIdsuplier();
                    suplier_pilih = Integer.valueOf(pilih);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(!idProduk.isEmpty()){
            edit_id_barang.setText(idProduk);
//            edit_id_barang.setText("");
            edit_id_login.setText(id);
            edit_barang.setText(namaProduk);
            edit_stok.setText(stokProduk);
            edit_harga_beli.setText(hargaBeli);
            edit_harga_jual.setText(hargaJual);
        }else {
            kosong();
            edit_id_login.setText(id);
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                id_barang = edit_id_barang.getText().toString();
                id_suplier = suplier_pilih.toString();
                id_login = edit_id_login.getText().toString();
                barang = edit_barang.getText().toString();
                stok = edit_stok.getText().toString();
                harga_beli = edit_harga_beli.getText().toString();
                harga_jual = edit_harga_jual.getText().toString();


                simpan_update();
//                detailBarang(id_barang);
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

    private void DetailDialogProduk(final String idProduk, final String perusahaan, final String idAdmin,
                              String namaProduk, String stokProduk, String hargaBeli, final String hargaJual, String button){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.produk_detail,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Detail Produk");

        detail_id_barang = (TextView) dialogView.findViewById(R.id.detail_Id_produk);
        detail_suplier = (TextView) dialogView.findViewById(R.id.detail_supll);
        detail_id_login = (TextView) dialogView.findViewById(R.id.detail_Id_admin);
        detail_barang = (TextView) dialogView.findViewById(R.id.detail_nama_produk);
        detail_stok = (TextView) dialogView.findViewById(R.id.detail_stok_produk);
        detail_harga_beli = (TextView) dialogView.findViewById(R.id.detail_beli);
        detail_harga_jual = (TextView) dialogView.findViewById(R.id.detail_jual);

        if(!idProduk.isEmpty()){
            detail_id_barang.setText(idProduk);
            detail_id_login.setText(idAdmin);
            detail_barang.setText("Produk : "+namaProduk);
            detail_suplier.setText("Suplier : "+perusahaan);
            detail_stok.setText("Stok : "+stokProduk);
            detail_harga_beli.setText("Harga Beli : "+hargaBeli);
            detail_harga_jual.setText("Harga Jual : "+hargaJual);
        }else {
            kosong2();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String produkId = detail_id_barang.getText().toString();
                editBarang(produkId);
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

    private void loadDataProduk(){
        dataProdukList.clear();
        adapterProduk.notifyDataSetChanged();
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
                                DataProduk dataProduk = new DataProduk();
                                dataProduk.setIdbaranag(JKObject.getString("idbarang"));
                                dataProduk.setIdsuplier(JKObject.getString("idsuplier"));
                                dataProduk.setNamasuplier(JKObject.getString("perusahaan"));
                                dataProduk.setIdlogin(JKObject.getString("idadmin"));
                                dataProduk.setNamaadmin(JKObject.getString("nama"));
                                dataProduk.setBarang(JKObject.getString("produk"));
                                dataProduk.setStok(JKObject.getString("stok"));
                                dataProduk.setHargabeli(JKObject.getString("hargabeli"));
                                dataProduk.setHargajual(JKObject.getString("hargajual"));

                                dataProdukList.add(dataProduk);
                            }
//                            adapter = new AdapterAdmin(dataAdminList,getContext());
                            Collections.sort(dataProdukList, new ProdukComparator());
                            listView.setAdapter(adapterProduk);
                            adapterProduk.notifyDataSetChanged();
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

    private void loadSpinnerSuplier(){
        dataSuplierList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlsuplier,
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
                                dataSuplierList.add(dataSuplier);
                            }

                            valueIDsuplier = new ArrayList<String>();
                            valueSuplier = new ArrayList<String>();
                            for (int i = 0; i < dataSuplierList.size(); i++) {
                                valueIDsuplier.add(dataSuplierList.get(i).getIdsuplier());
                                valueSuplier.add(dataSuplierList.get(i).getPerusahaan());

                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                    android.R.layout.simple_spinner_item, valueSuplier);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner_id_suplier.setAdapter(adapter);

                            spinner_id_suplier.setSelection(adapter.getPosition(posisi_suplier));
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
                        hideDialog();}
                });
        requestQueue.add(stringRequest);
    }

    private void simpan_update(){
        String urlp;

        if(id_barang.isEmpty()){
            urlp = url_insert;
        }else {
            urlp = url_update;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, urlp, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("Add/update", jObj.toString());

                        dataProdukList.clear();
                        adapterProduk.notifyDataSetChanged();
                        loadDataProduk();
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
                if (id_barang.isEmpty()){
                    params.put("perusahaan", id_suplier);
                    params.put("nama", id);
                    params.put("produk", barang);
                    params.put("stok", stok);
                    params.put("hargabeli", harga_beli);
                    params.put("hargajual", harga_jual);
                } else {
                    params.put("id", id_barang);
                    params.put("perusahaan", id_suplier);
                    params.put("nama", id);
                    params.put("produk", barang);
                    params.put("stok", stok);
                    params.put("hargabeli", harga_beli);
                    params.put("hargajual", harga_jual);
                }
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void tambahStok(){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_stok, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("Add/update", jObj.toString());

                        dataProdukList.clear();
                        adapterProduk.notifyDataSetChanged();
                        loadDataProduk();
                        stok_id.setText(null);
                        nama_barang.setText(null);
                        tambah_stok.setText(null);

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

                    params.put("id", id_barang);
                    params.put("stok", stok);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void stokBarang(final String idProduk, String nm_produk){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.produk_stok,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Tambah Stok Produk");

        stok_id = (EditText) dialogView.findViewById(R.id.stok_Id_produk);
        nama_barang = (TextView) dialogView.findViewById(R.id.nama_barang);
        tambah_stok = (EditText) dialogView.findViewById(R.id.stok_tambah);

        if(!idProduk.isEmpty()){
            stok_id.setText(idProduk);
            nama_barang.setText(nm_produk);
            tambah_stok.setText(null);
        }

        dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                id_barang = stok_id.getText().toString();
                stok = tambah_stok.getText().toString();

                tambahStok();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                stok_id.setText(null);
                nama_barang.setText(null);
                tambah_stok.setText(null);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void detailBarang(final String idbarang){
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
                        String idProduk = jObj.getString("idbarang");
                        String Suplier = jObj.getString("perusahaan");
                        String Admin = jObj.getString("nama");
                        String namaBarang = jObj.getString("produk");
                        String stokbarang = jObj.getString("stok");
                        String beliBarang = jObj.getString("hargabeli");
                        String jualBarang = jObj.getString("hargajual");

                        DetailDialogProduk(idProduk, Suplier,Admin,namaBarang,
                                stokbarang,beliBarang,jualBarang, "Edit");
                        adapterProduk.notifyDataSetChanged();

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
                params.put("id", idbarang);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void editBarang(final String idbarang){
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
                        String idProduk = jObj.getString("idbarang");
                        String Suplier = jObj.getString("perusahaan");
                        String Admin = jObj.getString("nama");
                        String namaBarang = jObj.getString("produk");
                        String stokbarang = jObj.getString("stok");
                        String beliBarang = jObj.getString("hargabeli");
                        String jualBarang = jObj.getString("hargajual");

                        DialogProduk(idProduk, Suplier,Admin,namaBarang,
                                stokbarang,beliBarang,jualBarang, "UPDATE");
                        adapterProduk.notifyDataSetChanged();

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
                params.put("id", idbarang);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void deleteBarang(final String idbarang){
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
                        loadDataProduk();
                        Toast.makeText(getContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapterProduk.notifyDataSetChanged();
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
                params.put("id", idbarang);

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