package com.jumbo.trus.comparator;

import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByNonPlayerThenName implements Comparator<Player> {

    public OrderByNonPlayerThenName() {
    }

    @Override
    public int compare(Player o1, Player o2) {
        int aComp = Boolean.compare(o2.isMatchParticipant(), o1.isMatchParticipant());
        if (aComp != 0) {
            return aComp;
        }
        return o1.getName().compareTo(o2.getName());
    }
}
