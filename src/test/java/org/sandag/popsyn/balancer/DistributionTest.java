package org.sandag.popsyn.balancer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.common.montecarlo.Distribution;
import org.sandag.popsyn.domain.Household;

public class DistributionTest
{
    protected static final Logger     LOGGER = Logger.getLogger(DistributionTest.class);
    protected Distribution<Household> dist   = null;

    @Before
    public void setUp() throws Exception
    {
        HashSet<Household> hhs = new HashSet<Household>();
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

        hharr[0].setModifiedWeight(31);
        hharr[1].setModifiedWeight(0);
        hharr[2].setModifiedWeight(51);
        hharr[3].setModifiedWeight(21);
        hharr[4].setModifiedWeight(36);
        hhs.addAll(Arrays.asList(hharr));

        dist = new Distribution<Household>();
        dist.setObjs(hhs);
    }

    @After
    public void tearDown() throws Exception
    {
        dist = null;
    }

    @Test
    public void testGetDist()
    {
        TreeMap<Household, Double> map = dist.getDist();
        Set<Entry<Household, Double>> set = map.entrySet();
        for (Entry<Household, Double> entry : set)
        {
            LOGGER.info("HH weight:" + entry.getKey().getInitWeight());
            LOGGER.info("HH weight:" + entry.getKey().getWeight());
            LOGGER.info("probability:" + entry.getValue());
            LOGGER.info("");
        }
    }

    @Test
    public void testGetAccDist()
    {
        SortedMap<Double, Household> map = dist.getAccDist();
        Set<Entry<Double, Household>> set = map.entrySet();
        LOGGER.info("test cummulative distribution");
        for (Entry<Double, Household> entry : set)
        {
            LOGGER.info("HH weight:" + entry.getValue().getInitWeight());
            LOGGER.info("probability:" + entry.getKey());
            LOGGER.info("");
        }
    }

}
