package com.kkp.keuangan.form.dialog;

import com.kkp.keuangan.backend.dao.ProdukDAO;
import com.kkp.keuangan.backend.model.ModelProduk;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DialogPilihProduk extends JDialog {
    private JTable table;
    private DefaultTableModel model;
    private ModelProduk selectedProduk;
    private final ProdukDAO dao = new ProdukDAO();

    public DialogPilihProduk(Window owner) {
        super(owner, "Pilih Produk", ModalityType.APPLICATION_MODAL);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel(new Object[]{"ID", "Nama", "Harga"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) pilihProduk();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnPilih = new JButton("Pilih");
        btnPilih.addActionListener(e -> pilihProduk());

        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBatal);
        bottom.add(btnPilih);

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);
        List<ModelProduk> list = dao.findAll();
        for (ModelProduk p : list) {
            model.addRow(new Object[]{p.getId(), p.getNama(), p.getHarga()});
        }
    }

    private void pilihProduk() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedProduk = new ModelProduk();
            selectedProduk.setId((int) model.getValueAt(row, 0));
            selectedProduk.setNama((String) model.getValueAt(row, 1));
            selectedProduk.setHarga((double) model.getValueAt(row, 2));
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!");
        }
    }

    public ModelProduk getSelectedProduk() {
        return selectedProduk;
    }
}
