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
    public static boolean deleteById(int id) {
    String sql = "DELETE FROM pembelian_detail WHERE id = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        int affected = ps.executeUpdate();
        return affected > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    // insert
    public int insert() throws SQLException {
        String sql = "INSERT INTO pembelian_detail (pembelian_id, produk_id, qty, harga_satuan, total_harga)"
                + " VALUES (?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);
            ps.setInt(1, pembelianId);
            ps.setInt(2, produkId);
            ps.setDouble(3, qty);

            ps.setDouble(4, hargaUnit);
            ps.setDouble(5, total);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int gen = -1;
            if (rs.next())
                gen = rs.getInt(1);
            this.id = gen;
            return gen;
            }
    }
}