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
            
            // COA - Chart of Account
            sql = "CREATE TABLE IF NOT EXISTS coa (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "code TEXT NOT NULL UNIQUE, " +
                    "nama TEXT NOT NULL, " +
                    "periode TEXT NOT NULL, " +             // format yyyymm (varchar)
                    "type TEXT NOT NULL, " +                // 'debit' or 'credit'
                    "parent_id INTEGER, " +
                    "beginning REAL NOT NULL DEFAULT 0, " +
                    "debit REAL NOT NULL DEFAULT 0, " +
                    "credit REAL NOT NULL DEFAULT 0, " +
                    "ending REAL NOT NULL DEFAULT 0, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(sql);
            
            // Card Info
            sql = "CREATE TABLE IF NOT EXISTS card_info (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "coa_id INTEGER NOT NULL, " +
                    "nama TEXT NOT NULL, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(sql);
            
            // Customer
            sql = "CREATE TABLE IF NOT EXISTS customer (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nama TEXT NOT NULL, " +
                    "hutang REAL NOT NULL DEFAULT 0, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(sql);
            
            // Supplier
            sql = "CREATE TABLE IF NOT EXISTS supplier (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nama TEXT NOT NULL, " +
                    "hutang REAL NOT NULL DEFAULT 0, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
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
            
            // Data COA
            // -----------------
            // AKTIVA (101)
            // -----------------
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('101','Aktiva', strftime('%Y%m','now'),'debit', NULL,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('101-01','Kas setara kas', strftime('%Y%m','now'),'debit',1,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('101-01001','Kas operasional', strftime('%Y%m','now'),'debit',2,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('101-01002','Kas kecil', strftime('%Y%m','now'),'debit',2,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('101-01003','Bank', strftime('%Y%m','now'),'debit',2,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('101-02','Piutang usaha', strftime('%Y%m','now'),'debit',1,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('101-03','Beban dibayar dimuka', strftime('%Y%m','now'),'debit',1,0,0,0,0)");

            // -----------------
            // LIABILITAS (201)
            // -----------------
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('201','Liabilitas', strftime('%Y%m','now'),'credit', NULL,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('201-01','Hutang usaha', strftime('%Y%m','now'),'credit',8,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('201-02','Beban yang harus dibayar', strftime('%Y%m','now'),'credit',8,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('201-03','Hutang Pajak', strftime('%Y%m','now'),'credit',8,0,0,0,0)");

            // -----------------
            // EKUITAS (301)
            // -----------------
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('301','Ekuitas', strftime('%Y%m','now'),'credit', NULL,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('301-01','Modal', strftime('%Y%m','now'),'credit',12,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('301-02','Prive', strftime('%Y%m','now'),'credit',12,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('301-03','Laba rugi ditahan tahun-tahun lalu', strftime('%Y%m','now'),'credit',12,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('301-04','Laba rugi ditahan tahun berjalan', strftime('%Y%m','now'),'credit',12,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('301-05','Laba rugi berjalan', strftime('%Y%m','now'),'credit',12,0,0,0,0)");

            // -----------------
            // PENDAPATAN (401)
            // -----------------
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('401','Pendapatan', strftime('%Y%m','now'),'credit', NULL,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('401-01','Penjualan', strftime('%Y%m','now'),'credit',18,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('401-02','Pendapatan lain-lain', strftime('%Y%m','now'),'credit',18,0,0,0,0)");

            // -----------------
            // BEBAN (501)
            // -----------------
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('501','Beban', strftime('%Y%m','now'),'debit', NULL,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('501-01','Beban operasional', strftime('%Y%m','now'),'debit',21,0,0,0,0)");
            stmt.execute("INSERT OR IGNORE INTO coa (code, nama, periode, type, parent_id, beginning, debit, credit, ending) VALUES ('501-02','Beban lain-lain', strftime('%Y%m','now'),'debit',21,0,0,0,0)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
