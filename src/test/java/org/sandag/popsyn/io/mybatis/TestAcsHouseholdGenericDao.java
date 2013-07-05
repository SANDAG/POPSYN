package org.sandag.popsyn.io.mybatis;

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
@ContextConfiguration(locations = { "classpath:applicationContext-mybatis.xml" })
@Ignore
//TODO: fix test
public class TestAcsHouseholdGenericDao
{
    @Autowired
    private IReaderDao hhReaderDao;

    @Test
    @SuppressWarnings("unused")
    public void testGetAll()
    {
        List<Household> households = hhReaderDao.getAllBySource(4);

        assertNotNull(households);

        int hCounter = 0;
        int pCounter = 0;

        for (Household household : households)
        {
            hCounter++;
            for (Person person : household.getPeople())
            {
                pCounter++;
            }
            //System.out.println(household);
        }
        
        

        assertEquals(57426, hCounter);
        assertEquals(142322, pCounter);
    }

    @Test
    public void testGetById()
    {
        Household household = hhReaderDao.getByIdAndSource("2005000000639", 4);
        assertEquals(Household.class, household.getClass());
        assertEquals(33000, household.getAdjIncome());
        assertEquals(new Integer(8111), household.getPumaId());

        assertEquals(2, household.getPeople().size());
        assertEquals(DwellingType.MULTI_UNIT, household.getDwellingType());

        assertEquals(UnitType.HOUSING_UNIT, household.getUnitType());
    }

}
