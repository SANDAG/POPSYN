USE [popsyn]
GO


-- ========================================================================
-- Author:		Eddie Janowicz
-- Create date: April, 2011
-- Description:	insert discretized household from temp csv file to table 
-- ========================================================================



ALTER PROCEDURE [dbo].[proc_regenerate_acs_targets]
	@tYear int 
AS
BEGIN

	-- 1. drop 2006 targets from target table
	
		DELETE FROM target WHERE year = 2006 AND geo_type_id = 13;
	
	-- 2. generate and insert new ACS-based TAZ targets
			
		with a as(
		SELECT taz, SUM(proportion * persons) AS pop,  SUM(proportion * male) AS male,  SUM(proportion * female) AS female,  
		SUM(proportion * age0to17) AS age0to17,  SUM(proportion * age18to24) AS age18to24,  SUM(proportion * age25to34) AS age25to34,  
		SUM(proportion * age35to49) AS age35to49,  SUM(proportion * age50to64) AS age50to64,  SUM(proportion * age65to79) AS age65to79,  
		SUM(proportion * age80plus) AS age80plus,  SUM(proportion * white) AS white,   SUM(proportion * black) AS black,  
		SUM(proportion * amindian) AS amindian,   SUM(proportion * asian) AS asian,   SUM(proportion * pacisland) AS pacisland,  
		SUM(proportion * other) AS other,   SUM(proportion * twoplus) AS twoplus,   SUM(proportion * hispanic) AS hispanic,  
		SUM(proportion * totalhh) AS totalhh,   SUM(proportion * onepluschild) AS onepluschild,   SUM(proportion * zerochild) AS zerochild, 
		SUM(proportion * hhsizeone) AS hhsizeone,   SUM(proportion * hhsizetwo) AS hhsizetwo,   SUM(proportion * hhsizethree) AS hhsizethree, 
		SUM(proportion * hhsizefourplus) AS hhsizefourplus,   SUM(proportion * income0to30k) AS income0to30k,   SUM(proportion * income30to60k) AS income30to60k, 
		SUM(proportion * income60to100k) AS income60to100k,   SUM(proportion * income100to150k) AS income100to150k,   SUM(proportion * income150kplus) AS income150kplus, 
		SUM(proportion * singlefam) AS singlefam,   SUM(proportion * multifam) AS multifam,   SUM(proportion * mobile) AS mobile
		FROM acs_bg_targets JOIN xref_blkgrp_taz10
		ON acs_bg_targets.blkgrp = xref_blkgrp_taz10.blkgrp
		GROUP BY taz)
		,b as (
		SELECT taz, SUM(proportion * totalhh) AS totalhh,  SUM(proportion * zeroworkers) AS zeroworkers,  SUM(proportion * oneworker) AS oneworker,  
		SUM(proportion * twoworkers) AS twoworkers,  SUM(proportion * threeplusoworkers) AS threeplusworkers
		FROM acs_tract_targets JOIN xref_tract_taz10
		ON acs_tract_targets.tract = xref_tract_taz10.tract
		GROUP BY taz)
		, c as (
		SELECT a.taz AS taz, zeroworkers*(a.totalhh / b.totalhh) as zeroworkers,  oneworker*(a.totalhh / b.totalhh) as oneworker,
		twoworkers*(a.totalhh / b.totalhh) as twoworkers, threeplusworkers*(a.totalhh / b.totalhh) as threeplusworkers
		FROM a, b
		WHERE a.taz = b.taz 
		AND b.totalhh > 0
		)
		, d as (
		SELECT g.taz, SUM(g.gqproportion * a.gq) AS gqtotal, SUM(g.gqinst * a.gq) AS gqinst, SUM(g.gqnoninstcoll * a.gq) AS gqnoninstcoll, SUM(g.gqnoninstmil * a.gq) AS gqnoninstmil, SUM(g.gqnoninstother * a.gq) AS gqnoninstother 
		FROM acs_tract_targets a JOIN xref_tract_taz10_gq_type g
		ON a.tract = g.tract
		GROUP BY taz
		)
		,e as (
		SELECT a.taz as taz,  pop, male, female, age0to17,   age18to24,  age25to34,  
		 age35to49,  age50to64,  age65to79,  
		age80plus, white,  black,  
		amindian,  asian,  pacisland,  
		 other, twoplus,  hispanic,  
		 a.totalhh as totalhh,  onepluschild,  zerochild, 
		 hhsizeone,   hhsizetwo,   hhsizethree, 
		 hhsizefourplus,  income0to30k,  income30to60k, 
		income60to100k, income100to150k,  income150kplus, 
		singlefam,   multifam, mobile, 
		c.zeroworkers,  c.oneworker, c.twoworkers, c.threeplusworkers, 
		d.gqtotal, d.gqinst, d.gqnoninstcoll, d.gqnoninstmil, d.gqnoninstother
		FROM ((a join b on a.taz = b.taz )
		left join c on a.taz = c.taz)
		left join d on a.taz = d.taz)

		,f as (
		select taz, 2007 as year, pop, male, female, age0to17,   age18to24,  age25to34,  
		 age35to49,  age50to64,  age65to79,  
		age80plus, white,  black,  
		amindian,  asian,  pacisland,  
		 other, twoplus,  hispanic,  
		 totalhh,  onepluschild,  zerochild, 
		 hhsizeone,   hhsizetwo,   hhsizethree, 
		 hhsizefourplus,  income0to30k,  income30to60k, 
		income60to100k, income100to150k,  income150kplus, 
		singlefam,   multifam, mobile, 
		isnull(zeroworkers,0) as zeroworkers,  isnull(oneworker,0) as oneworker, isnull(twoworkers,0) as twoworkers, isnull(threeplusworkers,0) as threeplusoworkers, 
		isnull(gqtotal,0) as gqtotal, isnull(gqinst,0) as gqinst, isnull(gqnoninstcoll,0) as gqnoninstcoll, isnull(gqnoninstmil,0) as gqnoninstmil, isnull(gqnoninstother,0) as gqnoninstother
		from e)

		,g as (
		SELECT geo_zone, tblPivot.category, tblPivot.value
		FROM 
		(Select taz as geo_zone,
		CONVERT(real,pop) as totalPop,
		CONVERT(real,male) as MALE,
		CONVERT(real, female ) as  FEMALE ,
		CONVERT(real,  age0to17) as  AGE0TO17 ,
		CONVERT(real, age18to24 ) as  AGE18TO24 ,
		CONVERT(real, age25to34 ) as  AGE25TO34 ,
		CONVERT(real,  age35to49) as  AGE35TO49 ,
		CONVERT(real,  age50to64) as  AGE50TO64 ,
		CONVERT(real, age65to79 ) as AGE65TO79  ,
		CONVERT(real,  age80plus) as  AGE80PLUS ,
		CONVERT(real, white ) as  WHITE ,
		CONVERT(real,  black) as BLACK  ,
		CONVERT(real, amindian ) as  NINDIAN ,
		CONVERT(real, asian ) as  ASIAN ,
		CONVERT(real, pacisland ) as  ISLANDER ,
		CONVERT(real, other ) as RACEOTHER  ,
		CONVERT(real, twoplus ) as  MIXED ,
		CONVERT(real, hispanic ) as  HISPANIC ,
		CONVERT(real, totalhh ) as  totalHouseholds ,
		CONVERT(real, onepluschild ) as  "1PlusChildHouseholds" ,
		CONVERT(real, zerochild ) as "0ChildHouseholds"  ,
		CONVERT(real, hhsizeone ) as  "1PersonHouseholds" ,
		CONVERT(real, hhsizetwo ) as   "2PersonHouseholds",
		CONVERT(real, hhsizethree ) as  "3PersonHouseholds" ,
		CONVERT(real, hhsizefourplus ) as  "4PlusPersonHouseholds" ,
		CONVERT(real, income0to30k ) as  income0to30k ,
		CONVERT(real, income30to60k ) as   income30to60k,
		CONVERT(real, income60to100k ) as   income60to100k,
		CONVERT(real, income100to150k ) as  income100to150k ,
		CONVERT(real, income150kplus ) as  income150kplus ,
		CONVERT(real, singlefam ) as  singleFamily ,
		CONVERT(real, multifam ) as  multiFamily ,
		CONVERT(real, mobile ) as  mobileHome ,
		CONVERT(real, zeroworkers ) as  "0WorkerHouseholds" ,
		CONVERT(real, oneworker ) as  "1WorkerHouseholds" ,
		CONVERT(real, twoworkers ) as   "2WorkerHouseholds",
		CONVERT(real, threeplusoworkers ) as  "3PlusWorkerHouseholds" ,
		CONVERT(real, gqtotal ) as  totalGroupQuarter ,
		CONVERT(real, gqinst ) as  gqInst ,
		CONVERT(real, gqnoninstcoll ) as gqNonInstCollege  ,
		CONVERT(real, gqnoninstmil ) as gqNonInstMilitary  ,
		CONVERT(real, gqnoninstother ) as   gqNonInstOther
		FROM f) f
		UNPIVOT (value for category in (totalPop, MALE, FEMALE, 
		AGE0TO17 ,AGE18TO24 ,  AGE25TO34 ,  AGE35TO49 , AGE50TO64 ,  AGE65TO79  , AGE80PLUS,
		WHITE , BLACK  , NINDIAN , ASIAN , ISLANDER , RACEOTHER  , MIXED ,  HISPANIC ,
		totalHouseholds , "1PlusChildHouseholds" ,"0ChildHouseholds"  , "1PersonHouseholds" , "2PersonHouseholds", "3PersonHouseholds" , "4PlusPersonHouseholds" ,
		income0to30k , income30to60k,  income60to100k, income100to150k ,  income150kplus ,
		singleFamily , multiFamily , mobileHome , "0WorkerHouseholds" ,"1WorkerHouseholds" ,  "2WorkerHouseholds", "3PlusWorkerHouseholds" ,
		totalGroupQuarter , gqInst , gqNonInstCollege  , gqNonInstMilitary  ,  gqNonInstOther
		  )) as tblPivot)
		  
		  INSERT INTO [popsyn].[dbo].[target]
		([category], [geo_type_id], [geo_zone], [year], [value], [lu_revision_no], [lu_major_version]
		 ,[lu_minor_version])
		 
		 SELECT c.id, 13, g.geo_zone, @tYear, g.value, 1,1,0
		  from g join popsyn.dbo.target_category c
		on g.category = c.name

END