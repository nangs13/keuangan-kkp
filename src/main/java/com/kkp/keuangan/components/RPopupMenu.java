package com.kkp.keuangan.components;

import com.kkp.keuangan.component.uis.RPopupMenuUI;
import javax.swing.JPopupMenu;

/**
 *
 * @author Raven
 */
public class RPopupMenu extends JPopupMenu {

    public RPopupMenu() {
        setUI(new RPopupMenuUI());
    }
}
