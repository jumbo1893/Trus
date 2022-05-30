package com.jumbo.trus.comparator.pkfl;

import com.jumbo.trus.pkfl.stats.PkflPlayerStats;

import java.util.Comparator;

public class OrderByYellowCardRatio implements Comparator<PkflPlayerStats> {

    private boolean desc;

    public OrderByYellowCardRatio(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(PkflPlayerStats o1, PkflPlayerStats o2) {
        if (desc) {
            return Float.compare(o2.getYellowCardMatchesRatio(), o1.getYellowCardMatchesRatio());
        }
        else {
            return Float.compare(o1.getYellowCardMatchesRatio(), o2.getYellowCardMatchesRatio());
        }
    }
}
