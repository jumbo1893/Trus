package com.jumbo.trus.repayment;

import com.jumbo.trus.IFragment;
import com.jumbo.trus.player.Player;

public interface IRepaymentFragment extends IFragment {

    boolean createNewRepayment(int amount, String note);
}
