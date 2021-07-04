package com.rachman_warehouse.ui.laporan;

import android.os.Parcel;
import android.os.Parcelable;

public class DataLaporan implements Parcelable
{
    String idlaporan,idlogin,lapor,totl,jenislap;

    public DataLaporan(String idlaporan,String idlogin,String lapor,String totl,String jenislap){
        this.idlaporan = idlaporan;
        this.idlogin = idlogin;
        this.lapor = lapor;
        this.totl = totl;
        this.jenislap = jenislap;
    }
    public DataLaporan(){

    }

    protected DataLaporan(Parcel in) {
        idlaporan = in.readString();
        idlogin = in.readString();
        lapor = in.readString();
        totl = in.readString();
        jenislap = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idlaporan);
        dest.writeString(idlogin);
        dest.writeString(lapor);
        dest.writeString(totl);
        dest.writeString(jenislap);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataLaporan> CREATOR = new Creator<DataLaporan>() {
        @Override
        public DataLaporan createFromParcel(Parcel in) {
            return new DataLaporan(in);
        }

        @Override
        public DataLaporan[] newArray(int size) {
            return new DataLaporan[size];
        }
    };

    public String getIdlaporan() {
        return idlaporan;
    }

    public String getIdlogin() {
        return idlogin;
    }

    public String getLapor() {
        return lapor;
    }

    public String getTotl() {
        return totl;
    }

    public String getJenislap() {
        return jenislap;
    }

    public void setIdlaporan(String idlaporan) {
        this.idlaporan = idlaporan;
    }

    public void setIdlogin(String idlogin) {
        this.idlogin = idlogin;
    }

    public void setLapor(String lapor) {
        this.lapor = lapor;
    }

    public void setTotl(String totl) {
        this.totl = totl;
    }

    public void setJenislap(String jenislap) {
        this.jenislap = jenislap;
    }
}
