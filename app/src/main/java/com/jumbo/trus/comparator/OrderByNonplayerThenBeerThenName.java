package com.jumbo.trus.comparator;

import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByNonplayerThenBeerThenName implements Comparator<Player> {

    public OrderByNonplayerThenBeerThenName() {
    }

    @Override
    public int compare(Player o1, Player o2) {
        int aComp = Boolean.compare(o2.isMatchParticipant(), o1.isMatchParticipant());
        if (aComp != 0) {
            return aComp;
        }
        Integer x1 = o1.getNumberOfBeers();
        Integer x2 = o2.getNumberOfBeers();
        int bComp = x2.compareTo(x1);
        if (bComp != 0) {
            return bComp;
        }
        return o2.getName().compareTo(o1.getName());
    }
}
