package com.jumbo.trus.fine;

import com.jumbo.trus.IFragment;

public interface IFineFragment extends IFragment {

    boolean createNewFine(String name, int amount);
    boolean editFine(String name, int amount, Fine fine);
}
