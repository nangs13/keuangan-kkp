package com.kkp.keuangan.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelMutasiKas;

public class MutasiKasDAO {

    public ModelMutasiKas insert(ModelMutasiKas mutasi) {
        String newCode = generateNextKodeMutasi();

        String sql = "INSERT INTO mutasi_kas (code, tanggal, sumber_code, tujuan_code, jumlah, keterangan) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, newCode);
            ps.setString(2, mutasi.getTanggal());
            ps.setString(3, mutasi.getSumberCode());
            ps.setString(4, mutasi.getTujuanCode());
            ps.setDouble(5, mutasi.getJumlah());
            ps.setString(6, mutasi.getKeterangan());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Insert mutasi kas gagal, tidak ada baris yang terpengaruh.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return findById(newId);
                } else {
                    throw new SQLException("Insert mutasi kas gagal, tidak mendapatkan ID baru.");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saat insert mutasi kas", e);
        }
    }

    public void update(ModelMutasiKas mutasi) {
        String sql = "UPDATE mutasi_kas SET tanggal = ?, sumber_code = ?, tujuan_code = ?, jumlah = ?, keterangan = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mutasi.getTanggal());
            ps.setString(2, mutasi.getSumberCode());
            ps.setString(3, mutasi.getTujuanCode());
            ps.setDouble(4, mutasi.getJumlah());
            ps.setString(5, mutasi.getKeterangan());
            ps.setInt(6, mutasi.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM mutasi_kas WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ModelMutasiKas> findAll() {
        List<ModelMutasiKas> list = new ArrayList<>();
        String sql = "SELECT * FROM mutasi_kas ORDER BY tanggal DESC, id DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ModelMutasiKas(
                        rs.getInt("id"),
                        rs.getString("tanggal"),
                        rs.getString("sumber_code"),
                        rs.getString("tujuan_code"),
                        rs.getDouble("jumlah"),
                        rs.getString("keterangan"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ModelMutasiKas findById(int id) {
        String sql = "SELECT * FROM mutasi_kas WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ModelMutasiKas(
                            rs.getInt("id"),
                            rs.getString("tanggal"),
                            rs.getString("sumber_code"),
                            rs.getString("tujuan_code"),
                            rs.getDouble("jumlah"),
                            rs.getString("keterangan"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<ModelMutasiKas> getSummaryAll() {
        List<ModelMutasiKas> summaries = new ArrayList<>();
        String sql = """
            SELECT 
                *
            FROM mutasi_kas 
            GROUP BY code
            ORDER BY tanggal DESC, code DESC
            """;

        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ModelMutasiKas summary = new ModelMutasiKas();
                summary.setCode(rs.getString("code"));
                summary.setTanggal(rs.getString("tanggal"));
                summary.setSumberCode(rs.getString("sumber_code"));
                summary.setSumberCode(rs.getString("tujuan_code"));
                summary.setJumlah(rs.getDouble("jumlah"));
                summary.setKeterangan(rs.getString("keterangan"));
                summaries.add(summary);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saat mengambil summary semua mutasi kas", e);
        }

        return summaries;
    }

    public List<ModelMutasiKas> findByCode(String code) {
        List<ModelMutasiKas> details = new ArrayList<>();
        String sql = "SELECT * FROM mutasi_kas WHERE code = ? ORDER BY id ASC";

        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ModelMutasiKas mutasi = new ModelMutasiKas();
                    mutasi.setId(rs.getInt("id"));
                    mutasi.setCode(rs.getString("code"));
                    mutasi.setTanggal(rs.getString("tanggal"));
                    mutasi.setSumberCode(rs.getString("sumber_code"));
                    mutasi.setTujuanCode(rs.getString("tujuan_code"));
                    mutasi.setJumlah(rs.getDouble("jumlah"));
                    mutasi.setKeterangan(rs.getString("keterangan"));
                    mutasi.setCreatedAt(rs.getTimestamp("created_at"));
                    details.add(mutasi);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saat mencari detail mutasi kas dengan code: " + code, e);
        }

        return details;
    }

    private String generateNextKodeMutasi() {
        String sql = "SELECT code FROM mutasi_kas WHERE code LIKE 'MK-%' ORDER BY code DESC LIMIT 1";
    
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            if (rs.next()) {
                String lastCode = rs.getString("code");
                try {
                    int number = Integer.parseInt(lastCode.substring(3));
                    return String.format("MK-%03d", number + 1);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    return "MK-001";
                }
            } else {
                return "MK-001";
            }
    
        } catch (SQLException e) {
            throw new RuntimeException("Gagal generate kode mutasi kas", e);
        }
    }
}