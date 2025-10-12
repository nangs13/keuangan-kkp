package com.kkp.keuangan.components;

import com.kkp.keuangan.component.uis.RMenuBarUI;
import javax.swing.JMenuBar;

/**
 *
 * @author Raven
 */
public class RMenuBar extends JMenuBar {

    public RMenuBar() {
        setUI(new RMenuBarUI());
    }
}
