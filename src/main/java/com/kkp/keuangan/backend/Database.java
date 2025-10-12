package com.kkp.keuangan.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:pos_app.db";

    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(DB_URL);
            createTablesIfNotExists(conn);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createTablesIfNotExists(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Produk
            String sql = "CREATE TABLE IF NOT EXISTS produk (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "nama TEXT NOT NULL, " +
                         "harga REAL NOT NULL, " +
                         "stok INTEGER NOT NULL, " +
                         "kategori  TEXT NOT NULL, " +
                         "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(sql);
            
            // Penjualan
            sql = "CREATE TABLE IF NOT EXISTS penjualan (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "tanggal TEXT, " +
                         "total_harga REAL) ";
            stmt.execute(sql);
            
            // Penjualan Detail
            sql = "CREATE TABLE IF NOT EXISTS penjualan_detail (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "penjualan_id INTEGER, " +
                         "produk_id INTEGER, " +
                         "qty INTEGER, " +
                         "harga_satuan REAL, " +
                         "FOREIGN KEY (penjualan_id) REFERENCES penjualan(id), " +
                         "FOREIGN KEY (produk_id) REFERENCES produk(id)) ";
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
