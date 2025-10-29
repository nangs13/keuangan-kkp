package com.kkp.keuangan.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModelPembelianDetail {

    private int id;
    private int pembelianId;
    private String namaBarang;
    private double qty;
    private String satuan;
    private double hargaUnit;
    private double total;

    private static final String DB_URL = "jdbc:sqlite:pos_app.db";

    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public ModelPembelianDetail() {}

    public ModelPembelianDetail(
            int id, int pembelianId, String namaBarang,
            double qty, String satuan, double hargaUnit, double total
    ) {
        this.id = id;
        this.pembelianId = pembelianId;
        this.namaBarang = namaBarang;
        this.qty = qty;
        this.satuan = satuan;
        this.hargaUnit = hargaUnit;
        this.total = total;
    }

    // Getters & setters …
    // (sengaja tidak dihapus)

    // INSERT → return generated ID
    public int insert() throws SQLException {

        String sql =
        "INSERT INTO pembelian_detail ("
      + "pembelian_id, nama_barang, qty, satuan, harga_unit, total"
      + ") VALUES (?,?,?,?,?,?)";
        

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pembelianId);
            ps.setString(2, namaBarang);
            ps.setDouble(3, qty);
            ps.setString(4, satuan);
            ps.setDouble(5, hargaUnit);
            ps.setDouble(6, total);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) this.id = rs.getInt(1);
            return this.id;
        }
    }

    // DELETE
    public boolean delete() throws SQLException {
        if (id <= 0) throw new SQLException("ID belum terisi!");

        String sql = "DELETE FROM pembelian_detail WHERE id=?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static List<ModelPembelianDetail> findByPembelianId(int pembelianId) throws SQLException {
        List<ModelPembelianDetail> list = new ArrayList<>();

        String sql =
            "SELECT id, pembelian_id, nama_barang, qty, satuan, harga_unit, total "
          + "FROM pembelian_detail "
          + "WHERE pembelian_id=?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pembelianId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(
                        new ModelPembelianDetail(
                                rs.getInt("id"),
                                rs.getInt("pembelian_id"),
                                rs.getString("nama_barang"),
                                rs.getDouble("qty"),
                                rs.getString("satuan"),
                                rs.getDouble("harga_unit"),
                                rs.getDouble("total")
                        )
                );
            }
        }
        return list;
    }

    public void setPembelianId(int pembelianId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}