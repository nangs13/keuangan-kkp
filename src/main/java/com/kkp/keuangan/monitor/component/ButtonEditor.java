package com.kkp.keuangan.monitor.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.kkp.keuangan.backend.dao.PenjualanDAO;
import com.kkp.keuangan.backend.dao.ProdukDAO;
import com.kkp.keuangan.backend.model.ModelPenjualanDetail;
import com.kkp.keuangan.backend.model.ModelProduk;
import com.kkp.keuangan.component.uis.RButtonUI;

public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private JTable table;
    private int selectedRow;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;

        button = new JButton("Detail");
        button.setUI(new RButtonUI());
        button.setForeground(new Color(0, 0, 0));

        button.addActionListener(e -> fireEditingStopped());
    }

    private void showDetailDialog(int penjualanId) {
        PenjualanDAO dao = new PenjualanDAO();
        java.util.List<ModelPenjualanDetail> details = dao.getDetailsByPenjualanId(penjualanId);

        Frame parent = JOptionPane.getFrameForComponent(table);
        JDialog dialog = new JDialog(parent, "Detail", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(null);

        String[] col = { "Produk", "Qty", "Harga", "Subtotal" };
        DefaultTableModel detailModel = new DefaultTableModel(col, 0);
        ProdukDAO produkDAO = new ProdukDAO();

        for (ModelPenjualanDetail d : details) {
            ModelProduk produk = produkDAO.findById(d.getProdukId());

            String namaProduk = produk != null ? produk.getNama() : "(Tidak ditemukan)";

            detailModel.addRow(new Object[]{
                namaProduk,
                d.getQty(),
                d.getHargaSatuan(),
                d.getQty() * d.getHargaSatuan()
            });
        }

        JTable detailTable = new JTable(detailModel);
        dialog.add(new JScrollPane(detailTable));

        dialog.setVisible(true);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int col) {
        this.selectedRow = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        int penjualanId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
        showDetailDialog(penjualanId);
        return "Detail";
    }
}



