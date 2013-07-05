package org.sandag.popsyn.controls;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.enums.ControlCategoryType;
import org.sandag.popsyn.enums.DwellingType;
import org.sandag.popsyn.io.IReaderDao;
import org.sandag.popsyn.io.ibatis.ReaderDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Ignore
//TODO: fix test
public class IntegerControlDefinitionTest
{
    private List<IControlDefinition<Household>> ctrlDefs;
    private IControlDefinition<Household>       edef;
    private List<Household>                     hhs;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        // populate control definition list
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        ctrlDefs = new ArrayList<IControlDefinition<Household>>();

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

        for (IControlDefinition<Household> def : ctrlDefs)
        {
            if (def.getCategory() == ControlCategoryType.SINGLE_FAMILY)
            {
                edef = def;
            }
        }

        IReaderDao hhReaderDao = ctx.getBean("ReaderDao", ReaderDao.class);

        hhs = hhReaderDao.getAllBySource(1);
    }

    @Test
    public void testCompare() throws Exception
    {
        for (Household hh : hhs)
        {
            if (hh.getDwellingType() == DwellingType.SINGLE_FAMILY)
            {
                edef.getParticipationFactor(hh);
            }
        }
    }

    /*
     * @Test public void testControlListAdd() { ControlList cList1 = new ControlList(controlDefList.get(0), 8101); ControlList cList2 = new
     * ControlList(controlDefList.get(0), 8105); for (IHousehold hh : hhs) { cList1.add(hh); cList2.add(hh); } assertEquals(3, cList1.size());
     * assertEquals(2, cList2.size()); }
     */
}
