package com.kkp.keuangan.backend.dao;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CoaPembelian - COA statis untuk keperluan transaksi pembelian.
 * Kamu bisa memperluas ini untuk menyimpan ke DB atau load dari konfigurasi.
 */
public class CoaPembelian {
    private static final Map<String, String> COA_MAP = new LinkedHashMap<>();

    static {
        // contoh COA: kode -> nama akun
        COA_MAP.put("1100", "Kas / Bank");
        COA_MAP.put("1200", "Persediaan / Inventory");
        COA_MAP.put("2100", "Hutang Dagang");
        COA_MAP.put("5000", "Biaya Pembelian");
        COA_MAP.put("4000", "Potongan Pembelian");
    }

    public static Map<String, String> getAllCoa() {
        return COA_MAP;
    }

    public static String getNamaByKode(String kode) {
        return COA_MAP.get(kode);
    }
}
