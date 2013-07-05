package org.sandag.popsyn.controls;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.domain.Person;

public class AbstractGroupControlDefinitionTest
{
    private IControlDefinition<Household> ctlDef1 = null;
    private IControlDefinition<Household> ctlDef2 = null;

    private Household                     hh1     = new Household();
    private Household                     hh2     = new Household();

    @Before
    public void setUp() throws Exception
    {
        GroupRangeIntegerControlDefinition<Household> grpIntCtlDef = new GroupRangeIntegerControlDefinition<Household>(
                Household.class);

        GroupBooleanControlDefinition<Household> grpBlnCtlDef = new GroupBooleanControlDefinition<Household>(
                Household.class);

        List<Person> hh1People = new ArrayList<Person>();
        List<Person> hh2People = new ArrayList<Person>();

        Person person1 = new Person();
        Person person2 = new Person();
        Person person3 = new Person();
        Person person4 = new Person();
        Person person5 = new Person();
        Person person6 = new Person();

        // Father
        person1.setAge(35);
        person1.setMale(true);
        hh1People.add(person1);
        // Mother
        person2.setAge(33);
        person2.setMale(false);
        hh1People.add(person2);
        // Son
        person3.setAge(9);
        person3.setMale(true);
        hh1People.add(person3);
        // Daughter
        person4.setAge(6);
        person4.setMale(false);
        hh1People.add(person4);

        // Male
        person5.setAge(25);
        person5.setMale(true);
        hh2People.add(person5);
        // Roomate
        person6.setAge(26);
        person6.setMale(true);
        hh2People.add(person6);

        hh1.setPeople(hh1People);
        hh2.setPeople(hh2People);

        grpIntCtlDef.setFieldName("people");
        grpIntCtlDef.setSubField("age");
        // grpIntCtlDef.setCategory("People <= 17");
        grpIntCtlDef.setDescription("Children in Household");
        grpIntCtlDef.setMinValue(0);
        grpIntCtlDef.setMaxValue(17);

        ctlDef1 = grpIntCtlDef;

        grpBlnCtlDef.setFieldName("people");
        grpBlnCtlDef.setSubField("male");
        // grpBlnCtlDef.setCategory("Gender");
        grpBlnCtlDef.setDescription("Gender = Female");
        grpBlnCtlDef.setCheck(false);

        ctlDef2 = grpBlnCtlDef;
    }

    @Test
    public void testGroupCompare()
    {
        assertEquals(2, ctlDef1.getParticipationFactor(hh1));
        assertEquals(0, ctlDef1.getParticipationFactor(hh2));

        assertEquals(2, ctlDef2.getParticipationFactor(hh1));
        assertEquals(0, ctlDef2.getParticipationFactor(hh2));
    }

    @Test
    public void testPerformance()
    {
        List<Person> hhPeople = new ArrayList<Person>();
        for (int i = 0; i < 100000; i++)
        {
            Person person = new Person();
            person.setAge((int) (Math.random() * 100));
            hhPeople.add(person);
        }

        Household hh = new Household();
        hh.setPeople(hhPeople);

        Date start = new Date();
        ctlDef1.getParticipationFactor(hh);
        Date finish = new Date();

        long runtime = (finish.getTime() - start.getTime());
        System.out.println("runtime: " + runtime + " ms");
    }
}
