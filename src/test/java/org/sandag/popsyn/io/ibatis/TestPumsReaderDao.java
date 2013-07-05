package org.sandag.popsyn.io.ibatis;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.domain.Person;
import org.sandag.popsyn.io.IReaderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Ignore
//TODO: fix test
public class TestPumsReaderDao
{
    @Autowired
    private IReaderDao pReaderDao;

    @Test
    @SuppressWarnings("unused")
    public void testGetAllBySource()
    {
        List<Household> households = pReaderDao.getAllBySource(1);
        int pCounter = 0;

        assertEquals(52774, households.size());

        for (Household household : households)
        {
            if (8315125L == household.getId())
                System.out.println("hhsize for serialno 8315125 is " + household.getHhSize());
            if (720L == household.getId())
                System.out.println("group quarter for serialno 720 is "
                        + household.isGroupQuarter());
            if (6450594L == household.getId())
            {
                System.out.println("initial weight for serialno 6450594 is "
                        + household.getInitWeight());
                for (Person person : household.getPeople())
                {
                    System.out.println("race for hh serialno 6450594 is :" + person.getRace());

                }
            }
            if (12827L == household.getId())
            {
                for (Person person : household.getPeople())
                {
                    System.out.println("hh serialno 12827, person id: " + person.getId());
                    System.out.println("race is: " + person.getRace());
                    System.out.println("male is: " + person.isMale());
                    System.out.println("age is: " + person.getAge()); 

                }
            }
            
            for (Person person : household.getPeople())
            {
                pCounter++;
            }
        }

        assertEquals(134866, pCounter);

        households = pReaderDao.getAllBySource(4);
        pCounter = 0;

        assertEquals(57426, households.size());

        for (Household household : households)
        {

            if (2005000004935L == household.getId())
                System.out.println("hhsize for serialno 2005000004935 is " + household.getHhSize());
            if (2006000002854L == household.getId())
                System.out.println("group quarter for serialno 2006000002854 is "
                        + household.isGroupQuarter());

            if (2005000066342L == household.getId())
            {
                for (Person person : household.getPeople())
                {
                    System.out.println("hh serialno 2005000066342, person id: " + person.getId());
                    System.out.println("race is: " + person.getRace());
                    System.out.println("male is: " + person.isMale());
                    System.out.println("age is: " + person.getAge());

                }
            }

            int hhpCounter = 0;

            for (Person person : household.getPeople())
            {
                pCounter++;
                hhpCounter++;
            }
        }

        assertEquals(142322, pCounter);
    }
}
