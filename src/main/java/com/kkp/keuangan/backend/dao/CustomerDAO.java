package com.kkp.keuangan.backend.dao;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelCustomer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void insert(ModelCustomer customer) {
        String sql = "INSERT INTO customer (nama, hutang) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getNama());
            ps.setDouble(2, customer.getHutang());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ModelCustomer customer) {
        String sql = "UPDATE customer SET nama = ?, hutang = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getNama());
            ps.setDouble(2, customer.getHutang());
            ps.setInt(3, customer.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ModelCustomer> findAll() {
        List<ModelCustomer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ModelCustomer(
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

    public ModelCustomer findById(int id) {
        String sql = "SELECT * FROM customer WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ModelCustomer(
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
        * @param id  id Customer yang diupdate
        * @param nominal nilai yang ingin ditambahkan (bisa positif/negatif)
     */
    public void addHutang(int id, double nominal) {
        String sql = "UPDATE customer SET hutang = hutang + ? WHERE id = ?";
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
