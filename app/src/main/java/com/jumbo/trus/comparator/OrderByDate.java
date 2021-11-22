package com.jumbo.trus.comparator;

import com.jumbo.trus.Model;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByDate implements Comparator<PkflMatch> {

    private boolean desc;

    public OrderByDate(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(PkflMatch o1, PkflMatch o2) {
        Long x1 = o1.getDate();
        Long x2 = o2.getDate();

        if (desc) {
            return x2.compareTo(x1);
        }
        else {
            return x1.compareTo(x2);
        }
    }
}
