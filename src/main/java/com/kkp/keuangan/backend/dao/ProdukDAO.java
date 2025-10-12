package com.kkp.keuangan.backend.dao;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelProduk;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukDAO {

    public void insert(ModelProduk produk) {
        String sql = "INSERT INTO produk (nama, harga, stok, kategori) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, produk.getNama());
            ps.setDouble(2, produk.getHarga());
            ps.setInt(3, produk.getStok());
            ps.setString(4, produk.getKategori());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ModelProduk produk) {
        String sql = "UPDATE produk SET nama = ?, harga = ?, stok = ?, kategori = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, produk.getNama());
            ps.setDouble(2, produk.getHarga());
            ps.setInt(3, produk.getStok());
            ps.setString(4, produk.getKategori());
            ps.setInt(5, produk.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM produk WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ModelProduk> findAll() {
        List<ModelProduk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ModelProduk(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getDouble("harga"),
                        rs.getInt("stok"),
                        rs.getString("kategori")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ModelProduk findById(int id) {
        String sql = "SELECT * FROM produk WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ModelProduk(
                            rs.getInt("id"),
                            rs.getString("nama"),
                            rs.getDouble("harga"),
                            rs.getInt("stok"),
                            rs.getString("kategori")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
