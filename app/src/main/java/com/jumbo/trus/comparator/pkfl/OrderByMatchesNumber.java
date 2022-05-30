package com.jumbo.trus.comparator.pkfl;

import com.jumbo.trus.pkfl.stats.PkflPlayerStats;

import java.util.Comparator;

public class OrderByMatchesNumber implements Comparator<PkflPlayerStats> {

    private boolean desc;

    public OrderByMatchesNumber(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(PkflPlayerStats o1, PkflPlayerStats o2) {
        if (desc) {
            return Integer.compare(o2.getMatches(), o1.getMatches());
        }
        else {
            return Integer.compare(o1.getMatches(), o2.getMatches());
        }
    }
}
