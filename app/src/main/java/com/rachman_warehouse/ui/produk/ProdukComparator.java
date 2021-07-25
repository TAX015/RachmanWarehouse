package com.rachman_warehouse.ui.produk;

import com.rachman_warehouse.ui.produk.DataProduk;

import java.util.Comparator;

//Comparator untuk list produk
public class ProdukComparator implements Comparator<DataProduk> {

    @Override
    public int compare(DataProduk dp1, DataProduk dp2) {
        return dp1.getBarang().compareTo(dp2.getBarang());
    }
}
