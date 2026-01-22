package com.kkp.keuangan.backend.model;

import java.util.Date;

public class ModelBiaya {
    private int id;
    private String code;
    private String tanggal;
    private String sumberCode;
    private String tujuanCode;
    private double jumlah;
    private String keterangan;
    private Date createdAt;

    // Constructor kosong
    public ModelBiaya() {
    }

    // Constructor lengkap (termasuk id dan code)
    public ModelBiaya(int id, String code, String tanggal, String sumberCode, String tujuanCode,
                      double jumlah, String keterangan, Date createdAt) {
        this.id = id;
        this.code = code;
        this.tanggal = tanggal;
        this.sumberCode = sumberCode;
        this.tujuanCode = tujuanCode;
        this.jumlah = jumlah;
        this.keterangan = keterangan;
        this.createdAt = createdAt;
    }

    // Constructor tanpa id dan code (untuk insert baru)
    public ModelBiaya(String tanggal, String sumberCode, String tujuanCode,
                      double jumlah, String keterangan) {
        this(0, null, tanggal, sumberCode, tujuanCode, jumlah, keterangan, null);
    }

    // Getter
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getTanggal() { return tanggal; }
    public String getSumberCode() { return sumberCode; }
    public String getTujuanCode() { return tujuanCode; }
    public double getJumlah() { return jumlah; }
    public String getKeterangan() { return keterangan; }
    public Date getCreatedAt() { return createdAt; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public void setSumberCode(String sumberCode) { this.sumberCode = sumberCode; }
    public void setTujuanCode(String tujuanCode) { this.tujuanCode = tujuanCode; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}