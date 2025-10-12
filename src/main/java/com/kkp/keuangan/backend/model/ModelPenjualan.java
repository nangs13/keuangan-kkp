// com.kkp.backend.model.Penjualan.java
package com.kkp.keuangan.backend.model;

import java.util.List;

public class ModelPenjualan {
    private int id;
    private String tanggal;
    private double totalHarga;
    private List<ModelPenjualanDetail> detailList;

    public ModelPenjualan() {}

    public ModelPenjualan(int id, String tanggal, double totalHarga) {
        this.id = id;
        this.tanggal = tanggal;
        this.totalHarga = totalHarga;
    }

    // Getter Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }

    public List<ModelPenjualanDetail> getDetailList() { return detailList; }
    public void setDetailList(List<ModelPenjualanDetail> detailList) { this.detailList = detailList; }
}
