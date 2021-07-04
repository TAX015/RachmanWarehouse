package com.rachman_warehouse.ui.produk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rachman_warehouse.R;

import java.util.List;

public class AdapterProduk extends ArrayAdapter<DataProduk> {
    private List<DataProduk> dataProdukList;
    private Context context;

    public AdapterProduk(List<DataProduk> dataProdukList, Context context){
        super(context, R.layout.produk_fragment,dataProdukList);
        this.dataProdukList = dataProdukList;
        this.context = context;
    }

    public int getCount(){ return  dataProdukList.size(); }
    @Override
    public DataProduk getItem(int location){ return dataProdukList.get(location); }
    @Override
    public long getItemId(int position){ return position; }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);

        View listViewItem = inflater.inflate(R.layout.produk_list,null,false);

        TextView ViewProduk = listViewItem.findViewById(R.id.txt_produk);
        TextView ViewStok = listViewItem.findViewById(R.id.txt_stok);

        final DataProduk dataProduk = dataProdukList.get(position);

        ViewProduk.setText(dataProduk.getBarang());
        ViewStok.setText("Stok : "+dataProduk.getStok());

        return listViewItem;
    }

}
