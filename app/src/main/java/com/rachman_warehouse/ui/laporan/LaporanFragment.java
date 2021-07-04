package com.rachman_warehouse.ui.laporan;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.rachman_warehouse.ui.produk.DataProduk;
import com.rachman_warehouse.ui.transaksi.AdapterPemesanan;
import com.rachman_warehouse.ui.transaksi.DataListTransaksi;
import com.rachman_warehouse.ui.transaksi.DataPemesanan;
import com.rachman_warehouse.ui.transaksi.ListTransaksi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LaporanFragment extends Fragment {


    public static LaporanFragment newInstance() {
        return new LaporanFragment();
    }

    private static final String TAG = Login.class.getSimpleName();
    public static final String url = Server.URL + "laporan/data_laporan.php?id=";
    public static final String url_insert = Server.URL + "laporan/tambah_laporan.php";
    public static final String url_update = Server.URL + "laporan/edit_laporan.php";
    public static final String url_delete = Server.URL + "laporan/del_laporan.php";
    public static final String url_by_id = Server.URL + "laporan/by_id_laporan.php";
    public static final String urlP = Server.URL + "transaksi/data_list_all.php";
    public static final String urlB = Server.URL + "data_produk/data_produk.php";
    public final static String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String session_status = "session_status";
    String tag_json_obj = "json_obj_req";
    List<DataProduk> dataProdukList = new ArrayList<>();
    List<DataLaporan> dataLaporanList = new ArrayList<>();
    List<DataListTransaksi> dataListTransaksiList = new ArrayList<>();
    List<String> valueLaporan1,valueLaporan2,valueLaporan3,valueLaporan4;
    SharedPreferences sharedpreferences;
    ConnectivityManager conMgr;
    String id,username;
    Boolean session = false;
    ListView listView1,listView2,listView3,listView4;

    String satu = "MASUK";
    String dua = "KELUAR";

    Intent intent;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    int success;
    View dialogView;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    int stokJual,price,ttal,total,msk,msk2,m,klr,klr2,k;
    TextView txt_penjualan,txt_beli, txt_total,txt_keluar,txt_masuk,txt_satu,txt_dua,txt_tiga,txt_empat;
    EditText edit_idLap,edit_login,edit_lap,edit_tot,edit_jen;
    String idLApor,idlogin,lapor,tottal,jennis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.laporan_fragment, container, false);

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

        txt_penjualan = root.findViewById(R.id.txtPenjualan);
        txt_beli = root.findViewById(R.id.txtPembelian);
        txt_total = root.findViewById(R.id.txt_keuntungan);
        txt_keluar = root.findViewById(R.id.txtKeluarLain);
        txt_masuk = root.findViewById(R.id.txtMasukLain);

        listView1 = root.findViewById(R.id.lv1);
        listView2 = root.findViewById(R.id.lv2);
        listView3 = root.findViewById(R.id.lv3);
        listView4 = root.findViewById(R.id.lv4);

        txt_penjualan.setText(null);
        txt_beli.setText(null);
        txt_total.setText(null);
        txt_masuk.setText(null);
        txt_keluar.setText(null);

        dataListTransaksiList.clear();
        dataProdukList.clear();
        dataLaporanList.clear();
        loadDataPenjualan();

        txt_penjualan.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] dialogitem = {"Detail"};
                final String isinya = txt_penjualan.getText().toString();
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                DialogPertama(isinya);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });

        txt_beli.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] dialogitem = {"Detail"};
                final String isinya = txt_beli.getText().toString();
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                DialogKedua(isinya);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });

        txt_masuk.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] dialogitem = {"Detail"};
                final String isinya = txt_masuk.getText().toString();
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                DialogTiga(isinya);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });

        txt_keluar.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] dialogitem = {"Detail"};
                final String isinya = txt_keluar.getText().toString();
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                DialogEmpat(isinya);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });

        return  root;
    }

    private void loadDataPenjualan(){
        dataListTransaksiList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlP,
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
                                dataListTransaksi.setHargajual(JKObject.getString("hargajual"));
                                dataListTransaksi.setJumlah(JKObject.getString("jumlah"));
                                dataListTransaksi.setHarga(JKObject.getString("harga"));

                                dataListTransaksiList.add(dataListTransaksi);
                            }
                            valueLaporan1 = new ArrayList<String>();
                            for (int i = 0; i < dataListTransaksiList.size(); i++) {
                                valueLaporan1.add("Produk : "+dataListTransaksiList.get(i).getProduk()
                                        +"\nJumlah : "+ dataListTransaksiList.get(i).getJumlah()
                                        +"\nHarga : "+formatRupiah.format(Integer.valueOf(dataListTransaksiList.get(i).getHarga())));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,valueLaporan1);
                            listView1.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            price = 0;
                            stokJual =0;

                            String tmp,tmp2,tmp3;
                            int ttl = 0,jml=0,jual=0,stk=0;
                            for(int i=0;i<dataListTransaksiList.size();i++){
                                tmp = dataListTransaksiList.get(i).getHarga();
                                tmp2 = dataListTransaksiList.get(i).getJumlah();
                                tmp3 = dataListTransaksiList.get(i).getHargajual();

                                ttl = Integer.valueOf(tmp);
                                jml = Integer.valueOf(tmp2);
                                jual = Integer.valueOf(tmp3);
                                stk = jml*jual;
                                stokJual += stk;
                                price += ttl;

                            }
                            loadDataBeli();
                            txt_penjualan.setText(formatRupiah.format(price));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void loadDataBeli(){
        dataProdukList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlB,
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

                            valueLaporan2 = new ArrayList<String>();
                            for (int i = 0; i < dataProdukList.size(); i++) {
                                valueLaporan2.add("Produk : "+dataProdukList.get(i).getBarang()
                                        +"\nStok : "+ dataProdukList.get(i).getStok()
                                        +"\nHarga Beli : "+ formatRupiah.format(Integer.valueOf(dataProdukList.get(i).getHargabeli())));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,valueLaporan2);
                            listView2.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            String temp,temp2;
                            int stok=0,beli=0,temp3=0;
                            ttal = 0;
                            total = 0;
                            for(int i=0;i<dataProdukList.size();i++){
                                temp = dataProdukList.get(i).getHargabeli();
                                temp2 = dataProdukList.get(i).getStok();

                                stok=Integer.valueOf(temp2);
                                beli=Integer.valueOf(temp);

                                temp3 = stok*beli;
                                ttal += temp3;

                            }
                            loadDataSatu(satu);
                            txt_beli.setText(null);
                            total = ttal+stokJual;
                            txt_beli.setText(formatRupiah.format(total));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void loadDataSatu(String jenis){
        dataLaporanList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+jenis,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray JKArray = obj.getJSONArray("result");

                            for (int i = 0; i < JKArray.length(); i++) {
                                JSONObject JKObject = JKArray.getJSONObject(i);

                                DataLaporan dataLaporan = new DataLaporan();
                                dataLaporan.setIdlaporan(JKObject.getString("idlap"));
                                dataLaporan.setIdlogin(JKObject.getString("idadm"));
                                dataLaporan.setLapor(JKObject.getString("namalaporan"));
                                dataLaporan.setTotl(JKObject.getString("total"));
                                dataLaporan.setJenislap(JKObject.getString("jenislap"));

                                dataLaporanList.add(dataLaporan);
                            }

                            int price = 0;
                            msk2=0;
                            String temp;
                            int ttal = 0;
                            for(int i=0;i<dataLaporanList.size();i++){
                                temp = dataLaporanList.get(i).getTotl();

                                    ttal = Integer.valueOf(temp);
                                    msk2 += ttal;

                            }
                            valueLaporan3 = new ArrayList<String>();
                            for (int i = 0; i < dataLaporanList.size(); i++) {
                                valueLaporan3.add("Pemasukan : "+dataLaporanList.get(i).lapor
                                        +"\nTotal : "+dataLaporanList.get(i).getTotl());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,valueLaporan3);
                            listView3.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            loadDataDua(dua);
                            txt_masuk.setText(null);
                            txt_masuk.setText(formatRupiah.format(msk2));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void loadDataDua(String jenis){
        dataLaporanList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+jenis,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray JKArray = obj.getJSONArray("result");

                            for (int i = 0; i < JKArray.length(); i++) {
                                JSONObject JKObject = JKArray.getJSONObject(i);

                                DataLaporan dataLaporan = new DataLaporan();
                                dataLaporan.setIdlaporan(JKObject.getString("idlap"));
                                dataLaporan.setIdlogin(JKObject.getString("idadm"));
                                dataLaporan.setLapor(JKObject.getString("namalaporan"));
                                dataLaporan.setTotl(JKObject.getString("total"));
                                dataLaporan.setJenislap(JKObject.getString("jenislap"));

                                dataLaporanList.add(dataLaporan);
                            }
                            valueLaporan4 = new ArrayList<String>();
                            for (int i = 0; i < dataLaporanList.size(); i++) {
                                valueLaporan4.add("Pengeluaran : "+dataLaporanList.get(i).lapor
                                        +"\nTotal : "+dataLaporanList.get(i).getTotl());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,valueLaporan4);
                            listView4.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            String temp;
                            klr2 =0;
                            msk = 0;
                            m=0;
                            k=0;
                            int ttal = 0;
                            for(int i=0;i<dataLaporanList.size();i++){
                                temp = dataLaporanList.get(i).getTotl();

                                    ttal = Integer.valueOf(temp);
                                    klr2 += ttal;

                            }
                            txt_keluar.setText(null);
                            txt_keluar.setText(formatRupiah.format(klr2));
                            msk = price-total;
                            m = msk + msk2;
                            k = m - klr2;
                            txt_total.setText(null);
                            txt_total.setText(formatRupiah.format(k));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void DialogPertama(String isinya){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.laporan_pertama,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Data List Penjualan");

        txt_satu = (TextView) dialogView.findViewById(R.id.txtTotalPertama);
        listView1 = (ListView) dialogView.findViewById(R.id.lv1);
        dataListTransaksiList.clear();
        laporanSatu();
        txt_satu.setText(null);
        txt_satu.setText("Total : "+isinya);

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void laporanSatu(){
        dataListTransaksiList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlP,
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
                                dataListTransaksi.setHargajual(JKObject.getString("hargajual"));
                                dataListTransaksi.setJumlah(JKObject.getString("jumlah"));
                                dataListTransaksi.setHarga(JKObject.getString("harga"));

                                dataListTransaksiList.add(dataListTransaksi);
                            }
                            valueLaporan1 = new ArrayList<String>();
                            for (int i = 0; i < dataListTransaksiList.size(); i++) {
                                valueLaporan1.add("Produk : "+dataListTransaksiList.get(i).getProduk()
                                        +"\nJumlah : "+ dataListTransaksiList.get(i).getJumlah()
                                        +"\nHarga : "+formatRupiah.format(Integer.valueOf(dataListTransaksiList.get(i).getHarga())));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,valueLaporan1);

                            listView1.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void DialogKedua(String isinya){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.laporan_kedua,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Data List Gudang");

        txt_dua = (TextView) dialogView.findViewById(R.id.txtTotalKedua);
        listView2 = (ListView) dialogView.findViewById(R.id.lv2);
        dataProdukList.clear();
        laporanDua();
        txt_dua.setText(null);
        txt_dua.setText("Total : "+isinya);
        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void laporanDua(){
        dataProdukList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlB,
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

                            valueLaporan2 = new ArrayList<String>();
                            for (int i = 0; i < dataProdukList.size(); i++) {
                                valueLaporan2.add("Produk : "+dataProdukList.get(i).getBarang()
                                        +"\nStok : "+ dataProdukList.get(i).getStok()
                                        +"\nHarga Beli : "+ formatRupiah.format(Integer.valueOf(dataProdukList.get(i).getHargabeli())));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,valueLaporan2);

                            listView2.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }
    private void DialogTiga(String isinya){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.laporan_tiga,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Data Pemasukan Lain-Lain");

        txt_tiga = (TextView) dialogView.findViewById(R.id.txtTotalTiga);
        listView3 = (ListView) dialogView.findViewById(R.id.lv3);
        dataLaporanList.clear();
        laporanTiga(satu);
        txt_tiga.setText(null);
        txt_tiga.setText("Total Pemasukan : \n"+isinya);

        listView3.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long id) {
                final String idsuplier = dataLaporanList.get(position).getIdlaporan();
                final CharSequence[] dialogitem = {"Edit","Delete"};
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                String jdulk = "Edit Laporan Pemasukan";
                                editLaporan(jdulk,idsuplier);
                                break;
                            case 1:
                                deleteLaporan(idsuplier);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });

        dialog.setPositiveButton("Tambah Data", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String jud = "Tambah Laporan Pemasukan";
                DialogTambahLaporan(jud,"","","","",satu);

            }
        });
        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void laporanTiga(String jenis){
        dataLaporanList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+jenis,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray JKArray = obj.getJSONArray("result");

                            for (int i = 0; i < JKArray.length(); i++) {
                                JSONObject JKObject = JKArray.getJSONObject(i);

                                DataLaporan dataLaporan = new DataLaporan();
                                dataLaporan.setIdlaporan(JKObject.getString("idlap"));
                                dataLaporan.setIdlogin(JKObject.getString("idadm"));
                                dataLaporan.setLapor(JKObject.getString("namalaporan"));
                                dataLaporan.setTotl(JKObject.getString("total"));
                                dataLaporan.setJenislap(JKObject.getString("jenislap"));

                                dataLaporanList.add(dataLaporan);
                            }
                            valueLaporan3 = new ArrayList<String>();
                            for (int i = 0; i < dataLaporanList.size(); i++) {
                                valueLaporan3.add("Pemasukan : "+dataLaporanList.get(i).lapor
                                        +"\nTotal : "+dataLaporanList.get(i).getTotl());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,valueLaporan3);
                            listView3.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }
    private void DialogEmpat(String isinya){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.laporan_empat,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Data Pengeluaran Lain-Lain");

        txt_empat = (TextView) dialogView.findViewById(R.id.txtTotalEmpat);
        listView4 = (ListView) dialogView.findViewById(R.id.lv4);
        dataLaporanList.clear();
        laporanEmpat(dua);
        txt_empat.setText(null);
        txt_empat.setText("Total Pengeluaran :\n"+isinya);

        listView4.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long id) {
                final String idsuplier = dataLaporanList.get(position).getIdlaporan();
                final CharSequence[] dialogitem = {"Edit","Delete"};
                dialog = new AlertDialog.Builder(getContext());
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                String jdulk = "Edit Laporan Pengeluaran";
                                editLaporan(jdulk,idsuplier);
                                break;
                            case 1:
                                deleteLaporan(idsuplier);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });

        dialog.setPositiveButton("Tambah Data", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String jud = "Tambah Laporan Pengeluaran";
                DialogTambahLaporan(jud,"","","","",dua);

            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void laporanEmpat(String jenis){
        dataLaporanList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+jenis,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray JKArray = obj.getJSONArray("result");

                            for (int i = 0; i < JKArray.length(); i++) {
                                JSONObject JKObject = JKArray.getJSONObject(i);

                                DataLaporan dataLaporan = new DataLaporan();
                                dataLaporan.setIdlaporan(JKObject.getString("idlap"));
                                dataLaporan.setIdlogin(JKObject.getString("idadm"));
                                dataLaporan.setLapor(JKObject.getString("namalaporan"));
                                dataLaporan.setTotl(JKObject.getString("total"));
                                dataLaporan.setJenislap(JKObject.getString("jenislap"));

                                dataLaporanList.add(dataLaporan);
                            }
                            valueLaporan4 = new ArrayList<String>();
                            for (int i = 0; i < dataLaporanList.size(); i++) {
                                valueLaporan4.add("Pengeluaran : "+dataLaporanList.get(i).lapor
                                        +"\nTotal : "+dataLaporanList.get(i).getTotl());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,valueLaporan4);
                            listView4.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void kosong(){
        edit_idLap.setText(null);
        edit_login.setText(null);
        edit_lap.setText(null);
        edit_tot.setText(null);
        edit_jen.setText(null);
    }

    private void DialogTambahLaporan(String judull, final String idLap, String idAdm, String lap, String tot, String jen){
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.laporan_edit,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle(judull);

        edit_idLap = (EditText) dialogView.findViewById(R.id.edit_IdLaporan);
        edit_login = (EditText) dialogView.findViewById(R.id.edit_IdAdm);
        edit_lap = (EditText) dialogView.findViewById(R.id.editLaporan);
        edit_tot = (EditText) dialogView.findViewById(R.id.edit_total_lap);
        edit_jen = (EditText) dialogView.findViewById(R.id.edit_jenis_lap);

        if(!idLap.isEmpty()){
            edit_idLap.setText(idLap);
            edit_login.setText(id);
            edit_lap.setText(lap);
            edit_tot.setText(tot);
            edit_jen.setText(jen);
        }else {
            kosong();
            edit_login.setText(id);
            edit_jen.setText(jen);
        }

        dialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                idLApor = edit_idLap.getText().toString();
                idlogin = edit_login.getText().toString();
                lapor = edit_lap.getText().toString();
                tottal = edit_tot.getText().toString();
                jennis = edit_jen.getText().toString();
                simpan_update();
                dialog.dismiss();

            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void simpan_update(){
        String url;

        if(idLApor.isEmpty()){
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
                        txt_penjualan.setText(null);
                        txt_beli.setText(null);
                        txt_total.setText(null);
                        txt_masuk.setText(null);
                        txt_keluar.setText(null);
                        dataListTransaksiList.clear();
                        dataLaporanList.clear();
                        dataProdukList.clear();
                        loadDataPenjualan();
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
                if (idLApor.isEmpty()){
                    params.put("idlogin", idlogin);
                    params.put("lapor", lapor);
                    params.put("total", tottal);
                    params.put("jenis", jennis);
                } else {
                    params.put("id", idLApor);
                    params.put("idlogin", idlogin);
                    params.put("lapor", lapor);
                    params.put("total", tottal);
                    params.put("jenis", jennis);
                }
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    private void editLaporan(final String jdulk, final String idsuplier){
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
                        String idl = jObj.getString("idlap");
                        String ida = jObj.getString("idadm");
                        String lpr = jObj.getString("namalaporan");
                        String t = jObj.getString("total");
                        String j = jObj.getString("jenislap");

                        DialogTambahLaporan(jdulk,idl,ida,lpr,t,j);

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

    private void deleteLaporan(final String idsuplier){
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
                        txt_penjualan.setText(null);
                        txt_beli.setText(null);
                        txt_total.setText(null);
                        txt_masuk.setText(null);
                        txt_keluar.setText(null);
                        dataListTransaksiList.clear();
                        dataLaporanList.clear();
                        dataProdukList.clear();
                        loadDataPenjualan();
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
                params.put("id", idsuplier);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}