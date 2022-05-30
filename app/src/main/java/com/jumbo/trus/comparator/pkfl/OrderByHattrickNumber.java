package com.jumbo.trus.comparator.pkfl;

import com.jumbo.trus.pkfl.stats.PkflPlayerStats;

import java.util.Comparator;

public class OrderByHattrickNumber implements Comparator<PkflPlayerStats> {

    private boolean desc;

    public OrderByHattrickNumber(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(PkflPlayerStats o1, PkflPlayerStats o2) {
        if (desc) {
            Integer x1 = o1.getHattrick();
            Integer x2 = o2.getHattrick();
            int bComp = x2.compareTo(x1);
            if (bComp != 0) {
                return bComp;
            }
            return Integer.compare(o2.getGoals(), o1.getGoals());
        }
        else {
            Integer x1 = o1.getHattrick();
            Integer x2 = o2.getHattrick();
            int bComp = x1.compareTo(x2);
            if (bComp != 0) {
                return bComp;
            }
            return Integer.compare(o1.getGoals(), o2.getGoals());
        }
    }
}
