package com.jumbo.trus.repayment;

import com.jumbo.trus.IFragment;
import com.jumbo.trus.player.Player;

public interface IRepaymentFragment {

    boolean createNewRepayment(int amount, String note, Player player);
    boolean deleteRepayment(Repayment repayment, Player player);
}
