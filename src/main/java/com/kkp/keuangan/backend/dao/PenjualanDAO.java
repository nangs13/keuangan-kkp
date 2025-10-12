package com.kkp.keuangan.backend.dao;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelPenjualan;
import com.kkp.keuangan.backend.model.ModelPenjualanDetail;
import java.sql.*;
import java.util.List;

public class PenjualanDAO {

    public int insert(ModelPenjualan penjualan) {
        String sqlPenjualan = "INSERT INTO penjualan (tanggal, total_harga) VALUES (?, ?)";
        String sqlDetail = "INSERT INTO penjualan_detail (penjualan_id, produk_id, qty, harga_satuan) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psPenjualan = conn.prepareStatement(sqlPenjualan, Statement.RETURN_GENERATED_KEYS)) {
                psPenjualan.setString(1, penjualan.getTanggal());
                psPenjualan.setDouble(2, penjualan.getTotalHarga());
                psPenjualan.executeUpdate();

                ResultSet rs = psPenjualan.getGeneratedKeys();
                int penjualanId = 0;
                if (rs.next()) {
                    penjualanId = rs.getInt(1);
                }

                try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                    for (ModelPenjualanDetail d : penjualan.getDetailList()) {
                        psDetail.setInt(1, penjualanId);
                        psDetail.setInt(2, d.getProdukId());
                        psDetail.setInt(3, d.getQty());
                        psDetail.setDouble(4, d.getHargaSatuan());
                        psDetail.addBatch();
                    }
                    psDetail.executeBatch();
                }

                conn.commit();
                return penjualanId;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
