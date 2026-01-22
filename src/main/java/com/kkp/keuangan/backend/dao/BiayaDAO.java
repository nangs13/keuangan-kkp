package com.kkp.keuangan.backend.dao;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelBiaya;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BiayaDAO {

    public ModelBiaya insert(ModelBiaya biaya) {
        String newCode = generateNextKodeBiaya();

        String sql = "INSERT INTO biaya (code, tanggal, sumber_code, tujuan_code, jumlah, keterangan) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newCode);
            ps.setString(2, biaya.getTanggal());
            ps.setString(3, biaya.getSumberCode());
            ps.setString(4, biaya.getTujuanCode());
            ps.setDouble(5, biaya.getJumlah());
            ps.setString(6, biaya.getKeterangan());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Insert biaya gagal, tidak ada baris yang terpengaruh.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return findById(newId);
                } else {
                    throw new SQLException("Insert biaya gagal, tidak mendapatkan ID baru.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saat insert biaya", e);
        }
    }

    public void update(ModelBiaya biaya) {
        String sql = "UPDATE biaya SET tanggal = ?, sumber_code = ?, tujuan_code = ?, jumlah = ?, keterangan = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, biaya.getTanggal());
            ps.setString(2, biaya.getSumberCode());
            ps.setString(3, biaya.getTujuanCode());
            ps.setDouble(4, biaya.getJumlah());
            ps.setString(5, biaya.getKeterangan());
            ps.setInt(6, biaya.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM biaya WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ModelBiaya> findAll() {
        List<ModelBiaya> list = new ArrayList<>();
        String sql = "SELECT * FROM biaya ORDER BY tanggal DESC, id DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ModelBiaya(
                        rs.getInt("id"),
                        rs.getString("code"),
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

    public ModelBiaya findById(int id) {
        String sql = "SELECT * FROM biaya WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ModelBiaya(
                            rs.getInt("id"),
                            rs.getString("code"),
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

    public List<ModelBiaya> getSummaryAll() {
        List<ModelBiaya> summaries = new ArrayList<>();
        String sql = """
            SELECT 
                *
            FROM biaya 
            GROUP BY code
            ORDER BY tanggal DESC, code DESC
            """;

        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ModelBiaya summary = new ModelBiaya();
                summary.setCode(rs.getString("code"));
                summary.setTanggal(rs.getString("tanggal"));
                summary.setSumberCode(rs.getString("sumber_code"));
                summary.setTujuanCode(rs.getString("tujuan_code"));
                summary.setJumlah(rs.getDouble("jumlah"));
                summary.setKeterangan(rs.getString("keterangan"));
                summaries.add(summary);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saat mengambil summary semua Biaya", e);
        }

        return summaries;
    }

    public List<ModelBiaya> findPageSummary(int limit, int offset) {
        List<ModelBiaya> summaries = new ArrayList<>();
        String sql = """
            SELECT 
                *
            FROM biaya 
            GROUP BY code
            ORDER BY tanggal DESC, code DESC
            LIMIT ?, ?
            """;

        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();


            while (rs.next()) {
                ModelBiaya summary = new ModelBiaya();
                summary.setCode(rs.getString("code"));
                summary.setTanggal(rs.getString("tanggal"));
                summary.setSumberCode(rs.getString("sumber_code"));
                summary.setTujuanCode(rs.getString("tujuan_code"));
                summary.setJumlah(rs.getDouble("jumlah"));
                summary.setKeterangan(rs.getString("keterangan"));
                summaries.add(summary);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saat mengambil summary semua Biaya", e);
        }

        return summaries;
    }

    public List<ModelBiaya> searchSummary(int limit, int offset, String search) {
        List<ModelBiaya> summaries = new ArrayList<>();
        String sql = """
            SELECT 
                *
            FROM biaya 
            WHERE
                code LIKE ? OR
                tanggal LIKE ? OR
                sumber_code LIKE ? OR
                tujuan_code LIKE ? OR
                keterangan LIKE ?
            GROUP BY code
            ORDER BY tanggal DESC, code DESC
            LIMIT ?, ?
            """;

        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            String searchPattern = "%" + search.trim() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);
            ps.setInt(6, offset);
            ps.setInt(7, limit);
            ResultSet rs = ps.executeQuery();


            while (rs.next()) {
                ModelBiaya summary = new ModelBiaya();
                summary.setCode(rs.getString("code"));
                summary.setTanggal(rs.getString("tanggal"));
                summary.setSumberCode(rs.getString("sumber_code"));
                summary.setTujuanCode(rs.getString("tujuan_code"));
                summary.setJumlah(rs.getDouble("jumlah"));
                summary.setKeterangan(rs.getString("keterangan"));
                summaries.add(summary);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saat mengambil summary semua Biaya", e);
        }

        return summaries;
    }

    public int countAllSummary() {
        String sql = "SELECT COUNT(DISTINCT code) FROM biaya";
        int count = 0;

        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saat mengambil summary semua Biaya", e);
        }

        return count;
    }

    public List<ModelBiaya> findByCode(String code) {
        List<ModelBiaya> details = new ArrayList<>();
        String sql = "SELECT * FROM biaya WHERE code = ? ORDER BY id ASC";

        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ModelBiaya mutasi = new ModelBiaya();
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
            throw new RuntimeException("Error saat mencari detail Biaya dengan code: " + code, e);
        }

        return details;
    }

    private String generateNextKodeBiaya() {
        String sql = "SELECT code FROM biaya WHERE code LIKE 'B-%' ORDER BY code DESC LIMIT 1";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastCode = rs.getString("code");
                try {
                    int number = Integer.parseInt(lastCode.substring(2));
                    return String.format("B-%03d", number + 1);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    return "B-001";
                }
            } else {
                return "B-001";
            }

        } catch (SQLException e) {
            throw new RuntimeException("Gagal generate kode biaya", e);
        }
    }
}