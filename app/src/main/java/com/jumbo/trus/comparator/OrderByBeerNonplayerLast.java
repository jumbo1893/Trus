package com.jumbo.trus.comparator;

import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByBeerNonplayerLast implements Comparator<Player> {

    public OrderByBeerNonplayerLast() {
    }

    @Override
    public int compare(Player o1, Player o2) {
        int pComp = Boolean.compare(o2.isMatchParticipant(), o1.isMatchParticipant());
        if (pComp != 0) {
            return pComp;
        }
        Integer x1 = o1.getNumberOfBeers();
        Integer x2 = o2.getNumberOfBeers();
        return x2.compareTo(x1);
    }
}
