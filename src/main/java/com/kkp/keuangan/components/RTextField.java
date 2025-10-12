package com.kkp.keuangan.components;

import com.kkp.keuangan.component.uis.RTextFieldUI;
import javax.swing.JTextField;

/**
 *
 * @author Raven
 */
public class RTextField extends JTextField {

    public RTextField() {
        setUI(new RTextFieldUI());
    }
}
