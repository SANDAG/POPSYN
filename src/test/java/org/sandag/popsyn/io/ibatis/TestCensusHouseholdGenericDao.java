package org.sandag.popsyn.io.ibatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.domain.Person;
import org.sandag.popsyn.enums.DwellingType;
import org.sandag.popsyn.enums.UnitType;
import org.sandag.popsyn.io.IReaderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Ignore
//TODO: fix test
public class TestCensusHouseholdGenericDao
{
    @Autowired
    private IReaderDao hhReaderDao;

    @Test
    @SuppressWarnings("unused")
    public void testGetAll()
    {
        List<Household> households = hhReaderDao.getAllBySource(1);

        assertNotNull(households);
        assertEquals(52774, households.size());

        int hCounter = 0;
        int pCounter = 0;

        for (Household household : households)
        {
            hCounter++;
            for (Person person : household.getPeople())
            {
                pCounter++;
            }
        }

        assertEquals(52774, hCounter);
        assertEquals(134866, pCounter);
    }

    @Test
    public void testGetById()
    {
        Household household = hhReaderDao.getByIdAndSource("1939574", 1);
        assertEquals(Household.class, household.getClass());
        assertEquals(new Integer(8102), household.getPumaId());
        assertEquals(DwellingType.MULTI_UNIT, household.getDwellingType());

        assertEquals(UnitType.HOUSING_UNIT, household.getUnitType());
      
        assertEquals(28180, household.getAdjIncome());
        assertEquals(6, household.getHhSize());      

        assertEquals(0, household.getNumOfWorkers());     

        assertEquals(3, household.getNumOfChildren());       

        Household milHh = hhReaderDao.getByIdAndSource("1997618", 1);

        assertEquals(UnitType.NON_INST_COLL, milHh.getUnitType());
  
        assertEquals(1, milHh.getNumOfWorkers());
    }

}
