package com.jumbo.trus.comparator.pkfl;

import com.jumbo.trus.pkfl.stats.PkflPlayerStats;

import java.util.Comparator;

public class OrderByBestPlayerNumber implements Comparator<PkflPlayerStats> {

    private boolean desc;

    public OrderByBestPlayerNumber(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(PkflPlayerStats o1, PkflPlayerStats o2) {
        if (desc) {
            Integer x1 = o1.getBestPlayer();
            Integer x2 = o2.getBestPlayer();
            int bComp = x2.compareTo(x1);
            if (bComp != 0) {
                return bComp;
            }
            return Integer.compare(o1.getMatches(), o2.getMatches());
        }
        else {
            Integer x1 = o1.getBestPlayer();
            Integer x2 = o2.getBestPlayer();
            int bComp = x1.compareTo(x2);
            if (bComp != 0) {
                return bComp;
            }
            return Integer.compare(o2.getMatches(), o1.getMatches());
        }
    }
}
