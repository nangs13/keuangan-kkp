package com.kkp.keuangan.components;

import com.kkp.keuangan.component.uis.RComboBoxUI;
import javax.swing.JComboBox;

public class RComboBox<E> extends JComboBox<E> {

    public RComboBox() {
        setUI(new RComboBoxUI());
    }
}
