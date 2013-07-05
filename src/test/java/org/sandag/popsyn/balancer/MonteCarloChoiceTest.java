package org.sandag.popsyn.balancer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.common.montecarlo.Distribution;
import org.sandag.common.montecarlo.MonteCarloChoice;
import org.sandag.popsyn.domain.Household;

public class MonteCarloChoiceTest
{
    protected static final Logger         LOGGER = Logger.getLogger(MonteCarloChoiceTest.class);
    protected MonteCarloChoice<Household> choice = null;
    protected boolean                     redraw = false;
    protected Set<Household>              hhs    = new HashSet<Household>();

    @Before
    public void setUp() throws Exception
    {
        Household[] hharr = new Household[5];
        for (int i = 0; i < hharr.length; i++)
        {
            hharr[i] = new Household();
            hharr[i].setId(new Long(i));
        }
        hharr[0].setInitWeight(30);
        hharr[1].setInitWeight(0);
        hharr[2].setInitWeight(50);
        hharr[3].setInitWeight(20);
        hharr[4].setInitWeight(35);

        hharr[0].setModifiedWeight(30);
        hharr[1].setModifiedWeight(0);
        hharr[2].setModifiedWeight(50);
        hharr[3].setModifiedWeight(20);
        hharr[4].setModifiedWeight(35);
        hhs.addAll(Arrays.asList(hharr));
    }

    @After
    public void tearDown() throws Exception
    {
        choice = null;
    }

    @Test
    public void testGetAlt()
    {
        Distribution<Household> dist = new Distribution<Household>();
        dist.setObjs(hhs);
        choice = new MonteCarloChoice<Household>();
        choice.setAccDist(dist.getAccDist());
        SortedMap<Double, Household> sm = dist.getAccDist();
        Set<Entry<Double, Household>> smset = sm.entrySet();
        for (Entry<Double, Household> entry : smset)
        {
            LOGGER.info("hh init weight:" + entry.getValue().getInitWeight() + "  acc=" + entry.getKey());
        }
        LOGGER.info("Chosen hh init weight:" + (choice.getAlt()).getInitWeight());
    }

    @Test
    public void testGetAlts()
    {
        Distribution<Household> dist = new Distribution<Household>();
        dist.setObjs(hhs);
        choice = new MonteCarloChoice<Household>();
        choice.setAccDist(dist.getAccDist());
        SortedMap<Double, Household> sm = dist.getAccDist();
        Set<Entry<Double, Household>> smset = sm.entrySet();
        for (Entry<Double, Household> entry : smset)
        {
            LOGGER.info("hh init weight:" + entry.getValue().getInitWeight() + "  acc=" + entry.getKey());
        }
        Set<Household> set = choice.getAlts(2);
        for (Household hh : set)
        {
            LOGGER.info("Chosen hh init weight:" + hh.getInitWeight());
            LOGGER.info("Chosen hh modified weight:" + hh.getWeight());
        }
    }

}
