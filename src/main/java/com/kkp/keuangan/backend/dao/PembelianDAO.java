package com.kkp.keuangan.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelPembelian;
import com.kkp.keuangan.backend.model.ModelPembelianDetail;

public class PembelianDAO {

    // insert header (returns generated id)
    public int insert(ModelPembelian pembelian) throws SQLException {
        String sql = "INSERT INTO pembelian (tanggal_pembelian, supplier_id, coa_id, remark) "
                + "VALUES (?,?,?,?)";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            ps.setString(1, pembelian.getTanggalPembelian());
            ps.setInt(2, pembelian.getSupplierId());
            ps.setInt(3, pembelian.getCoaId());
            ps.setString(4, pembelian.getRemark());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int gen = -1;
            if (rs.next()) {
                gen = rs.getInt(1);
            }

            conn.commit();
            return gen;

        } catch (SQLException ex) {
            throw ex;
        }
    }

    // DELETE
    public boolean delete(int id) throws SQLException {
        if (id <= 0)
            throw new SQLException("ID belum terisi!");

        String sql = "DELETE FROM pembelian WHERE id=?";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            ps.setInt(1, id);
            boolean deleted = ps.executeUpdate() > 0;

            conn.commit();
            return deleted;

        } catch (SQLException ex) {
            throw ex;
        }
    }

    // insert
    public int insert(ModelPembelianDetail pembelianDetail) throws SQLException {
        String sql = "INSERT INTO pembelian_detail (pembelian_id, produk_id, qty, harga_satuan, total_harga)"
                + " VALUES (?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            ps.setInt(1, pembelianDetail.getPembelianId());
            ps.setString(2, pembelianDetail.getProdukId() + "");
            ps.setDouble(3, pembelianDetail.getQty());

            ps.setDouble(4, pembelianDetail.getHargaUnit());
            ps.setDouble(5, pembelianDetail.getTotal());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int gen = -1;
            if (rs.next())
                gen = rs.getInt(1);
            return gen;
        }
    }

    public boolean delete(int id, ModelPembelian pembelian) throws SQLException {
        if (id <= 0)
            throw new SQLException("ID belum terisi!");
        String sql = "DELETE FROM pembelian_detail WHERE id=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static List<ModelPembelianDetail> findByPembelianId(int pembelianId) throws SQLException {
        List<ModelPembelianDetail> list = new ArrayList<>();
        String sql = "SELECT id, pembelian_id, nama_barang, qty, satuan, harga_unit, total "
                + "FROM pembelian_detail WHERE pembelian_id=?";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pembelianId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ModelPembelianDetail(
                        rs.getInt("id"),
                        rs.getInt("pembelian_id"),
                        rs.getInt("produk_id"),
                        rs.getDouble("qty"),
                        rs.getString("satuan"),
                        rs.getDouble("harga_unit"),
                        rs.getDouble("total")));
            }
        }
        return list;
    }
}