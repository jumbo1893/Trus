package com.jumbo.trus.comparator;

import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByBeerThenName implements Comparator<Player> {

    private boolean includeLiquors;

    public OrderByBeerThenName(boolean includeLiquors) {
        this.includeLiquors = includeLiquors;
    }

    @Override
    public int compare(Player o1, Player o2) {
        if (!includeLiquors) {
            Integer x1 = o1.getNumberOfBeers();
            Integer x2 = o2.getNumberOfBeers();
            int bComp = x2.compareTo(x1);
            if (bComp != 0) {
                return bComp;
            }
            return o1.getName().compareTo(o2.getName());
        }
        else {
            Integer x1 = o1.getNumberOfBeers()+o1.getNumberOfLiquors();
            Integer x2 = o2.getNumberOfBeers()+o1.getNumberOfLiquors();
            int bComp = x2.compareTo(x1);
            if (bComp != 0) {
                return bComp;
            }
            return o1.getName().compareTo(o2.getName());
        }
    }
}
