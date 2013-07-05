package org.sandag.popsyn.balancer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.common.montecarlo.Distribution;
import org.sandag.common.montecarlo.MonteCarloChoice;
import org.sandag.popsyn.domain.Household;

public class HouseholdSamplerTest
{
    protected Set<Household> hhs = new HashSet<Household>();
    protected int            numOfTargetHhs;

    @Before
    public void setUp() throws Exception
    {
        Household hh1 = new Household();
        Household hh2 = new Household();
        Household hh3 = new Household();
        hh1.setId(1L);
        hh2.setId(2L);
        hh3.setId(3L);
        hh1.setInitWeight(20);
        hh2.setInitWeight(30);
        hh3.setInitWeight(50);
        hhs.add(hh1);
        hhs.add(hh2);
        hhs.add(hh3);
    }

    @After
    public void tearDown() throws Exception
    {
        hhs = null;
    }

    @Test
    public void testFilderSeedHouseholds()
    {
        Distribution<Household> hhd = new Distribution<Household>();
        hhd.setObjs(hhs);
        MonteCarloChoice<Household> mtc = new MonteCarloChoice<Household>();
        mtc.setAccDist(hhd.getAccDist());
        Set<Household> chosenHhs = mtc.getAlts(1);
        Iterator<Household> iter = chosenHhs.iterator();

        while (iter.hasNext())
        {
            System.out.println("choosen hh's weight is "
                    + ((Household) iter.next()).getInitWeight());
        }
    }
}
