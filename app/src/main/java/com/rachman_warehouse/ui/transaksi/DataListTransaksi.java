package com.rachman_warehouse.ui.transaksi;

import android.os.Parcel;
import android.os.Parcelable;

public class DataListTransaksi implements Parcelable {
    String idpenjualan,iddata,produk,hargajual,jumlah,harga;

    public DataListTransaksi(String idpenjualan,String iddata,String produk,String hargajual, String jumlah,String harga){
        this.idpenjualan = idpenjualan;
        this.iddata = iddata;
        this.produk = produk;
        this.hargajual = hargajual;
        this.jumlah = jumlah;
        this.harga = harga;
    }
    public DataListTransaksi(){

    }

    protected DataListTransaksi(Parcel in) {
        idpenjualan = in.readString();
        iddata = in.readString();
        produk = in.readString();
        hargajual = in.readString();
        jumlah = in.readString();
        harga = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idpenjualan);
        dest.writeString(iddata);
        dest.writeString(produk);
        dest.writeString(hargajual);
        dest.writeString(jumlah);
        dest.writeString(harga);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataListTransaksi> CREATOR = new Creator<DataListTransaksi>() {
        @Override
        public DataListTransaksi createFromParcel(Parcel in) {
            return new DataListTransaksi(in);
        }

        @Override
        public DataListTransaksi[] newArray(int size) {
            return new DataListTransaksi[size];
        }
    };

    public String getIdpenjualan() {
        return idpenjualan;
    }

    public String getIddata() {
        return iddata;
    }

    public String getProduk() {
        return produk;
    }

    public String getHargajual() {
        return hargajual;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getHarga() {
        return harga;
    }

    public void setIdpenjualan(String idpenjualan) {
        this.idpenjualan = idpenjualan;
    }

    public void setIddata(String iddata) {
        this.iddata = iddata;
    }

    public void setProduk(String produk) {
        this.produk = produk;
    }

    public void setHargajual(String hargajual) {
        this.hargajual = hargajual;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }
}
