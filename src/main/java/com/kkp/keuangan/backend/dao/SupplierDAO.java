package com.kkp.keuangan.backend.dao;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelSupplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public void insert(ModelSupplier supplier) {
        String sql = "INSERT INTO supplier (nama, hutang) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getNama());
            ps.setDouble(2, supplier.getHutang());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ModelSupplier supplier) {
        String sql = "UPDATE supplier SET nama = ?, hutang = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getNama());
            ps.setDouble(2, supplier.getHutang());
            ps.setInt(3, supplier.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM supplier WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ModelSupplier> findAll() {
        List<ModelSupplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ModelSupplier(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getDouble("hutang")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ModelSupplier findById(int id) {
        String sql = "SELECT * FROM supplier WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ModelSupplier(
                            rs.getInt("id"),
                            rs.getString("nama"),
                            rs.getDouble("hutang")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /** Add hutang secara akumulatif 
        * @param id  id Supplier yang diupdate
        * @param nominal nilai yang ingin ditambahkan (bisa positif/negatif)
     */
    public void addHutang(int id, double nominal) {
        String sql = "UPDATE supplier SET hutang = hutang + ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nominal);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
