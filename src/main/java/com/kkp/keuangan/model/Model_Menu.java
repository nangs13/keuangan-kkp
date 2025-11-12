package com.kkp.keuangan.model;
import java.awt.Color;
import java.lang.reflect.Field;

import javax.swing.Icon;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import com.kkp.keuangan.model.Enum.MenuKey;
import com.kkp.keuangan.model.Enum.MenuType;

public class Model_Menu {

    private String iconKey; // misal: "HOME", "USER", "COG"
    private String name;
    private MenuType type;
    private MenuKey key;

    public Model_Menu(String iconKey, String name, MenuType type, MenuKey key) {
        this.iconKey = iconKey;
        this.name = name;
        this.type = type;
        this.key = key;
    }

    public Model_Menu() {}

    public String getIconKey() { return iconKey; }
    public void setIconKey(String iconKey) { this.iconKey = iconKey; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MenuType getType() { return type; }
    public void setType(MenuType type) { this.type = type; }

    public MenuKey getKey() { return key; }
    public void setKey(MenuKey key) { this.key = key; }

    public Icon toIcon() {
        Ikon ikon = getIkonByName(iconKey);
        FontIcon icon = FontIcon.of(ikon);
        icon.setIconSize(20);
        icon.setIconColor(Color.white);
        return icon;
    }

    private Ikon getIkonByName(String name) {
        if (name == null || name.isEmpty()) {
            return FontAwesomeSolid.QUESTION_CIRCLE;
        }

        try {
            String fieldName = name.trim().toUpperCase().replace("-", "_");
            Field field = FontAwesomeSolid.class.getField(fieldName);
            return (Ikon) field.get(null);
        } catch (Exception e) {
            return FontAwesomeSolid.QUESTION_CIRCLE;
        }
    }
}
