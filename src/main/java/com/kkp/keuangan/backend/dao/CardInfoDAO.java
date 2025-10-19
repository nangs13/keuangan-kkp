package com.kkp.keuangan.backend.dao;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelCardInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardInfoDAO {

    public void insert(ModelCardInfo cardInfo) {
        String sql = "INSERT INTO card_info (coa_id, nama) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cardInfo.getCoaId());
            ps.setString(2, cardInfo.getNama());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ModelCardInfo cardInfo) {
        String sql = "UPDATE card_info SET coa_id = ?, nama = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cardInfo.getCoaId());
            ps.setString(2, cardInfo.getNama());
            ps.setInt(3, cardInfo.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM card_info WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ModelCardInfo> findAll() {
        List<ModelCardInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM card_info ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ModelCardInfo(
                        rs.getInt("id"),
                        rs.getInt("coa_id"),
                        rs.getString("nama")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ModelCardInfo findById(int id) {
        String sql = "SELECT * FROM card_info WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ModelCardInfo(
                            rs.getInt("id"),
                            rs.getInt("coa_id"),
                            rs.getString("nama")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<ModelCardInfo> findByCoaId(int coaId) {
        List<ModelCardInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM card_info WHERE coa_id = ? ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ModelCardInfo(
                            rs.getInt("id"),
                            rs.getInt("coa_id"),
                            rs.getString("nama")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
