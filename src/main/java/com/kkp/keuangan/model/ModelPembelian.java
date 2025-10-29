package com.kkp.keuangan.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class ModelPembelian {

    private int id;
    private String kode;
    private String tanggalPembelian;
    private String tanggalDeadline;
    private int supplierId;
    private String metodePembayaran;
    private double total;
    private String poStatus;
    private String returStatus;

    private static final String DB_URL = "jdbc:sqlite:pos_app.db";

    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }
    public String getTanggalPembelian() { return tanggalPembelian; }
    public void setTanggalPembelian(String tanggalPembelian) { this.tanggalPembelian = tanggalPembelian; }
    public String getTanggalDeadline() { return tanggalDeadline; }
    public void setTanggalDeadline(String tanggalDeadline) { this.tanggalDeadline = tanggalDeadline; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getPoStatus() { return poStatus; }
    public void setPoStatus(String poStatus) { this.poStatus = poStatus; }
    public String getReturStatus() { return returStatus; }
    public void setReturStatus(String returStatus) { this.returStatus = returStatus; }

    // Generate kode
    public static String generateKode() {
        String prefix = "PB-";
        int rnd = new Random().nextInt(9000) + 1000;
        long timestamp = System.currentTimeMillis() % 100000;
        return prefix + rnd + "-" + timestamp;
    }

    // Insert → returns generated ID
    public int insert() throws SQLException {
         String sql =
        "INSERT INTO pembelian ("
      + "kode, tanggal_pembelian, tanggal_deadline, "
      + "supplier_id, metode_pembayaran, total, "
      + "po_status, retur_status"
      + ") VALUES (?,?,?,?,?,?,?,?)";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, kode);
            ps.setString(2, tanggalPembelian);
            ps.setString(3, tanggalDeadline);
            ps.setInt(4, supplierId);
            ps.setString(5, metodePembayaran);
            ps.setDouble(6, total);
            ps.setString(7, poStatus);
            ps.setString(8, returStatus);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
            return this.id;
        }
    }

    // Update
    public boolean update() throws SQLException {
        if (id <= 0) throw new SQLException("ID belum terisi!");

        String sql =
        "UPDATE pembelian SET "
      + "kode=?, tanggal_pembelian=?, tanggal_deadline=?, supplier_id=?, "
      + "metode_pembayaran=?, total=?, po_status=?, retur_status=? "
      + "WHERE id=?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kode);
            ps.setString(2, tanggalPembelian);
            ps.setString(3, tanggalDeadline);
            ps.setInt(4, supplierId);
            ps.setString(5, metodePembayaran);
            ps.setDouble(6, total);
            ps.setString(7, poStatus);
            ps.setString(8, returStatus);
            ps.setInt(9, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Delete
    public boolean delete() throws SQLException {
        if (id <= 0) throw new SQLException("ID belum terisi!");

        String sql = "DELETE FROM pembelian WHERE id=?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
