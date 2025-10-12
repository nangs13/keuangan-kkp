package com.kkp.keuangan.backend.model;

public class ModelPenjualanDetail {
    private int id;
    private int penjualanId;
    private int produkId;
    private int qty;
    private double hargaSatuan;

    public ModelPenjualanDetail() {}

    public ModelPenjualanDetail(int produkId, int qty, double hargaSatuan) {
        this.produkId = produkId;
        this.qty = qty;
        this.hargaSatuan = hargaSatuan;
    }

    // Getter Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPenjualanId() { return penjualanId; }
    public void setPenjualanId(int penjualanId) { this.penjualanId = penjualanId; }

    public int getProdukId() { return produkId; }
    public void setProdukId(int produkId) { this.produkId = produkId; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(double hargaSatuan) { this.hargaSatuan = hargaSatuan; }
}
