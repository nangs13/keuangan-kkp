// com.kkp.backend.model.Penjualan.java
package com.kkp.keuangan.backend.model;

import java.util.List;

public class ModelPenjualan {
    private int id;
    private String tanggal;
    private double totalHarga;
    private int customerId;
    private int  coaId;
    private List<ModelPenjualanDetail> detailList;

    public ModelPenjualan() {}

    public ModelPenjualan(int id, String tanggal, double totalHarga, int customerId, int coaId) {
        this.id = id;
        this.tanggal = tanggal;
        this.totalHarga = totalHarga;
        this.customerId = customerId;
        this.coaId = coaId;
    }

    // Getter Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getCoaId() { return coaId; }
    public void setCoaId(int coaId) { this.coaId = coaId; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }

    public List<ModelPenjualanDetail> getDetailList() { return detailList; }
    public void setDetailList(List<ModelPenjualanDetail> detailList) { this.detailList = detailList; }
}
