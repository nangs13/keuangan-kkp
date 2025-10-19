package com.kkp.keuangan.backend.dao;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelCoa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoaDAO {

    public void insert(ModelCoa coa) {
        String sql = "INSERT INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coa.getCode());
            ps.setString(2, coa.getNama());
            ps.setString(3, coa.getPeriode());
            ps.setString(4, coa.getType());
            if (coa.getParentId() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, coa.getParentId());
            }
            ps.setDouble(6, coa.getBeginning());
            ps.setDouble(7, coa.getDebit());
            ps.setDouble(8, coa.getCredit());
            ps.setDouble(9, coa.getEnding());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    // ----------------------------
    // Generate kode otomatis
    // ----------------------------
    public String generateCode(String parentCode) {
        // contoh parentCode = "101-01"
        String sql = "SELECT code FROM coa WHERE code LIKE ? ORDER BY code DESC LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, parentCode + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String lastCode = rs.getString("code");
                    // ambil 3 digit terakhir
                    String lastDigits = lastCode.substring(lastCode.length() - 3);
                    int next = Integer.parseInt(lastDigits) + 1;
                    return parentCode + String.format("%03d", next);
                } else {
                    // jika belum ada anak sama sekali
                    return parentCode + "001";
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Generate periode sekarang (yyyymm)
    public String getCurrentPeriode() {
        java.time.LocalDate now = java.time.LocalDate.now();
        return String.format("%04d%02d", now.getYear(), now.getMonthValue());
    }

    // Tambah COA baru dengan generate code otomatis
    public void addChildCoa(String nama, String type, String parentCode, Integer parentId, double beginning) {
        String newCode = generateCode(parentCode);
        ModelCoa coa = new ModelCoa();
        coa.setCode(newCode);
        coa.setNama(nama);
        coa.setType(type);
        coa.setParentId(parentId);
        coa.setPeriode(getCurrentPeriode());
        coa.setBeginning(beginning);
        coa.setDebit(0);
        coa.setCredit(0);
        coa.setEnding(beginning);

        insert(coa);
    }

    public void update(ModelCoa coa) {
        ModelCoa coaCurrent = findById(coa.getId());
        String k = coa.getType().trim().toLowerCase();

        double beginning = coa.getBeginning();
        double debit = coaCurrent.getDebit();
        double credit = coaCurrent.getCredit();
        double ending = 0;

        switch (k) {
            case "debit":
                ending = beginning + debit - credit;
                break;
            case "credit":
                ending = beginning - debit + credit;
                break;
            default:
                throw new IllegalArgumentException("Jenis harus salah satu dari: beginning, debit, credit");
        }

        String sql = "UPDATE coa SET code = ?, nama = ?, type = ?, parent_id = ?, " +
                     "beginning = ?, ending = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coa.getCode());
            ps.setString(2, coa.getNama());
            ps.setString(3, coa.getType());
            if (coa.getParentId() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, coa.getParentId());
            }
            ps.setDouble(5, coa.getBeginning());
            ps.setDouble(6, ending);
            ps.setInt(7, coa.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM coa WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ModelCoa> findAll() {
        List<ModelCoa> list = new ArrayList<>();
        String sql = "SELECT * FROM coa ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Integer parentId = null;
                int pid = rs.getInt("parent_id");
                if (!rs.wasNull()) parentId = pid;

                list.add(new ModelCoa(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("nama"),
                        rs.getString("periode"),
                        rs.getString("type"),
                        parentId,
                        rs.getDouble("beginning"),
                        rs.getDouble("debit"),
                        rs.getDouble("credit"),
                        rs.getDouble("ending")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ModelCoa findById(int id) {
        String sql = "SELECT * FROM coa WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer parentId = null;
                    int pid = rs.getInt("parent_id");
                    if (!rs.wasNull()) parentId = pid;

                    return new ModelCoa(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("nama"),
                            rs.getString("periode"),
                            rs.getString("type"),
                            parentId,
                            rs.getDouble("beginning"),
                            rs.getDouble("debit"),
                            rs.getDouble("credit"),
                            rs.getDouble("ending")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    
    public ModelCoa findByCode(String code) {
        String sql = "SELECT * FROM coa WHERE code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer parentId = null;
                    int pid = rs.getInt("parent_id");
                    if (!rs.wasNull()) parentId = pid;

                    return new ModelCoa(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("nama"),
                            rs.getString("periode"),
                            rs.getString("type"),
                            parentId,
                            rs.getDouble("beginning"),
                            rs.getDouble("debit"),
                            rs.getDouble("credit"),
                            rs.getDouble("ending")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<ModelCoa> findByPeriode(String periode) {
        List<ModelCoa> list = new ArrayList<>();
        String sql = "SELECT * FROM coa WHERE periode = ? ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, periode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer parentId = null;
                    int pid = rs.getInt("parent_id");
                    if (!rs.wasNull()) parentId = pid;

                    list.add(new ModelCoa(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("nama"),
                            rs.getString("periode"),
                            rs.getString("type"),
                            parentId,
                            rs.getDouble("beginning"),
                            rs.getDouble("debit"),
                            rs.getDouble("credit"),
                            rs.getDouble("ending")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Update saldo: menambahkan nominal ke salah satu field (beginning/debit/credit)
     * lalu menghitung ulang ending berdasarkan type (DEBIT / CREDIT).
     *
     * @param coaId  id coa yang diupdate
     * @param jenis  salah satu "beginning", "debit", "credit"
     * @param nominal nilai yang ingin ditambahkan (bisa positif/negatif)
     */
    public void updateSaldo(int coaId, String jenis, double nominal) {
        // Ambil record sekarang
        ModelCoa coa = findById(coaId);
        if (coa == null) {
            throw new IllegalArgumentException("COA dengan id " + coaId + " tidak ditemukan.");
        }

        // Normalisasi jenis
        if (jenis == null) {
            throw new IllegalArgumentException("Jenis tidak boleh null.");
        }
        String k = jenis.trim().toLowerCase();

        double beginning = coa.getBeginning();
        double debit = coa.getDebit();
        double credit = coa.getCredit();

        switch (k) {
            case "beginning":
                beginning += nominal;
                break;
            case "debit":
                debit += nominal;
                break;
            case "credit":
                credit += nominal;
                break;
            default:
                throw new IllegalArgumentException("Jenis harus salah satu dari: beginning, debit, credit");
        }

        // Hitung ending sesuai type COA (T2)
        String type = coa.getType();
        double ending;
        if ("CREDIT".equalsIgnoreCase(type)) {
            ending = beginning - debit + credit;
        } else { // default treat as DEBIT
            ending = beginning + debit - credit;
        }

        // Update ke DB
        String sql = "UPDATE coa SET beginning = ?, debit = ?, credit = ?, ending = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, beginning);
            ps.setDouble(2, debit);
            ps.setDouble(3, credit);
            ps.setDouble(4, ending);
            ps.setInt(5, coaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
