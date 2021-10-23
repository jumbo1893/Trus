package com.jumbo.trus.listener;

import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;

import java.util.List;

public interface ChangeListener {
    void itemAdded(Model model);
    void itemChanged(Model model);
    void itemDeleted(Model model);
    void itemListLoaded(List<Model> models, Flag flag);
    void alertSent(String message);
}
