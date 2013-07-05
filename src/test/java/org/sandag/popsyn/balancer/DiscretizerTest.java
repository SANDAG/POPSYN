package org.sandag.popsyn.balancer;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.popsyn.domain.Household;

public class DiscretizerTest
{
    protected static final Logger    LOGGER = Logger.getLogger(DistributionTest.class);
    protected Discretizer<Household> disc   = null;

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
        disc = null;
    }

    @Test
    public void testDiscretize()
    {
        /*
         * HashSet<Household> hhs = new HashSet<Household>(); Household hh1 = new Household(); Household hh2 = new Household(); Household hh3 = new
         * Household(); hh1.setId(1); hh1.setInitWeight(20.45); hh1.setModifiedWgt(20.55);
         * 
         * hh2.setId(2); hh2.setInitWeight(31.23); hh2.setModifiedWgt(30.98);
         * 
         * hh3.setId(3); hh3.setInitWeight(50.2); hh3.setModifiedWgt(50.01);
         * 
         * hhs.add(hh1); hhs.add(hh2); hhs.add(hh3);
         * 
         * //need to change version here. RandomSeedManager rdm=new RandomSeedManager(); disc = new Discretizer<Household>(new Version(), 100);
         * HashSet<Household> dhhs = (HashSet<Household>) disc.discretize(hhs, 102); for (Household hh : dhhs) { LOGGER.info("init weigt:" +
         * hh.getInitWeight()); } System.out.println("modified weight"); for (Household hh : hhs) {
         * 
         * LOGGER.info("modified weigt:" + hh.getWeight()); }
         */
    }

}
