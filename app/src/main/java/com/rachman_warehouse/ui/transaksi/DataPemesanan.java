package com.rachman_warehouse.ui.transaksi;

import android.os.Parcel;
import android.os.Parcelable;

public class DataPemesanan implements Parcelable {
    String idpenjualan, idadmin, pembeli, tgljual;

    public DataPemesanan(String idpenjualan, String idadmin, String pembeli, String tgljual){
        this.idpenjualan = idpenjualan;
        this.idadmin = idadmin;
        this.pembeli = pembeli;
        this.tgljual = tgljual;
    }
    public DataPemesanan(){

    }

    protected DataPemesanan(Parcel in) {
        idpenjualan = in.readString();
        idadmin = in.readString();
        pembeli = in.readString();
        tgljual = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idpenjualan);
        dest.writeString(idadmin);
        dest.writeString(pembeli);
        dest.writeString(tgljual);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataPemesanan> CREATOR = new Creator<DataPemesanan>() {
        @Override
        public DataPemesanan createFromParcel(Parcel in) {
            return new DataPemesanan(in);
        }

        @Override
        public DataPemesanan[] newArray(int size) {
            return new DataPemesanan[size];
        }
    };

    public String getIdpenjualan() {
        return idpenjualan;
    }

    public String getIdadmin() {
        return idadmin;
    }

    public String getPembeli() {
        return pembeli;
    }

    public String getTgljual() {
        return tgljual;
    }

    public void setIdpenjualan(String idpenjualan) {
        this.idpenjualan = idpenjualan;
    }

    public void setIdadmin(String idadmin) {
        this.idadmin = idadmin;
    }

    public void setPembeli(String pembeli) {
        this.pembeli = pembeli;
    }

    public void setTgljual(String tgljual) {
        this.tgljual = tgljual;
    }
}
