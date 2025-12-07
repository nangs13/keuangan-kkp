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

    public List<ModelPembelian> findAll() {
        List<ModelPembelian> list = new ArrayList<>();
    
        String sql = "SELECT p.id, p.tanggal_pembelian, (SELECT SUM(pd.total_harga) FROM pembelian_detail pd WHERE pd.pembelian_id = p.id) AS total_harga, p.supplier_id, p.coa_id FROM pembelian AS p ORDER BY p.id DESC";
    
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
    
            while (rs.next()) {
                ModelPembelian p = new ModelPembelian();
                p.setId(rs.getInt("id"));
                p.setTanggalPembelian(rs.getString("tanggal_pembelian"));
                p.setTotal(rs.getDouble("total_harga"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setCoaId(rs.getInt("coa_id"));
    
                // isi detail
                p.setDetailList(findByPembelianId(p.getId()));
    
                list.add(p);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return list;
    }
     
    public ModelPembelian findById(int id) {
        String sqlPembelian = "SELECT p.id, p.tanggal_pembelian, (SELECT SUM(pd.total_harga) FROM pembelian_detail pd WHERE pd.pembelian_id = p.id) AS total_harga, p.supplier_id, p.coa_id FROM pembelian AS p WHERE p.id = ?";
        String sqlDetail = "SELECT id, pembelian_id, nama_barang, qty, satuan, harga_unit, total FROM pembelian_detail WHERE pembelian_id=?";

        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlPembelian)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ModelPembelian p = new ModelPembelian();
                p.setId(rs.getInt("id"));
                p.setTanggalPembelian(rs.getString("tanggal_pembelian"));
                p.setTotal(rs.getDouble("total_harga"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setCoaId(rs.getInt("coa_id"));

                // Ambil detail pembelian
                try (PreparedStatement psDet = conn.prepareStatement(sqlDetail)) {
                    psDet.setInt(1, id);
                    ResultSet rsDet = psDet.executeQuery();

                    List<ModelPembelianDetail> detailList = new ArrayList<>();

                    while (rsDet.next()) {
                        ModelPembelianDetail d = new ModelPembelianDetail();
                        d.setId(rsDet.getInt("id"));
                        d.setProdukId(rsDet.getInt("produk_id"));
                        d.setQty(rsDet.getInt("qty"));
                        d.setHargaUnit(rsDet.getDouble("harga_unit"));
                        detailList.add(d);
                    }

                    p.setDetailList(detailList);
                }

                return p;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ModelPembelian> findPage(int limit, int offset) {
        List<ModelPembelian> list = new ArrayList<>();
        String sql = "SELECT p.*, (SELECT SUM(pd.total_harga) FROM pembelian_detail pd WHERE pd.pembelian_id = p.id) AS total_harga FROM pembelian AS p ORDER BY p.id DESC LIMIT ? OFFSET ?";
    
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                ModelPembelian p = new ModelPembelian();
                p.setId(rs.getInt("id"));
                p.setTanggalPembelian(rs.getString("tanggal_pembelian"));
                p.setTotal(rs.getDouble("total_harga"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setCoaId(rs.getInt("coa_id"));
                list.add(p);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll() {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM pembelian")) {
    
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<ModelPembelian> search(String keyword) {
        List<ModelPembelian> list = new ArrayList<>();
    
        String sql = """
            SELECT p.*,
            (SELECT SUM(pd.total_harga) FROM pembelian_detail pd WHERE pd.pembelian_id = p.id) AS total_harga
            FROM pembelian p
            JOIN supplier s ON s.id = p.supplier_id
            WHERE LOWER(s.name) LIKE ? OR p.id LIKE ?
            ORDER BY p.id DESC
        """;
    
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ps.setString(2, "%" + keyword.toLowerCase() + "%");
    
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ModelPembelian p = new ModelPembelian();
                p.setId(rs.getInt("id"));
                p.setTanggalPembelian(rs.getString("tanggal_pembelian"));
                p.setTotal(rs.getDouble("total_harga"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setCoaId(rs.getInt("coa_id"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ModelPembelianDetail> getDetailsByPembelianId(int idPembelian) {
        List<ModelPembelianDetail> list = new ArrayList<>();
    
        String sql = "SELECT * FROM pembelian_detail WHERE pembelian_id = ?";
    
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setInt(1, idPembelian);
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                ModelPembelianDetail d = new ModelPembelianDetail();
                d.setId(rs.getInt("id"));
                d.setProdukId(rs.getInt("produk_id"));
                d.setQty(rs.getInt("qty"));
                d.setHargaUnit(rs.getDouble("harga_unit"));
                list.add(d);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return list;
    }
}

