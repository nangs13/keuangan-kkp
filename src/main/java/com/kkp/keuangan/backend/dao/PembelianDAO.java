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
import com.kkp.keuangan.backend.model.ModelPembelian;
import com.kkp.keuangan.backend.model.ModelPembelianDetail;
import com.kkp.keuangan.backend.model.ModelProduk;

public class PembelianDAO {
    public int insert(ModelPembelian pembelian) {
        String sqlPembelian = "INSERT INTO pembelian (tanggal_pembelian, supplier_id, coa_id, remark) "
                + "VALUES (?,?,?,?)";
        String sqlDetail = "INSERT INTO pembelian_detail (pembelian_id, produk_id, qty, harga_satuan, total_harga)"
                + " VALUES (?,?,?,?,?)";
        String sqlUpdateProduk = "UPDATE produk SET harga = ?, stok = ? WHERE id = ?";

        try (Connection conn = Database.getConnection()) {

            try (PreparedStatement psPembelian = conn.prepareStatement(sqlPembelian, Statement.RETURN_GENERATED_KEYS)) {
                psPembelian.setString(1, pembelian.getTanggalPembelian());
                psPembelian.setInt(2, pembelian.getSupplierId());
                psPembelian.setInt(3, pembelian.getCoaId());
                psPembelian.setString(4, pembelian.getRemark());
                psPembelian.executeUpdate();

                ResultSet rs = psPembelian.getGeneratedKeys();
                int pembelianId = 0;
                if (rs.next()) {
                    pembelianId = rs.getInt(1);
                }

                Double totalValue = 0.0;

                try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                    for (ModelPembelianDetail d : pembelian.getDetailList()) {
                        psDetail.setInt(1, pembelianId);
                        psDetail.setInt(2, d.getProdukId());
                        psDetail.setDouble(3, d.getQty());
                        psDetail.setDouble(4, d.getHargaUnit());
                        psDetail.setDouble(5, d.getTotal());
                        psDetail.addBatch();

                        ProdukDAO dao = new ProdukDAO();
                        ModelProduk produk = dao.findById(d.getProdukId());
                        Double oldPrice = produk.getHarga();
                        int oldStok = produk.getStok();
                        totalValue += d.getHargaUnit() * d.getQty();

                        Double newPrice = ((oldPrice * oldStok) + (d.getHargaUnit() * d.getQty())) / (oldStok + d.getQty());
                        int newStok = oldStok + (int) d.getQty();
                        try (PreparedStatement psProduk = conn.prepareStatement(sqlUpdateProduk)) {
                            psProduk.setDouble(1, newPrice);
                            psProduk.setInt(2, newStok);
                            psProduk.setInt(3, d.getProdukId());

                            psProduk.execute();
                        }
                    }
                    psDetail.executeBatch();
                }

                // Update COA
                CoaDAO daoCOA = new CoaDAO();
                ModelCoa coaPersedian = daoCOA.findByCode("101-03001");
                ModelCoa coaHutang = daoCOA.findByCode("201-01");
                ModelCoa coaKas = daoCOA.findById(pembelian.getCoaId());

                // Jurnal Persedian - Hutang
                daoCOA.updateSaldo(coaPersedian.getId(), "debit", totalValue);
                daoCOA.updateSaldo(coaHutang.getId(), "credit", totalValue);

                // Jurnal Hutang - Kas
                daoCOA.updateSaldo(coaHutang.getId(), "debit", totalValue);
                daoCOA.updateSaldo(coaKas.getId(), "credit", totalValue);

                return pembelianId;

            } catch (SQLException ex) {
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static List<ModelPembelianDetail> findByPembelianId(int pembelianId) throws SQLException {
        List<ModelPembelianDetail> list = new ArrayList<>();
        String sql = "SELECT id, pembelian_id, produk_id, qty, harga_satuan, total_harga "
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
                        "",
                        rs.getDouble("harga_satuan"),
                        rs.getDouble("total_harga")));
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
        String sqlDetail = "SELECT id, pembelian_id, produk_id, qty, harga_satuan, total_harga FROM pembelian_detail WHERE pembelian_id=?";

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
                        d.setPembelianId(rsDet.getInt("pembelian_id"));
                        d.setProdukId(rsDet.getInt("produk_id"));
                        d.setQty(rsDet.getInt("qty"));
                        d.setHargaUnit(rsDet.getDouble("harga_satuan"));
                        d.setTotal(rsDet.getDouble("total_harga"));
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
                d.setPembelianId(rs.getInt("pembelian_id"));
                d.setProdukId(rs.getInt("produk_id"));
                d.setQty(rs.getInt("qty"));
                d.setHargaUnit(rs.getDouble("harga_satuan"));
                d.setTotal(rs.getDouble("total_harga"));
                list.add(d);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return list;
    }
}

