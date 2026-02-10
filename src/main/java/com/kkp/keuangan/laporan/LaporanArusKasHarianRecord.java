package com.kkp.keuangan.laporan;

import java.time.LocalDate;

public class LaporanArusKasHarianRecord {
    private LocalDate tanggal;
    private double posisiAwal;
    private double pemasukan;
    private double pengeluaran;
    private double posisiAkhir;

    public LaporanArusKasHarianRecord(LocalDate tanggal, double posisiAwal, double pemasukan, double pengeluaran, double posisiAkhir) {
        this.tanggal = tanggal;
        this.posisiAwal = posisiAwal;
        this.pemasukan = pemasukan;
        this.pengeluaran = pengeluaran;
        this.posisiAkhir = posisiAkhir;
    }

    // Getters
    public LocalDate getTanggal() {
        return tanggal;
    }

    public double getPosisiAwal() {
        return posisiAwal;
    }

    public double getPemasukan() {
        return pemasukan;
    }

    public double getPengeluaran() {
        return pengeluaran;
    }

    public double getPosisiAkhir() {
        return posisiAkhir;
    }

    // Setters (if needed, but for records, often immutable)
    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public void setPosisiAwal(double posisiAwal) {
        this.posisiAwal = posisiAwal;
    }

    public void setPemasukan(double pemasukan) {
        this.pemasukan = pemasukan;
    }

    public void setPengeluaran(double pengeluaran) {
        this.pengeluaran = pengeluaran;
    }

    public void setPosisiAkhir(double posisiAkhir) {
        this.posisiAkhir = posisiAkhir;
    }
}
