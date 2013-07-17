CREATE FUNCTION [getxref](@SuperZone integer, @SubZone integer)
 RETURNS @ret_table Table
  (
	pZone int,
	pGeoType int,
	cZone int,
	cGeoType int	
  )
  AS 

BEGIN

 WITH T 
    AS ( 
      SELECT 
        p.zone pZone,
        p.shape pShape,
        p.geo_type_id   
      FROM
        data_cafe.dbo.geography_zone p
      WHERE p.geo_type_id = @SuperZone
    )
    
  INSERT @ret_table
  
  SELECT
    T.pZone as pZone, 
    T.geo_type_id as pGeoType,
    c.zone as cZone,
    c.geo_type_id as cGeoType   
  FROM
    data_cafe.dbo.geography_zone   c, T   
  WHERE
    c.geo_type_id = @SubZone
    and   T.pShape.STContains(c.centroid) = 1 
    
  RETURN
 END
GO