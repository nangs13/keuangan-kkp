package com.kkp.keuangan.backend.model;

public class ModelCardInfo {
    private int id;
    private int coaId;      // foreign key ke COA (ModelCoa)
    private String nama;    // nama kartu / uraian

    public ModelCardInfo() {
        // Constructor kosong
    }

    public ModelCardInfo(int id, int coaId, String nama) {
        this.id = id;
        this.coaId = coaId;
        this.nama = nama;
    }

    public ModelCardInfo(int coaId, String nama) {
        this(0, coaId, nama);
    }

    // Getter
    public int getId() { return id; }
    public int getCoaId() { return coaId; }
    public String getNama() { return nama; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setCoaId(int coaId) { this.coaId = coaId; }
    public void setNama(String nama) { this.nama = nama; }
}
