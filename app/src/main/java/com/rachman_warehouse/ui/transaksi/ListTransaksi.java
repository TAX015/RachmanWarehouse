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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.rachman_warehouse.MenuAdmin;
import com.rachman_warehouse.R;
import com.rachman_warehouse.connect.AppController;
import com.rachman_warehouse.connect.Server;
import com.rachman_warehouse.ui.produk.DataProduk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListTransaksi extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = Login.class.getSimpleName();
    public static final String url = Server.URL + "transaksi/data_list_pesanan.php?id=";
    public static final String url_insert = Server.URL + "transaksi/tambah_list_pesanan.php";
    public static final String url_update = Server.URL + "transaksi/edit_list_pesanan.php";
    public static final String url_by_id = Server.URL + "transaksi/by_id_list_pesanan.php";
    public static final String url_delete = Server.URL + "transaksi/del_list_pesanan.php";
    public static final String url_produk = Server.URL + "data_produk/data_produk.php";
    public final static String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String session_status = "session_status";
    String tag_json_obj = "json_obj_req";
    List<DataListTransaksi> dataListTransaksiList = new ArrayList<>();

    List<DataProduk> dataProdukList = new ArrayList<>();
    List<String> valueProduk = new ArrayList<>();
    List<String> valueIDproduk = new ArrayList<>();
    List<String> valueHargaproduk = new ArrayList<>();
    SharedPreferences sharedpreferences;
    ConnectivityManager conMgr;
    ProgressDialog pDialog;
    String id,username,data_id,nama_pelanggan;
    Boolean session = false;
    SwipeRefreshLayout swipe;
    FloatingActionButton fab;
    Intent intent;

    AdapterListTransaksi adapterListTransaksi;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    int success;
    View dialogView;
    TextView txt_name,txt_total,txt_data;
    Spinner sp_produk;
    String id_jual,id_data,barang,jumlah_produk,harga_akhir,posisi_produk,stok_barang;
    Integer produk_pilih,harga_produk,temp_harga,tmp_stok,stok_produk;
    EditText edit_jumlah, edit_data, edit_id_jual;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaksi);

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
        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        data_id = getIntent().getStringExtra("data_id");
        nama_pelanggan = getIntent().getStringExtra("pelanggan");

        fab = (FloatingActionButton) findViewById(R.id.fabList);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshAll);
        listView = (ListView) findViewById(R.id.listViewAll);
        txt_data = (TextView) findViewById(R.id.txtDataId);
        txt_name = (TextView) findViewById(R.id.txtTransaksi);
        txt_total = (TextView) findViewById(R.id.txtTotal);

        txt_data.setText(data_id);
        txt_name.setText("Pembeli : "+nama_pelanggan);

        adapterListTransaksi = new AdapterListTransaksi(dataListTransaksiList,ListTransaksi.this);
        listView.setAdapter(adapterListTransaksi);

        swipe.setOnRefreshListener(this);
        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
                dataListTransaksiList.clear();
                adapterListTransaksi.notifyDataSetChanged();
                loadDataTransaksi();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogTransaksi("","","","","","SIMPAN");
//                Toast.makeText(getApplicationContext(), data_id +" dan "+ nama_pelanggan,
//                        Toast.LENGTH_LONG).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long id) {
                final String idpenjualan = dataListTransaksiList.get(position).getIdpenjualan();
                final CharSequence[] dialogitem = {"Edit","Delete"};
                dialog = new AlertDialog.Builder(ListTransaksi.this);
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                editTransaksi(idpenjualan);
                                break;
                            case 1:
                                deleteTransaksi(idpenjualan);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
    }

    public void onRefresh() {
        dataListTransaksiList.clear();
        adapterListTransaksi.notifyDataSetChanged();
        loadDataTransaksi();
    }

    private void kosong(){
        edit_id_jual.setText(null);
        String dt = txt_data.getText().toString();
        edit_data.setText(dt);
        edit_jumlah.setText(null);
    }

    private void DialogTransaksi(final String idjual, String iddata, final String produk, final String jumlah, String harga, String button){
        dialog = new AlertDialog.Builder(ListTransaksi.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.transaksi_edit,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Form Transaksi");

        edit_id_jual = (EditText) dialogView.findViewById(R.id.edit_list_transaksi);
        edit_data = (EditText) dialogView.findViewById(R.id.edit_data_transaksi);
        sp_produk = (Spinner) dialogView.findViewById(R.id.edit_spinner_transaksi);
        edit_jumlah = (EditText) dialogView.findViewById(R.id.editJumlah);

        posisi_produk = produk;
        loadSpinnerProduk();
        sp_produk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String pilih = dataProdukList.get(position).getIdbaranag();
                produk_pilih = Integer.valueOf(pilih);
                String hargaa = dataProdukList.get(position).getHargajual();
                temp_harga = Integer.valueOf(hargaa);
                String stk = dataProdukList.get(position).getStok();
                tmp_stok = Integer.valueOf(stk);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(!idjual.isEmpty()){
            edit_id_jual.setText(idjual);
            edit_data.setText(iddata);
            edit_jumlah.setText(jumlah);

        }else {
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
            id_jual = edit_id_jual.getText().toString();
            id_data = edit_data.getText().toString();
            barang = produk_pilih.toString();
            jumlah_produk = edit_jumlah.getText().toString();
                Integer sToK = 0, stok1 = 0,tmp_jumlah = 0;
                if(!jumlah.isEmpty()){
                    stok1 = Integer.valueOf(jumlah);
                }else{
                    stok1 = 0;
                }
                stok_produk = stok1 + tmp_stok;
                tmp_jumlah = Integer.valueOf(jumlah_produk);
                sToK = stok_produk - tmp_jumlah;
                stok_barang =String.valueOf(sToK);
            Integer jumlahh = 0;
            Integer price = 0;
                if(!jumlah_produk.isEmpty()){
                    jumlahh = Integer.valueOf(jumlah_produk);
                    if(tmp_stok > jumlahh){
                        price = Integer.valueOf(temp_harga.toString());
                        harga_produk = price*jumlahh;
                        harga_akhir = String.valueOf(harga_produk);
                        simpan_update();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(getApplicationContext(), "Jumlah STOK Produk Kurang",
                                Toast.LENGTH_LONG).show();
                    }

                }else {
                    harga_produk = 0;
                    harga_akhir = String.valueOf(harga_produk);
                }
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
        dataListTransaksiList.clear();
        adapterListTransaksi.notifyDataSetChanged();
        swipe.setRefreshing(true);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+data_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray JKArray = obj.getJSONArray("result");

                            for (int i = 0; i < JKArray.length(); i++) {
                                JSONObject JKObject = JKArray.getJSONObject(i);

                                DataListTransaksi dataListTransaksi = new DataListTransaksi();
                                dataListTransaksi.setIdpenjualan(JKObject.getString("idpenjualan"));
                                dataListTransaksi.setIddata(JKObject.getString("iddata"));
                                dataListTransaksi.setProduk(JKObject.getString("produk"));
                                dataListTransaksi.setJumlah(JKObject.getString("jumlah"));
                                dataListTransaksi.setHarga(JKObject.getString("harga"));

                                dataListTransaksiList.add(dataListTransaksi);
                            }
                            int price = 0;
                            String temp;
                            int ttal = 0;
                            Locale localeID = new Locale("in", "ID");
                            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                            for(int i=0;i<dataListTransaksiList.size();i++){
                                temp = dataListTransaksiList.get(i).getHarga();
                                if(temp == null){
                                    temp = String.valueOf(0);
                                    txt_total.setText("Total : Rp."+temp);
                                }else{
                                    ttal = Integer.valueOf(temp);
                                    price += ttal;

                                    txt_total.setText("Total : "+formatRupiah.format(price));
                                }
                            }
                            listView.setAdapter(adapterListTransaksi);
                            adapterListTransaksi.notifyDataSetChanged();
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void loadSpinnerProduk(){
        dataProdukList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_produk,
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

                            valueIDproduk = new ArrayList<String>();
                            valueProduk = new ArrayList<String>();
                            valueHargaproduk = new ArrayList<String>();
                            for (int i = 0; i < dataProdukList.size(); i++) {
                                valueIDproduk.add(dataProdukList.get(i).getIdbaranag());
                                valueProduk.add(dataProdukList.get(i).getBarang());
                                valueHargaproduk.add(dataProdukList.get(i).getHargajual());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListTransaksi.this,
                                    android.R.layout.simple_spinner_item, valueProduk);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sp_produk.setAdapter(adapter);

                            sp_produk.setSelection(adapter.getPosition(posisi_produk));

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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();}
                });
        requestQueue.add(stringRequest);
    }

    private void simpan_update(){
        String url;

        if(id_jual.isEmpty()){
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

                        dataListTransaksiList.clear();
                        adapterListTransaksi.notifyDataSetChanged();
                        loadDataTransaksi();
                        kosong();

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                // jika id kosong maka simpan, jika id ada nilainya maka update
                if (id_jual.isEmpty()){
                    params.put("jualid", id_data);
                    params.put("barang", barang);
                    params.put("jumlah", jumlah_produk);
                    params.put("harga", harga_akhir);

                } else {
                    params.put("id", id_jual);
                    params.put("barang", barang);
                    params.put("stok", stok_barang);
                    params.put("jumlah", jumlah_produk);
                    params.put("harga", harga_akhir);
                }
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void editTransaksi(final String idtransaksi){
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
                        String idPenjualan = jObj.getString("idpenjualan");
                        String idData = jObj.getString("iddata");
                        String idBarang = jObj.getString("produk");
                        String jumlah = jObj.getString("jumlah");
                        String harga = jObj.getString("harga");

                        DialogTransaksi(idPenjualan, idData, idBarang, jumlah, harga, "UPDATE");
                        adapterListTransaksi.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idtransaksi);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void deleteTransaksi(final String idtransaksi){
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
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapterListTransaksi.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idtransaksi);

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

    @Override
    public void onBackPressed() {
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        if (session) {
            intent = new Intent(ListTransaksi.this,MenuAdmin.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }
    }
}