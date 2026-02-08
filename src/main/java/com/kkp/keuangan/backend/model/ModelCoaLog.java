package com.kkp.keuangan.backend.model;

import java.time.LocalDate;

public class ModelCoaLog {
    private int id;
    private int coaId;
    private LocalDate tanggal;
    private String tipe; // 'debit' or 'credit'
    private double nominal;
    private String keterangan;

    public ModelCoaLog() {
        // Constructor kosong
    }

    public ModelCoaLog(int id, int coaId, LocalDate tanggal, String tipe, double nominal, String keterangan) {
        this.id = id;
        this.coaId = coaId;
        this.tanggal = tanggal;
        this.tipe = tipe;
        this.nominal = nominal;
        this.keterangan = keterangan;
    }

    public ModelCoaLog(int coaId, LocalDate tanggal, String tipe, double nominal, String keterangan) {
        this(0, coaId, tanggal, tipe, nominal, keterangan);
    }

    // Getters
    public int getId() { return id; }
    public int getCoaId() { return coaId; }
    public LocalDate getTanggal() { return tanggal; }
    public String getTipe() { return tipe; }
    public double getNominal() { return nominal; }
    public String getKeterangan() { return keterangan; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCoaId(int coaId) { this.coaId = coaId; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setTipe(String tipe) { this.tipe = tipe; }
    public void setNominal(double nominal) { this.nominal = nominal; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
}
