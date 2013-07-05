package org.sandag.popsyn.controls;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.popsyn.domain.Household;

public class BooleanControlDefinitionTest
{
    private IControlDefinition<Household> ctlDef = new BooleanControlDefinition<Household>(
                                                          Household.class);

    @Before
    public void setUp() throws Exception
    {
        BooleanControlDefinition<Household> boolCtlDef = new BooleanControlDefinition<Household>(
                Household.class);

        // boolCtlDef.setCategory("Household");
        // boolCtlDef.setDescription("Family Households");
        boolCtlDef.setFieldName("groupQuarter");

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

        assertEquals(0, ctlDef.getParticipationFactor(hh1));
        assertEquals(0, ctlDef.getParticipationFactor(hh2));
    }

}
