package com.rachman_warehouse.ui.suplier;

import java.util.Comparator;

public class SuplierComparator implements Comparator<DataSuplier> {
    @Override
    public int compare(DataSuplier ds1, DataSuplier ds2) {
        return ds1.getPerusahaan().compareTo(ds2.getPerusahaan());
    }
}
