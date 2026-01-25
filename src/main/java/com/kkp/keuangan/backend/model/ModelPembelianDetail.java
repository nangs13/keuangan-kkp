package com.kkp.keuangan.backend.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kkp.keuangan.backend.Database;

public class ModelPembelianDetail {
    private int id;
    private int pembelianId;
    private int produkId;
    private double qty;
    private String satuan;
    private double hargaUnit;
    private double total;
    
    public ModelPembelianDetail() {}

    public ModelPembelianDetail(int id, int pembelianId, int produkId, double qty, String satuan, double hargaUnit,
            double total) {
        this.id = id;
        this.pembelianId = pembelianId;
        this.produkId = produkId;
        this.qty = qty;
        this.satuan = satuan;
        this.hargaUnit = hargaUnit;
        this.total = total;
    }

    // getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPembelianId() {
        return pembelianId;
    }

    public void setPembelianId(int pembelianId) {
        this.pembelianId = pembelianId;
    }

    public int  getProdukId() {
        return produkId;
    }

    public void setProdukId(int produkId) {
        this.produkId = produkId;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public double getHargaUnit() {
        return hargaUnit;
    }

    public void setHargaUnit(double hargaUnit) {
        this.hargaUnit = hargaUnit;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}