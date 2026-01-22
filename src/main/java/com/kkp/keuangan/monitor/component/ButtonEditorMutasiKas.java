package com.kkp.keuangan.monitor.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.kkp.keuangan.backend.dao.CoaDAO;
import com.kkp.keuangan.backend.dao.MutasiKasDAO;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.backend.model.ModelMutasiKas;
import com.kkp.keuangan.component.uis.RButtonUI;

public class ButtonEditorMutasiKas extends DefaultCellEditor {
    private JButton button;
    private JTable table;
    private int selectedRow;

    public ButtonEditorMutasiKas(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;

        button = new JButton("Detail");
        button.setUI(new RButtonUI());
        button.setForeground(new Color(0, 0, 0));

        button.addActionListener(e -> fireEditingStopped());
    }

    private void showDetailDialog(String mutasiKasCode) {
        MutasiKasDAO dao = new MutasiKasDAO();
        List<ModelMutasiKas> details = dao.findByCode(mutasiKasCode);

        Frame parent = JOptionPane.getFrameForComponent(table);
        JDialog dialog = new JDialog(parent, "Detail", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(null);

        String[] col = { "Tipe", "Akun", "Jumlah"};
        DefaultTableModel detailModel = new DefaultTableModel(col, 0);
        CoaDAO coaDAO = new CoaDAO();

        Map<String, Map<String, Object>> grouping = new HashMap<>();

        for (ModelMutasiKas d : details) {
            ModelCoa coaSumber = coaDAO.findByCode(d.getSumberCode());
            ModelCoa coaTujuan = coaDAO.findByCode(d.getTujuanCode());

            updateGrouping(grouping, coaSumber, d.getJumlah(), "credit");
            updateGrouping(grouping, coaTujuan, d.getJumlah(), "debit");
        }

        for (Map<String, Object> val : grouping.values()) {
            detailModel.addRow(new Object[]{
                val.get("tipe"),
                val.get("nama"),
                val.get("jumlah")
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
        String mutasiKasCode = table.getValueAt(selectedRow, 0).toString();
        showDetailDialog(mutasiKasCode);
        return "Detail";
    }

    private void updateGrouping(Map<String, Map<String, Object>> grouping, ModelCoa coa, double nominal, String tipe) {
        if (coa == null) return; 
        
        String compositeKey = coa.getCode() + "|" + tipe;

        grouping.compute(compositeKey, (key, detail) -> {
            if (detail == null) {
                detail = new HashMap<>();
                detail.put("tipe", tipe);
                detail.put("code", coa.getCode());
                detail.put("nama", coa.getNama());
                detail.put("jumlah", nominal);
            } else {
                double current = (double) detail.get("jumlah");
                detail.put("jumlah", current + nominal);
            }
            return detail;
        });
    }
}



