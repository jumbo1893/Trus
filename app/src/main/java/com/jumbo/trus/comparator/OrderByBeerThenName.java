package com.jumbo.trus.comparator;

import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByBeerThenName implements Comparator<Player> {

    public OrderByBeerThenName() {
    }

    @Override
    public int compare(Player o1, Player o2) {
        Integer x1 = o1.getNumberOfBeers();
        Integer x2 = o2.getNumberOfBeers();
        int bComp = x2.compareTo(x1);
        if (bComp != 0) {
            return bComp;
        }
        return o1.getName().compareTo(o2.getName());
    }
}
