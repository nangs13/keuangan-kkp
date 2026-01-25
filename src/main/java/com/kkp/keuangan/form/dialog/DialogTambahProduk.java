package com.kkp.keuangan.form.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kkp.keuangan.backend.dao.ProdukDAO;
import com.kkp.keuangan.backend.model.ModelProduk;

public class DialogTambahProduk extends JDialog {
    private boolean cancelled = true;
    private JTextField tfNama, tfHarga, tfStok, tfKategori;

    public DialogTambahProduk(Window owner) {
        super(owner, "Tambah Produk Baru", ModalityType.APPLICATION_MODAL);
        setSize(360, 220);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel p = new JPanel(new GridLayout(4, 2, 6, 6));
        p.add(new JLabel("Nama Produk:"));
        tfNama = new JTextField();
        p.add(tfNama);
        p.add(new JLabel("Harga:"));
        tfHarga = new JTextField("0");
        p.add(tfHarga);
        p.add(new JLabel("Stok:"));
        tfStok = new JTextField("0");
        p.add(tfStok);
        p.add(new JLabel("Kategori:"));
        tfKategori = new JTextField();
        p.add(tfKategori);
        add(p, BorderLayout.CENTER);

        JButton ok = new JButton("Simpan");
        JButton cancel = new JButton("Batal");
        ok.addActionListener(e -> {
            if (!tfNama.getText().trim().isEmpty() && !tfKategori.getText().trim().isEmpty()) {
                try {
                    // Buat model produk baru
                    String nama = tfNama.getText().trim();
                    double harga = Double.parseDouble(tfHarga.getText().trim());
                    int stok = Integer.parseInt(tfStok.getText().trim());
                    String kategori = tfKategori.getText().trim();
                    ModelProduk produk = new ModelProduk(nama, harga, stok, kategori);

                    // Simpan ke database menggunakan DAO
                    ProdukDAO dao = new ProdukDAO();
                    dao.insert(produk);

                    cancelled = false;
                    setVisible(false);
                } catch (NumberFormatException ex) {
                    System.err.println("Input harga atau stok tidak valid.");
                } catch (Exception ex) {
                    System.err.println("Gagal menyimpan produk: " + ex.getMessage());
                }
            }
        });
        cancel.addActionListener(e -> setVisible(false));

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(ok);
        bp.add(cancel);
        add(bp, BorderLayout.SOUTH);
    }

    public boolean isCancelled() {
        return cancelled;
    }
}