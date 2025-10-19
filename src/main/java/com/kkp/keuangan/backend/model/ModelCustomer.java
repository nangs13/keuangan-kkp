package com.kkp.keuangan.backend.model;

public class ModelCustomer {
    private int id;
    private String nama;
    private double hutang;

    public ModelCustomer() {
        // Constructor kosong
    }

    public ModelCustomer(int id, String nama, double hutang) {
        this.id = id;
        this.nama = nama;
        this.hutang = hutang;
    }

    public ModelCustomer(String nama, double hutang) {
        this(0, nama, hutang);
    }

    // Getter
    public int getId() { return id; }
    public String getNama() { return nama; }
    public double getHutang() { return hutang; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setHutang(double hutang) { this.hutang = hutang; }
}
