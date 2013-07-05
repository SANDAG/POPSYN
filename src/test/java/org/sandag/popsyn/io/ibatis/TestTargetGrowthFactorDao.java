package org.sandag.popsyn.io.ibatis;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.enums.GeographyType;
import org.sandag.popsyn.io.ITargetGrowthFactorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Ignore
//TODO: fix test
public class TestTargetGrowthFactorDao
{
    @Autowired
    private ITargetGrowthFactorDao targetGrowthDao;

    @Test
    public void testGetGrowthFactor()
    {
        Version version = new Version();
        version.setTargetGeography(GeographyType.TAZ_11);
        version.setTargetYear(2008);
        version.setTargetMajorVersion(10);
        version.setTargetMinorVersion(1);

        HashMap<Integer, Double> resultMap = targetGrowthDao.getGrowthFactor(18, version);

        assertEquals(36, resultMap.size());

        assertEquals(1.16593, resultMap.get(4), 0.00001);
    }

}
