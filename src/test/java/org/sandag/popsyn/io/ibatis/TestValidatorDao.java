package org.sandag.popsyn.io.ibatis;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sandag.popsyn.io.IValidatorDao;
import org.sandag.popsyn.validator.IPumaValMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Ignore
//TODO: fix test
public class TestValidatorDao
{

    @Autowired
    private IValidatorDao validatorDao;

    @Test
    public void testGetAllFromPopSyn()
    {
        List<IPumaValMetrics> resultList = validatorDao.getAllFromPopSyn(1, 1080);
        assertEquals(16, resultList.size());
    }

    @Test
    public void testGetAllFromObserved()
    {
        List<IPumaValMetrics> resultList = validatorDao.getAllFromObserved(1);
        assertEquals(16, resultList.size());

        resultList = validatorDao.getAllFromObserved(4);
        assertEquals(16, resultList.size());
    }

    @Test
    public void testGetByPumaFromPopSyn()
    {
        IPumaValMetrics valMetric = validatorDao.getByPumaFromPopSyn(8116, 1, 1080);
        assertEquals(11895, valMetric.getHh1Person());
    }

    @Test
    public void testGetByPumaFromObserved()
    {
        IPumaValMetrics valMetric = validatorDao.getByPumaFromObserved(8116, 1);
        assertEquals(11850, valMetric.getHh1Person());

        valMetric = validatorDao.getByPumaFromObserved(8116, 4);
        assertEquals(20130, valMetric.getHh2Persons());
    }

}
