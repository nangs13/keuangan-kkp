package com.kkp.keuangan.backend.model;

import java.util.Random;

public class ModelPembelian {
    private int id;
    private String kode;
    private String tanggalPembelian;
    private String tanggalDeadline;
    private int supplierId;
    private int  coaId;
    private double total;
    private String poStatus;
    private String returStatus;
    private String remark;


    public ModelPembelian() {
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getTanggalPembelian() {
        return tanggalPembelian;
    }

    public void setTanggalPembelian(String tanggalPembelian) {
        this.tanggalPembelian = tanggalPembelian;
    }

    public String getTanggalDeadline() {
        return tanggalDeadline;
    }

    public void setTanggalDeadline(String tanggalDeadline) {
        this.tanggalDeadline = tanggalDeadline;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int  getCoaId() {
        return this.coaId;
    }

    public void setCoaId(int  coaId) {
        this.coaId= coaId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPoStatus() {
        return poStatus;
    }

    public void setPoStatus(String poStatus) {
        this.poStatus = poStatus;
    }

    public String getReturStatus() {
        return returStatus;
    }

    public void setReturStatus(String returStatus) {
        this.returStatus = returStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    // generate kode
    public static String generateKode() {
        String prefix = "PB-";
        int rnd = new Random().nextInt(9000) + 1000;
        long t = System.currentTimeMillis() % 100000;
        return prefix + rnd + "-" + t;
    }
}