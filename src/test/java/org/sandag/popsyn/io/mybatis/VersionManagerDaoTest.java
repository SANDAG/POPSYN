package org.sandag.popsyn.io.mybatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.enums.GeographyType;
import org.sandag.popsyn.io.IVersionManagerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-mybatis.xml" })
@Ignore
//TODO: fix test
public class VersionManagerDaoTest
{
    @Autowired
    private IVersionManagerDao vMgrDao;

    @Test
    @Ignore
    public void testInitializeRun()
    {
        Version version = new Version();

        version.setTargetMajorVersion(2);
        version.setTargetMinorVersion(1);
        version.setDescription("JUNIT TEST: " + Math.random());
        version.setTargetYear(2030);
        version.setSourceId(4);
        version.setTargetGeography(GeographyType.TAZ_11);
        version.setBalanceGeography(GeographyType.MGRA_12);
        version.setStartTime(new Date());
        version.setValidateSourceId(4);
        version.setValCount(10);
        version.setTargetMethod("regular");
        version.setSelectZones("NA");
        version.setAllocationGeography(GeographyType.TAZ_11);
        version.setSmallValue(-999999999);
        version.setPumaStart(8101);
        version.setNumExtTaz(14);
        version.setUseInitWeight(true);
        version.setFixRandomSeed(true);

        vMgrDao.initializeRun(version);

        assertNotNull(version.getId());
    }

    @Test
    @Ignore
    public void testFinalizeRun()
    {
        assertTrue(vMgrDao.finalizeRun(new Integer(7)));
    }

    @Test
    public void testUsername()
    {
        String user = System.getProperty("user.name");
        assertEquals("cdan", user);
    }

}
