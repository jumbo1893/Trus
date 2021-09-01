package com.jumbo.trus.comparator;

import com.jumbo.trus.fine.ReceivedFine;

import java.util.Comparator;

public class OrderByNonplayerFine implements Comparator<ReceivedFine> {

    private boolean allPlayerFineFirst;

    public OrderByNonplayerFine(boolean allPlayerFineFirst) {
        this.allPlayerFineFirst = allPlayerFineFirst;
    }

    @Override
    public int compare(ReceivedFine o1, ReceivedFine o2) {
        if (allPlayerFineFirst) {
            return Boolean.compare(o1.getFine().isForNonPlayers(), o2.getFine().isForNonPlayers());
        }
        else {
            return Boolean.compare(o2.getFine().isForNonPlayers(), o1.getFine().isForNonPlayers());
        }
    }
}
