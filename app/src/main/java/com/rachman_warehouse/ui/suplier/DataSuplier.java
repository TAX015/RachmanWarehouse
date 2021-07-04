package com.rachman_warehouse.ui.suplier;

import android.os.Parcel;
import android.os.Parcelable;

public class DataSuplier implements Parcelable {
    String idsuplier,perusahaan,sales,no,jenisP;

    public DataSuplier(String idsuplier,String perusahaan,String sales,String no,String jenisP){
        this.idsuplier = idsuplier;
        this.perusahaan = perusahaan;
        this.sales = sales;
        this.no = no;
        this.jenisP = jenisP;
    }

    protected DataSuplier(Parcel in) {
        idsuplier = in.readString();
        perusahaan = in.readString();
        sales = in.readString();
        no = in.readString();
        jenisP = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idsuplier);
        dest.writeString(perusahaan);
        dest.writeString(sales);
        dest.writeString(no);
        dest.writeString(jenisP);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataSuplier> CREATOR = new Creator<DataSuplier>() {
        @Override
        public DataSuplier createFromParcel(Parcel in) {
            return new DataSuplier(in);
        }

        @Override
        public DataSuplier[] newArray(int size) {
            return new DataSuplier[size];
        }
    };

    public DataSuplier(){

    }

    public String getIdsuplier() {
        return idsuplier;
    }

    public String getPerusahaan() {
        return perusahaan;
    }

    public String getSales() {
        return sales;
    }

    public String getNo() {
        return no;
    }

    public String getJenisP() {
        return jenisP;
    }

    public void setIdsuplier(String idsuplier) {
        this.idsuplier = idsuplier;
    }

    public void setPerusahaan(String perusahaan) {
        this.perusahaan = perusahaan;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setJenisP(String jenisP) {
        this.jenisP = jenisP;
    }
}
