package com.rachman_warehouse.ui.produk;

import android.os.Parcel;
import android.os.Parcelable;

public class DataProduk implements Parcelable {
    String idbarang,idsuplier,namasuplier,idlogin,namaadmin,barang,stok,hargabeli,hargajual;

    public DataProduk(String idbarang, String idsuplier,String namasuplier, String idjenis
            ,String jenisproduk, String idlogin, String namaadmin, String baranag, String stok, String hargabeli, String hargajual){
        this.idbarang = idbarang;
        this.idsuplier = idsuplier;
        this.namasuplier = namasuplier;
        this.idlogin = idlogin;
        this.namaadmin =namaadmin;
        this.barang = baranag;
        this.stok = stok;
        this.hargabeli = hargabeli;
        this.hargajual = hargajual;
    }

    public DataProduk(){

    }

    protected DataProduk(Parcel in) {
        idbarang = in.readString();
        idsuplier = in.readString();
        namasuplier = in.readString();
        idlogin = in.readString();
        namaadmin = in.readString();
        barang = in.readString();
        stok = in.readString();
        hargabeli = in.readString();
        hargajual = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idbarang);
        dest.writeString(idsuplier);
        dest.writeString(namasuplier);
        dest.writeString(idlogin);
        dest.writeString(namaadmin);
        dest.writeString(barang);
        dest.writeString(stok);
        dest.writeString(hargabeli);
        dest.writeString(hargajual);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataProduk> CREATOR = new Creator<DataProduk>() {
        @Override
        public DataProduk createFromParcel(Parcel in) {
            return new DataProduk(in);
        }

        @Override
        public DataProduk[] newArray(int size) {
            return new DataProduk[size];
        }
    };

    public String getIdbarang() {
        return idbarang;
    }

    public String getIdsuplier() {
        return idsuplier;
    }

    public String getNamasuplier() {
        return namasuplier;
    }

    public String getIdlogin() {
        return idlogin;
    }

    public String getNamaadmin() {
        return namaadmin;
    }

    public String getBarang() {
        return barang;
    }

    public String getStok() {
        return stok;
    }

    public String getHargabeli() {
        return hargabeli;
    }

    public String getHargajual() {
        return hargajual;
    }

    public void setIdbaranag(String idbarang) {
        this.idbarang = idbarang;
    }

    public void setIdsuplier(String idsuplier) {
        this.idsuplier = idsuplier;
    }

    public void setNamasuplier(String namasuplier) {
        this.namasuplier = namasuplier;
    }

    public void setIdlogin(String idlogin) {
        this.idlogin = idlogin;
    }

    public void setNamaadmin(String namaadmin) {
        this.namaadmin = namaadmin;
    }

    public void setBarang(String barang) {
        this.barang = barang;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public void setHargabeli(String hargabeli) {
        this.hargabeli = hargabeli;
    }

    public void setHargajual(String hargajual) {
        this.hargajual = hargajual;
    }
}
