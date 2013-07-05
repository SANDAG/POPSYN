package org.sandag.popsyn.inputs.domain.geography;

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.enums.GeographyType;

public class GeographyTest
{

    @Test
    public void testHashAndEquality()
    {
        Geography geo1 = new Geography();
        geo1.setGeoType(GeographyType.CITY);
        geo1.setZone(10);
        
        Geography geo1Copy = new Geography();
        geo1Copy.setGeoType(GeographyType.CITY);
        geo1Copy.setZone(10);
        
        Geography geo2 = new Geography();
        geo2.setGeoType(GeographyType.CITY);
        geo2.setZone(11);
        
        assertTrue(geo1Copy.equals(geo1));
        
        Map<Geography, Integer> aMap = new HashMap<Geography, Integer>();
        aMap.put(geo1Copy, 1);
        assertTrue(aMap.size() == 1);
        
        aMap.put(geo1, 1);
        assertTrue(aMap.size() == 1);
        
        aMap.put(geo2, 2);
        assertTrue(aMap.size() == 2);
    }

}
