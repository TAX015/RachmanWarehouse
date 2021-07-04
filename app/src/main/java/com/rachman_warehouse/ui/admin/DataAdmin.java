package com.rachman_warehouse.ui.admin;

import android.os.Parcel;
import android.os.Parcelable;

public class DataAdmin implements Parcelable {
    String idlogin,nama,usernama,pass,status;

    public DataAdmin(String idlogin, String nama, String usernama, String pass, String status){
        this.idlogin = idlogin;
        this.nama = nama;
        this.usernama = usernama;
        this.pass = pass;
        this.status = status;
    }

    protected DataAdmin(Parcel in) {
        this.idlogin = in.readString();
        this.nama = in.readString();
        this.usernama = in.readString();
        this.pass = in.readString();
        this.status = in.readString();
    }

    public static final Parcelable.Creator<DataAdmin> CREATOR = new Parcelable.Creator<DataAdmin>() {
        @Override
        public DataAdmin createFromParcel(Parcel source) {
            return new DataAdmin(source);
        }

        @Override
        public DataAdmin[] newArray(int size) {
            return new DataAdmin[size];
        }
    };

    public DataAdmin() {

    }

    public String getIdlogin() {
        return idlogin;
    }

    public String getNama() {
        return nama;
    }

    public String getUsernama() {
        return usernama;
    }

    public String getPass() {
        return pass;
    }

    public String getStatus() {
        return status;
    }

    public void setIdlogin(String idlogin) {
        this.idlogin = idlogin;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setUsernama(String usernama) {
        this.usernama = usernama;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idlogin);
        parcel.writeString(nama);
        parcel.writeString(usernama);
        parcel.writeString(pass);
        parcel.writeString(status);
    }

}
