package org.sandag.popsyn.io.mybatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.enums.GeographyType;
import org.sandag.popsyn.io.IGeographyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-mybatis.xml" })
@Ignore
//TODO: fix test
public class GeographyDaoTest
{
    @Autowired
    private IGeographyDao geographyDao;

    @Test
    @Ignore
    public void testGetSubzoneMap()
    {
        Map<Integer, Integer> zones = geographyDao.getSubZoneMap(GeographyType.PUMA,
                GeographyType.MGRA_10);

        assertNotNull(zones);
        assertEquals(33353, zones.size());

        assertEquals(8115, zones.get(20475).intValue());
    }

    @Test
    public void testGetAllSubZonesByTypeAndId()
    {
        List<Geography> zones = geographyDao.getAllSubZonesByTypeAndId(GeographyType.TAZ_11, 509,
                GeographyType.MGRA_10);

        assertNotNull(zones);
        assertEquals(11, zones.size());
    }

    @Test
    public void testGetSuperZoneMap()
    {
        Map<Geography, List<Geography>> geos = geographyDao.getSuperZoneMap(GeographyType.TAZ_11,
                GeographyType.MGRA_10);

        assertEquals(4593, geos.size());

        int counter = 0;

        List<Integer> subzoneIds = new ArrayList<Integer>();

        for (List<Geography> subzones : geos.values())
        {
            for (Geography zone : subzones)
            {
                if (subzoneIds.contains(zone.getZone()))
                {
                    System.out.println("Zone is already matched: " + zone.getZone());
                } else
                {
                    subzoneIds.add(zone.getZone());
                    counter++;
                }

            }
        }

        assertEquals(33353, counter);
    }

    @Test
    public void testGetAllZonesByType()
    {
        List<Geography> geographies = geographyDao.getAllZonesByType(GeographyType.TAZ_11);
        assertNotNull(geographies);
        assertEquals(4605, geographies.size());
    }

    @Test
    public void testJDBCGeographyRetrieval() throws Exception
    {
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String connectionUrl = "jdbc:sqlserver://pila.sandag.org\\SdgIntDb:1285;"
                + "databaseName=data_cafe;integratedSecurity=false;user=popsyn;password=p0ps1n";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        Date startQuery = null;
        Date endQuery = null;

        try
        {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(connectionUrl);
            stmt = conn.createStatement();
            startQuery = new Date();
            rs = stmt.executeQuery(this.getQueryString());
            int counter = 0;
            while (rs.next())
                counter++;
            System.out.println("Record Count: " + counter);
            endQuery = new Date();
        } catch (SQLException sqlE)
        {
            sqlE.printStackTrace();
            fail();

        } finally
        {
            if (rs != null) rs.close();
            if (null != stmt) stmt.close();
            if (null != conn) conn.close();
        }

        System.out.println("Query Run Time (seconds): "
                + ((endQuery.getTime() - startQuery.getTime()) / 1000));
    }

    private String getQueryString()
    {
        return "WITH T AS (SELECT p.zone pZone, p.shape pShape, p.geo_type_id "
                + "FROM data_cafe.dbo.geography_zone p WHERE p.geo_type_id = 13) "
                + "SELECT T.pZone as pZone, T.geo_type_id as pGeoType, c.zone as cZone, "
                + "c.geo_type_id as cGeoType FROM data_cafe.dbo.geography_zone   c, T "
                + "WHERE c.geo_type_id = 9 and   T.pShape.STContains(c.centroid) = 1";
    }

    /*
     * @Test public void testGetAllZonesByType() { List<Geography> zones = geographyDao.getAllZonesByType(GeographyType.TAZ_11);
     * System.out.println("Number of zones=" + zones.size()); }
     * 
     * @Test public void testGetZone() { GeographyQueryParameters pars = new GeographyQueryParameters(); pars.setGivenZoneType(GeographyType.MGRA_10);
     * pars.setGivenZone(100); pars.setQueryZoneType(GeographyType.TAZ_11); Geography zone = geographyDao.getZone(pars);
     * System.out.println(zone.getZone()); }
     */

    /*
     * @Test public void testGeoXrefByZone() { GeographyQueryParameters pars = new GeographyQueryParameters();
     * pars.setGivenZoneType(GeographyType.TAZ_11); pars.setQueryZoneType(GeographyType.MGRA_10); pars.setGivenZone(100); List<Geography> list =
     * geographyDao.getGeoXrefByZone(pars); for (Geography pZone : list) { List<Geography> children = pZone.getChildren(); for (Geography cZone :
     * children) { System.out.println("parentZone=" + pZone.getZone() + " childZone=" + cZone.getZone()); } } System.out.println("size=" +
     * list.size()); }
     */
}
