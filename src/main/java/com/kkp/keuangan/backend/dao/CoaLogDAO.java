package com.kkp.keuangan.backend.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelCoaLog;

public class CoaLogDAO {

    public void insert(ModelCoaLog coaLog) {
        String sql = "INSERT INTO coa_log (coa_id, tanggal, tipe, nominal, keterangan) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, coaLog.getCoaId());
            ps.setDate(2, Date.valueOf(coaLog.getTanggal()));
            ps.setString(3, coaLog.getTipe());
            ps.setDouble(4, coaLog.getNominal());
            ps.setString(5, coaLog.getKeterangan());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    coaLog.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ModelCoaLog> findByCoaIdAndDateRange(int coaId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        List<ModelCoaLog> list = new ArrayList<>();
        String sql = "SELECT * FROM coa_log WHERE coa_id = ? AND tanggal BETWEEN ? AND ? ORDER BY tanggal DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coaId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ModelCoaLog(
                            rs.getInt("id"),
                            rs.getInt("coa_id"),
                            rs.getDate("tanggal").toLocalDate(),
                            rs.getString("tipe"),
                            rs.getDouble("nominal"),
                            rs.getString("keterangan")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<ModelCoaLog> findAll() {
        List<ModelCoaLog> list = new ArrayList<>();
        String sql = "SELECT * FROM coa_log ORDER BY tanggal DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ModelCoaLog(
                        rs.getInt("id"),
                        rs.getInt("coa_id"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getString("tipe"),
                        rs.getDouble("nominal"),
                        rs.getString("keterangan")));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<ModelCoaLog> findByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        List<ModelCoaLog> list = new ArrayList<>();
        String sql = "SELECT * FROM coa_log WHERE tanggal BETWEEN ? AND ? ORDER BY tanggal DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ModelCoaLog(
                            rs.getInt("id"),
                            rs.getInt("coa_id"),
                            rs.getDate("tanggal").toLocalDate(),
                            rs.getString("tipe"),
                            rs.getDouble("nominal"),
                            rs.getString("keterangan")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
