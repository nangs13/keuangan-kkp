package com.kkp.keuangan.components;

import com.kkp.keuangan.component.uis.RButtonUI;
import javax.swing.JButton;

/**
 *
 * @author Raven
 */
public class RButton extends JButton {

    public RButton() {
       setUI(new RButtonUI());
    }
}
