package com.kkp.keuangan.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.backend.model.ModelPenjualan;
import com.kkp.keuangan.backend.model.ModelPenjualanDetail;
import com.kkp.keuangan.backend.model.ModelProduk;

public class PenjualanDAO {

    public int insert(ModelPenjualan penjualan) {
        String sqlPenjualan = "INSERT INTO penjualan (tanggal, total_harga, customer_id, coa_id) VALUES (?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO penjualan_detail (penjualan_id, produk_id, qty, harga_satuan) VALUES (?, ?, ?, ?)";
        String sqlUpdateProduk = "UPDATE produk SET stok = ? WHERE id = ?";

        try (Connection conn = Database.getConnection()) {

            try (PreparedStatement psPenjualan = conn.prepareStatement(sqlPenjualan, Statement.RETURN_GENERATED_KEYS)) {
                psPenjualan.setString(1, penjualan.getTanggal());
                psPenjualan.setDouble(2, penjualan.getTotalHarga());
                psPenjualan.setInt(3, penjualan.getCustomerId());
                psPenjualan.setInt(4, penjualan.getCoaId());
                psPenjualan.executeUpdate();

                ResultSet rs = psPenjualan.getGeneratedKeys();
                int penjualanId = 0;
                if (rs.next()) {
                    penjualanId = rs.getInt(1);
                }

                Double totalValue = 0.0;

                try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                    for (ModelPenjualanDetail d : penjualan.getDetailList()) {
                        psDetail.setInt(1, penjualanId);
                        psDetail.setInt(2, d.getProdukId());
                        psDetail.setInt(3, d.getQty());
                        psDetail.setDouble(4, d.getHargaSatuan());
                        psDetail.addBatch();

                        ProdukDAO dao = new ProdukDAO();
                        ModelProduk produk = dao.findById(d.getProdukId());
                        Double oldPrice = produk.getHarga();
                        int oldStok = produk.getStok();

                        totalValue += oldPrice * d.getQty();
                        int newStok = oldStok - (int) d.getQty();

                        try (PreparedStatement psProduk = conn.prepareStatement(sqlUpdateProduk)) {
                            psProduk.setDouble(1, newStok);
                            psProduk.setInt(2, d.getProdukId());

                            psProduk.execute();
                        }

                    }
                    psDetail.executeBatch();
                }

                // Update COA
                CoaDAO daoCOA = new CoaDAO();
                ModelCoa coaPersedian = daoCOA.findByCode("101-03001");
                ModelCoa coaHPP = daoCOA.findByCode("401-03");
                ModelCoa coaPenjualan = daoCOA.findByCode("401-01");
                ModelCoa coaPiutang = daoCOA.findByCode("101-02");
                ModelCoa coaKas = daoCOA.findById(penjualan.getCoaId());

                // Jurnal Piutang - Penjualan
                daoCOA.updateSaldo(coaPiutang.getId(), "debit", penjualan.getTotalHarga());
                daoCOA.updateSaldo(coaPenjualan.getId(), "credit", penjualan.getTotalHarga());

                // Jurnal Kas - Piutang
                daoCOA.updateSaldo(coaKas.getId(), "debit", penjualan.getTotalHarga());
                daoCOA.updateSaldo(coaPiutang.getId(), "credit", penjualan.getTotalHarga());

                // Jurnal HPP - Persediaan
                daoCOA.updateSaldo(coaHPP.getId(), "debit", totalValue);
                daoCOA.updateSaldo(coaPersedian.getId(), "credit", totalValue);

                return penjualanId;

            } catch (SQLException ex) {
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<ModelPenjualan> findAll() {
        List<ModelPenjualan> list = new ArrayList<>();
    
        String sql = "SELECT id, tanggal, total_harga, customer_id, coa_id FROM penjualan ORDER BY id DESC";
    
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
    
            while (rs.next()) {
                ModelPenjualan p = new ModelPenjualan();
                p.setId(rs.getInt("id"));
                p.setTanggal(rs.getString("tanggal"));
                p.setTotalHarga(rs.getDouble("total_harga"));
                p.setCustomerId(rs.getInt("customer_id"));
                p.setCoaId(rs.getInt("coa_id"));
    
                // isi detail
                p.setDetailList(findDetailByPenjualanId(conn, p.getId()));
    
                list.add(p);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return list;
    }

    private List<ModelPenjualanDetail> findDetailByPenjualanId(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, produk_id, qty, harga_satuan FROM penjualan_detail WHERE penjualan_id = ?";
        List<ModelPenjualanDetail> list = new ArrayList<>();
    
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                ModelPenjualanDetail d = new ModelPenjualanDetail();
                d.setId(rs.getInt("id"));
                d.setProdukId(rs.getInt("produk_id"));
                d.setQty(rs.getInt("qty"));
                d.setHargaSatuan(rs.getDouble("harga_satuan"));
                list.add(d);
            }
        }
        return list;
    }    
    
    public ModelPenjualan findById(int id) {
        String sqlPenjualan = "SELECT id, tanggal, total_harga, customer_id, coa_id FROM penjualan WHERE id = ?";
        String sqlDetail = "SELECT id, produk_id, qty, harga_satuan FROM penjualan_detail WHERE penjualan_id = ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlPenjualan)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ModelPenjualan p = new ModelPenjualan();
                p.setId(rs.getInt("id"));
                p.setTanggal(rs.getString("tanggal"));
                p.setTotalHarga(rs.getDouble("total_harga"));
                p.setCustomerId(rs.getInt("customer_id"));
                p.setCoaId(rs.getInt("coa_id"));

                // Ambil detail penjualan
                try (PreparedStatement psDet = conn.prepareStatement(sqlDetail)) {
                    psDet.setInt(1, id);
                    ResultSet rsDet = psDet.executeQuery();

                    List<ModelPenjualanDetail> detailList = new ArrayList<>();

                    while (rsDet.next()) {
                        ModelPenjualanDetail d = new ModelPenjualanDetail();
                        d.setId(rsDet.getInt("id"));
                        d.setProdukId(rsDet.getInt("produk_id"));
                        d.setQty(rsDet.getInt("qty"));
                        d.setHargaSatuan(rsDet.getDouble("harga_satuan"));
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

    public List<ModelPenjualan> findPage(int limit, int offset) {
        List<ModelPenjualan> list = new ArrayList<>();
        String sql = "SELECT * FROM penjualan ORDER BY id DESC LIMIT ? OFFSET ?";
    
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                ModelPenjualan p = new ModelPenjualan();
                p.setId(rs.getInt("id"));
                p.setTanggal(rs.getString("tanggal"));
                p.setTotalHarga(rs.getDouble("total_harga"));
                p.setCustomerId(rs.getInt("customer_id"));
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
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM penjualan")) {
    
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<ModelPenjualan> search(String keyword) {
        List<ModelPenjualan> list = new ArrayList<>();
    
        String sql = """
            SELECT p.*
            FROM penjualan p
            JOIN customer c ON c.id = p.customer_id
            WHERE LOWER(c.name) LIKE ? OR p.id LIKE ?
            ORDER BY p.id DESC
        """;
    
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ps.setString(2, "%" + keyword.toLowerCase() + "%");
    
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ModelPenjualan p = new ModelPenjualan();
                p.setId(rs.getInt("id"));
                p.setTanggal(rs.getString("tanggal"));
                p.setTotalHarga(rs.getDouble("total_harga"));
                p.setCustomerId(rs.getInt("customer_id"));
                p.setCoaId(rs.getInt("coa_id"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ModelPenjualanDetail> getDetailsByPenjualanId(int penjualanId) {
        List<ModelPenjualanDetail> list = new ArrayList<>();
    
        String sql = "SELECT * FROM penjualan_detail WHERE penjualan_id = ?";
    
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setInt(1, penjualanId);
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                ModelPenjualanDetail d = new ModelPenjualanDetail();
                d.setId(rs.getInt("id"));
                d.setPenjualanId(rs.getInt("penjualan_id"));
                d.setProdukId(rs.getInt("produk_id"));
                d.setQty(rs.getInt("qty"));
                d.setHargaSatuan(rs.getDouble("harga_satuan"));
                list.add(d);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return list;
    }    
}
