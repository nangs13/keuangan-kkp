package com.kkp.keuangan.event;

import com.kkp.keuangan.model.Enum.MenuKey;

public interface EventMenuSelected {
    void selected(MenuKey key);
}
