package com.rachman_warehouse.ui.transaksi;

import com.rachman_warehouse.ui.produk.DataProduk;
import com.rachman_warehouse.ui.transaksi.DataPemesanan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

//Comparator untuk list transaksi
public class TransaksiComparator implements Comparator<DataPemesanan> {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int compare(DataPemesanan dp1, DataPemesanan dp2) {
        try {
            return dateFormat.parse(dp1.getTgljual()).compareTo(dateFormat.parse(dp2.getTgljual()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}

