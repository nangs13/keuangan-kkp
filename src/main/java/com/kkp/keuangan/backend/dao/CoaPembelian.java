package com.kkp.keuangan.backend.dao;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoaPembelian {
    private static final Map<String, String> COA = new LinkedHashMap<>();
    static {
        COA.put("1100", "Kas / Bank");
        COA.put("1200", "Persediaan / Inventory");
        COA.put("2100", "Hutang Dagang");
        COA.put("5000", "Biaya Pembelian");
    }

    public static Map<String,String> getAll() { return COA; }
    public static String getNama(String kode) { return COA.get(kode); }
}
