package com.kkp.keuangan.monitor.component;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import com.kkp.keuangan.component.uis.RButtonUI;

import java.awt.*;

public class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setUI(new RButtonUI());
        setBackground(new Color(0x0d6efd));
        setBackground(new Color(0x0d6efd));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText("Detail");
        return this;
    }
}
