package org.sandag.popsyn.io.ibatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.controls.IControlDefinition;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.enums.GeographyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Ignore
//TODO: fix test
public class TargetDaoTest
{
    @Autowired
    private TargetDao targetDao;

    @After
    public void tearDown() throws Exception
    {
        targetDao = null;
    }

    @Test
    public void testGetZonalHHTotal()
    {
        int zone = 56;
        Version version = new Version();
        version.setTargetYear(2010);
        version.setBalanceGeography(GeographyType.MGRA_10);
        version.setTargetMajorVersion(1);
        version.setTargetMinorVersion(0);

        double value = targetDao.getZonalHHTotal(zone, version);

        assertEquals(24.63956, value, 0.00001);
    }

    @Test
    public void testGetZonalGQTotal()
    {
        int zone = 2306;
        Version version = new Version();
        version.setTargetYear(2010);
        version.setBalanceGeography(GeographyType.MGRA_10);
        version.setTargetMajorVersion(1);
        version.setTargetMinorVersion(0);

        double value = targetDao.getZonalGQTotal(zone, version);

        assertEquals(41, value, 0.00001);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetZonalTargets()
    {
        List<IControlDefinition<Household>> ctrlDefs = new ArrayList<IControlDefinition<Household>>();
        ApplicationContext ctx = new ClassPathXmlApplicationContext("control-target-config.xml");

        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("InstGQControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("ColGQControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("MilGQControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("OthGQControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("ZeroChildHouseholdControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("OnePlusChildrenHouseholdControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("sfHouseholdTypeControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("mfHouseholdTypeControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("mhHouseholdTypeControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("numOfWorkers0ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("numOfWorkers1ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("numOfWorkers2ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("numOfWorkers3ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("householdIncome1ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("householdIncome2ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("householdIncome3ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("householdIncome4ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx
                .getBean("householdIncome5ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("hhSize1ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("hhSize2ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("hhSize3ControlDefinition"));
        ctrlDefs.add((IControlDefinition<Household>) ctx.getBean("hhSize4ControlDefinition"));

        Version version = new Version();
        version.setTargetYear(2010);
        version.setTargetGeography(GeographyType.MGRA_10);
        version.setTargetMajorVersion(1);
        version.setTargetMinorVersion(0);

        List<Geography> geos = new ArrayList<Geography>();
        Geography geo1 = new Geography();
        geo1.setZone(2306);
        geos.add(geo1);

        Geography geo2 = new Geography();
        geo2.setZone(1503);
        geos.add(geo2);

        Geography geo3 = new Geography();
        geo3.setZone(1804);
        geos.add(geo3);

        Geography geo4 = new Geography();
        geo4.setZone(875);
        geos.add(geo4);

        Hashtable<IControlDefinition<Household>, Double> results = targetDao.getZonalTargets(geos,
                ctrlDefs, version);

        assertNotNull(results);
        assertEquals(11, results.size());
        assertEquals(139.6583, results.get((IControlDefinition<Household>) ctx
                .getBean("hhSize3ControlDefinition")), .0001);

    }
}
