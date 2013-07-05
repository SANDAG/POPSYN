package org.sandag.popsyn.controls;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.domain.Person;

public class GroupBooleanControlDefinitionTest
{
    private IControlDefinition<Household> ctlDef = new GroupBooleanControlDefinition<Household>(
                                                          Household.class);

    @Before
    public void setUp() throws Exception
    {
        GroupBooleanControlDefinition<Household> boolCtlDef = new GroupBooleanControlDefinition<Household>(
                Household.class);

    
        boolCtlDef.setFieldName("people");
        boolCtlDef.setSubField("hispanic");
        boolCtlDef.setCheck(true);

        ctlDef = boolCtlDef;
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testGetParticipationFactor()
    {
        Household hh1 = new Household();
        Household hh2 = new Household();
        
        Person per1 = new Person();
        Person per2 = new Person();
        
        per1.setHispanic(true);
        per2.setHispanic(false);
        
        
        ((Household) hh1).addPerson((Person) per1);
        ((Household) hh2).addPerson((Person) per2);
  
        assertEquals(1, ctlDef.getParticipationFactor(hh1));
        assertEquals(0, ctlDef.getParticipationFactor(hh2));
    }

}
