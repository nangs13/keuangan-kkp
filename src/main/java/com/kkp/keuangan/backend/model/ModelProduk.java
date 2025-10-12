package com.kkp.keuangan.backend.model;

public class ModelProduk {
    private int id;
    private String nama;
    private double harga;
    private int stok;
    private String kategori;

    public ModelProduk() {
        // Constructor kosong untuk fleksibilitas
    }

    public ModelProduk(int id, String nama, double harga, int stok, String kategori) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.kategori = kategori;
    }

    public ModelProduk(String nama, double harga, int stok, String kategori) {
        this(0, nama, harga, stok, kategori);
    }

    // Getter
    public int getId() { return id; }
    public String getNama() { return nama; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
    public String getKategori() { return kategori; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setHarga(double harga) { this.harga = harga; }
    public void setStok(int stok) { this.stok = stok; }
    public void setKategori(String kategori) { this.kategori = kategori; }
}
