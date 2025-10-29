package com.kkp.keuangan.form;

import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class FormPembelian extends JFrame {
    private JTable headerTablel;
    private JTable detaiTable;
    private DefaultTableModel headerTableModel;
    private DefaultTableModel detailTableModel;
    private JComboBox<SupplierItem> cbSupplier;
    private JButton btnTambahItem, btnSimpan, btnHapusItem, btnRefreshSupplier;
    private JLabel lblInfo;
    private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

}
