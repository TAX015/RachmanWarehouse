package com.rachman_warehouse.ui.admin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rachman_warehouse.R;

import java.util.List;

public class AdapterAdmin extends ArrayAdapter<DataAdmin> {
    private List<DataAdmin> dataAdminList;
    private Context context;

    public AdapterAdmin(List<DataAdmin> dataAdminList, Context context){
        super(context, R.layout.admin_fragment,dataAdminList);
        this.dataAdminList = dataAdminList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataAdminList.size();
    }

    @Override
    public DataAdmin getItem(int location) {
        return dataAdminList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);

        View listViewItem = inflater.inflate(R.layout.admin_list, null, false);

        TextView ViewNama = listViewItem.findViewById(R.id.txtAdminNama);

        final DataAdmin dataAdmin = dataAdminList.get(position);

        ViewNama.setText(dataAdmin.getNama());

        return  listViewItem;
    }
}
