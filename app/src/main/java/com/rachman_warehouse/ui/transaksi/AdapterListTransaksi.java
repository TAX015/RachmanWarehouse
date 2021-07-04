package com.rachman_warehouse.ui.transaksi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rachman_warehouse.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterListTransaksi extends ArrayAdapter<DataListTransaksi> {
    private List<DataListTransaksi> dataListTransaksiList;
    private Context context;

    public  AdapterListTransaksi(List<DataListTransaksi> dataListTransaksiList, Context context){
        super(context, R.layout.activity_list_transaksi,dataListTransaksiList);
        this.dataListTransaksiList = dataListTransaksiList;
        this.context = context;
    }

    public int getCount(){ return  dataListTransaksiList.size(); }
    @Override
    public DataListTransaksi getItem(int location){ return dataListTransaksiList.get(location); }
    @Override
    public long getItemId(int position){ return position; }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);

        View listViewItem = inflater.inflate(R.layout.list_transaksi,null,false);

        TextView ViewProduk = listViewItem.findViewById(R.id.txtProduk);
        TextView ViewJumlah = listViewItem.findViewById(R.id.txtJumlah);
        TextView ViewHarga = listViewItem.findViewById(R.id.txtHarga);

        final DataListTransaksi dataListTransaksi = dataListTransaksiList.get(position);

        ViewProduk.setText(dataListTransaksi.getProduk());
        ViewJumlah.setText(dataListTransaksi.getJumlah());
        double harga;
        harga = Double.parseDouble(dataListTransaksi.getHarga());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        ViewHarga.setText(formatRupiah.format(harga));

        return listViewItem;
    }
}
