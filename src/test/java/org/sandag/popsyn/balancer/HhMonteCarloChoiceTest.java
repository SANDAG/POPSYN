package org.sandag.popsyn.balancer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.common.montecarlo.Distribution;
import org.sandag.common.montecarlo.MonteCarloChoice;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.io.ibatis.ReaderDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Ignore
//TODO: fix test
public class HhMonteCarloChoiceTest
{
    protected static final Logger         LOGGER     = Logger
                                                             .getLogger(HhMonteCarloChoiceTest.class);
    protected MonteCarloChoice<Household> choice     = null;
    protected boolean                     redraw     = false;
    private Set<Household>                hhs        = null;
    private ReaderDao                     pReaderDao = null;
    private Set<Household>                household  = new HashSet<Household>();

    @Before
    public void setUp() throws Exception
    {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        pReaderDao = (ReaderDao) ctx.getBean("ReaderDao");
        hhs = new HashSet<Household>(pReaderDao.getAllBySource(1));

        for (Household hh : hhs)
        {
            if (hh.getPumaId() == 8101)
            {
                hh.setModifiedWeight(hh.getInitWeight());
                household.add(hh);
            }
        }
    }

    @After
    public void tearDown() throws Exception
    {
        pReaderDao = null;
        choice = null;
    }

    @Test
    public void testGetAlts()
    {
        Distribution<Household> dist = new Distribution<Household>();
        dist.setObjs(household);
        choice = new MonteCarloChoice<Household>();
        choice.setAccDist(dist.getAccDist());
        System.out.println("household set size is " + household.size());
        System.out.println("cummulative % size is " + dist.getAccDist().size());

        int gqCount = 0;
        Collection<Household> c = dist.getAccDist().values();
        Iterator<Household> itr = c.iterator();

        while (itr.hasNext())
            if ((itr.next()).isGroupQuarter()) gqCount = gqCount + 1;
        System.out.println("number of GQs in accDist is " + gqCount);

        Set<Household> set = choice.getAlts(200);
        System.out.println("chosen household size is " + set.size());
        for (Household hh : set)
        {
            if (hh.isGroupQuarter())
            {
                System.out.println("chosen hh is GQ with weight: " + hh.getWeight()
                        + ", init weight " + hh.getInitWeight());
            }
        }
    }

}
