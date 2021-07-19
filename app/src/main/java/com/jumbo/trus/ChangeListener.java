package com.jumbo.trus;

import java.util.List;

public interface ChangeListener {
    void itemAdded(Model model);
    void itemChanged(Model model);
    void itemDeleted(Model model);
    void itemListLoaded(List<Model> models, Flag flag);
    void alertSent(String message);
    void notificationAdded();
}
