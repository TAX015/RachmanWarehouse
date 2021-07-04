package com.rachman_warehouse.ui.transaksi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rachman_warehouse.R;

import java.util.List;

public class AdapterPemesanan extends ArrayAdapter<DataPemesanan> {
    private List<DataPemesanan> dataPemesananList;
    private Context context;

    public AdapterPemesanan(List<DataPemesanan> dataPemesananList, Context context){
        super(context, R.layout.pemesanan_fragment,dataPemesananList);
        this.dataPemesananList = dataPemesananList;
        this.context = context;
    }

    public int getCount(){ return  dataPemesananList.size(); }
    @Override
    public DataPemesanan getItem(int location){ return dataPemesananList.get(location); }
    @Override
    public long getItemId(int position){ return position; }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);

        View listViewItem = inflater.inflate(R.layout.pemesanan_list,null,false);

        TextView ViewPembeli = listViewItem.findViewById(R.id.txt_pembeli);
        TextView ViewTGL = listViewItem.findViewById(R.id.txt_tgl);

        final DataPemesanan dataPemesanan = dataPemesananList.get(position);

        ViewPembeli.setText(dataPemesanan.getPembeli());
        ViewTGL.setText("Tgl : "+dataPemesanan.getTgljual());

        return listViewItem;
    }
}
