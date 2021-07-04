package com.rachman_warehouse.ui.suplier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rachman_warehouse.R;

import java.util.List;

public class AdapterSuplier extends ArrayAdapter<DataSuplier> {
    private List<DataSuplier> dataSuplierList;
    private Context context;

    public AdapterSuplier(List<DataSuplier> dataSuplierList, Context context){
        super(context, R.layout.suplier_fragment,dataSuplierList);
        this.dataSuplierList = dataSuplierList;
        this.context = context;
    }

    @Override
    public int getCount(){ return dataSuplierList.size(); }
    @Override
    public DataSuplier getItem(int location){ return dataSuplierList.get(location); }
    @Override
    public long getItemId(int position){ return position; }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);

        View listViewItem = inflater.inflate(R.layout.suplier_list,null,false);

        TextView ViewSuplier = listViewItem.findViewById(R.id.txtSuplier);
        TextView ViewSales = listViewItem.findViewById(R.id.txtSales);
        TextView ViewKontak = listViewItem.findViewById(R.id.txtKontak);

        final DataSuplier dataSuplier = dataSuplierList.get(position);

        ViewSuplier.setText("Perusahaan : "+dataSuplier.getPerusahaan());
        ViewSales.setText("Sales : "+dataSuplier.getSales());
        ViewKontak.setText("Kontak : "+dataSuplier.getNo());

        return listViewItem;
    }

}
