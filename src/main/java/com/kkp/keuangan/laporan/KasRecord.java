package com.kkp.keuangan.laporan;

public class KasRecord {
    String namaAkun;
    String saldoAwal;
    String penerimaan;
    String pengeluaran;
    String mutasi;
    String saldoAkhir;

    public KasRecord(String namaAkun, String saldoAwal, String penerimaan,
                    String pengeluaran, String mutasi, String saldoAkhir) {
        this.namaAkun = namaAkun;
        this.saldoAwal = saldoAwal;
        this.penerimaan = penerimaan;
        this.pengeluaran = pengeluaran;
        this.mutasi = mutasi;
        this.saldoAkhir = saldoAkhir;
    }
}