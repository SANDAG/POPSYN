package org.sandag.popsyn.balancer;

import static org.junit.Assert.assertEquals;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.popsyn.domain.Household;

public class SetSorterTest
{

    @Test
    public void testSort()
    {
        Household hh1 = new Household();
        hh1.setId(3L);
        Household hh2 = new Household();
        hh2.setId(1L);
        Household hh3 = new Household();
        hh3.setId(8L);
        Household hh4 = new Household();
        hh4.setId(5L);
        SortedSet<Household> hhs = new TreeSet<Household>();
        hhs.add(hh1);
        hhs.add(hh2);
        hhs.add(hh3);
        hhs.add(hh4);

        Iterator<Household> iterator = hhs.iterator();
        assertEquals(1L, iterator.next().getId().longValue());
        assertEquals(3L, iterator.next().getId().longValue());
        assertEquals(5L, iterator.next().getId().longValue());
        assertEquals(8L, iterator.next().getId().longValue());
    }

}
