package com.kkp.keuangan.components;

import com.kkp.keuangan.component.uis.RPasswordFieldUI;
import javax.swing.JPasswordField;

/**
 *
 * @author Raven
 */
public class RPasswordField extends JPasswordField {

    public RPasswordField() {
        setUI(new RPasswordFieldUI());
    }
}
