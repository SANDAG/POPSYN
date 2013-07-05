USE [popsyn]
GO

-- ================================================================================
-- Author:		Ziying Ouyang
-- Create date: 1/12/2011
-- Description:	sum up social demographic information from ACS SF
-- by block group and tract to PUMA
-- the final table is ACS_validation
-- Dec 8, 2011 modified to parameterize county fip code and summary level
-- ==================================================================================


ALTER PROCEDURE [dbo].[proc_acs_validation]
(@countyfip int,
@sumlevelct int,
@sumlevelblkgp int
)
AS
BEGIN
		
	--final summary table
	IF OBJECT_ID(N'popsyn.dbo.acs_validation',N'U') is not null 
	DROP TABLE popsyn.dbo.acs_validation;		
	
   with x as (select pzone,czone from data_cafe.dbo.getxref(69,42)), --puma and blkgrp
	x_ct as (select pzone,czone from data_cafe.dbo.getxref(69,1)), --puma and ct
   
	hhs as (	
		select x.pzone AS puma, sum(b11001_001) as hhs, sum(b11001_002) as famHhs, sum(b11001_007) as nonFamHhs
		from acs.dbo.sf_b11001 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		join x on x.cZone = g.tract*10+g.blkgrp		
		where county = @countyfip and sumlevel = @sumlevelblkgp  
		group by x.pzone
	),
	
	hhsize as (
		select x.pzone AS puma, sum(b11016_010) as hh1Person, sum(B11016_003+B11016_011) as hh2Persons, 
			   SUM(B11016_004+B11016_012) as hh3Persons, sum(B11016_005+B11016_006+B11016_007+B11016_008+B11016_013+B11016_014+B11016_015+B11016_016)
				as hh4PlusPersons
		from acs.dbo.sf_b11016 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		  
		join x on x.cZone = g.tract*10+g.blkgrp	
		where county = @countyfip and sumlevel = @sumlevelblkgp  		  
		group by x.pzone
    ),
    
    
    nonfambyage as(
		select x_ct.pzone AS puma, 
			SUM(B11010_004+B11010_007+B11010_011+B11010_014) as nonFamHhsbyHolderAge15to64,
			SUM(B11010_005+B11010_008+B11010_012+B11010_015 )AS nonFamHhsbyHolderAge65Plus
		from acs.dbo.sf_b11010 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on t1.logrecno = g.logrecno
		join x_ct on g.tract=x_ct.czone
		where county = @countyfip and sumlevel = @sumlevelct  		  
		group by x_ct.pzone	
	),
	
	fambyownchild as(
		select x.pzone AS puma,
			SUM(B23007_002) AS famHhsOwnChildUnder18, 
			SUM(B23007_031) AS famHhsNoOwnChildUnder18
		FROM acs.dbo.sf_b23007 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno	
		join x on x.cZone = g.tract*10+g.blkgrp		
		where county = @countyfip and sumlevel = @sumlevelblkgp  	  
		group by x.pzone
	      ),
	
	hhsbyminors as(
		select x.pzone AS puma,
			SUM(B11005_003) AS famHhsAge18UnderPres, 
			SUM(B11005_012) AS famHhsNoAge18UnderPres, 
			SUM(B11005_008) AS nonFamHhsAge18UnderPres, 
			SUM(B11005_017) AS nonFamHhsNoAge18UnderPres
		FROM acs.dbo.sf_b11005 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		join x on x.cZone = g.tract*10+g.blkgrp		
		where county = @countyfip and sumlevel = @sumlevelblkgp  	  
	    group by x.pzone
	      ),

	hhsizebyseniors as(
		select x.pzone AS puma,SUM(B11007_002) AS hhs1PlusAge65Plus, 
			SUM(B11007_007) AS hhsNoAge65Plus, SUM(B11007_003) AS hhs1PersonAge65Plus, 
			SUM(B11007_005) AS fam2PlusPersons1PlusAge65Plus, SUM(B11007_006) AS nonFam2PlusPersons1PlusAge65Plus,
			SUM(B11007_008) AS hhs1PersonAge65Under, SUM(B11007_010) AS fam2PlusPersonsNoAge65Plus,
			SUM(B11007_011) AS nonFam2PlusPersonsNoAge65Plus
		FROM acs.dbo.sf_b11007 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		join x on x.cZone = g.tract*10+g.blkgrp		
		where county = @countyfip and sumlevel = @sumlevelblkgp  	  
	      group by x.pzone
	      ),
    
	hhsbyworkers as(
		select x_ct.pzone AS puma,SUM(B08202_002) AS numOfWorkers0, 
			SUM(B08202_003) AS numOfWorkers1, SUM(B08202_004) AS numOfWorkers2, 
			SUM(B08202_005) AS numOfWorkers3Plus
		FROM acs.dbo.sf_b08202 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on t1.logrecno = g.logrecno
		join x_ct on g.tract=x_ct.czone
		where  county = @countyfip and sumlevel = @sumlevelct		  
		group by x_ct.pzone	
	      ),

	hhincome as(
		select x.pzone AS puma,SUM(B19001_002+B19001_003+B19001_004+B19001_005+B19001_006) AS incomeUnder30k, 
			SUM(B19001_007+B19001_008+B19001_009+B19001_010+B19001_011) AS income30kto60k, 
			SUM(B19001_012+B19001_013) AS income60kto100k, 
			SUM(B19001_014+B19001_015) AS income100kto150k, 
			SUM(B19001_016+B19001_017) AS income150kPlus,
			SUM(B19001_002) AS incomeUnder10k, SUM(B19001_003+B19001_004) AS income10kto20k,
			SUM(B19001_005+B19001_006) AS income20kto30k,
			SUM(B19001_007+B19001_008) AS income30kto40k,
			SUM(B19001_009+B19001_010) AS income40kto50k,
			SUM(B19001_011) AS income50kto60k,
			SUM(B19001_012) AS income60kto75k,
			SUM(B19001_013) AS income75kto100k,
			SUM(B19001_002+B19001_003+B19001_004+B19001_005+B19001_006+B19001_007+B19001_008+B19001_009+B19001_010+B19001_011)AS income60kUnder
		FROM acs.dbo.sf_b19001 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		join x on x.cZone = g.tract*10+g.blkgrp		
		where county = @countyfip and sumlevel = @sumlevelblkgp  	  
		group by x.pzone
		  ),
	
	dwelltype as (
		select x_ct.pzone as puma
		,SUM(B11011_004+B11011_009+B11011_013+B11011_017) as singleFamily
		,SUM(b11011_005+B11011_010+B11011_014+B11011_018) as multiUnit
		,SUM(B11011_006+B11011_011+B11011_015+B11011_019) as mobileHome

		from acs.dbo.sf_b11011 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on t1.logrecno = g.logrecno
	    join x_ct on g.tract=x_ct.czone
	    where county = @countyfip and sumlevel = @sumlevelct  		  
	    group by x_ct.pzone	
			),
	
	tenure as (	
		select x.pzone as puma
			,SUM(B11012_004+B11012_008+B11012_011+B11012_014) AS numOfOwned
			,SUM(B11012_005+B11012_009+B11012_012+B11012_015) AS numOfRent
		from acs.dbo.sf_B11012 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		join x on x.cZone = g.tract*10+g.blkgrp	
		where county = @countyfip and sumlevel = @sumlevelblkgp  			  
		group by x.pzone

	),
	
	hhbyworker as (
		select x_ct.pzone as puma
			,SUM(B08202_007) AS hh1per0wkr
			,SUM(B08202_008) AS hh1per1wkr
			,SUM(B08202_010) AS hh2per0wkr
			,SUM(B08202_011) AS hh2per1wkr
			,SUM(B08202_012) AS hh2per2wkr
			,SUM(B08202_014) AS hh3per0wkr
			,SUM(B08202_015) AS hh3per1wkr
			,SUM(B08202_016) AS hh3per2wkr
			,SUM(B08202_017) AS hh3per3wkr
			,SUM(B08202_019) AS hh4plusper0wkr
			,SUM(B08202_020) AS hh4plusper1wkr
			,SUM(B08202_021) AS hh4plusper2wkr
			,SUM(B08202_022) AS hh4plusper3wkr
		 from acs.dbo.sf_b08202 t1
		 join acs.dbo.acs_2009_5yr_ca_geo g on t1.logrecno = g.logrecno
		 join x_ct on g.tract=x_ct.czone
		 where county = @countyfip and sumlevel = @sumlevelct  	  
		 group by x_ct.pzone
	),
	
	ppinHh as (
		SELECT x_ct.pzone as puma
			,SUM(B09016_002) as numPersonsHh
			,SUM(B09016_003) AS numPersonsFamilyHh
			,SUM(B09016_019) AS numPersonsNonFamilyHh			
		from acs.dbo.sf_B09016 t1	
		join acs.dbo.acs_2009_5yr_ca_geo g on t1.logrecno = g.logrecno
		join x_ct on g.tract=x_ct.czone
		where county = 73 and sumlevel = 140		  
		group by x_ct.pzone			
		),

		ppPoverty as(
			SELECT x_ct.pzone as puma
			,SUM(B17001_002) AS numPersonsIncomeBelowPoverty
			FROM acs.dbo.sf_b17001 t1
			join acs.dbo.acs_2009_5yr_ca_geo g on t1.logrecno = g.logrecno
			join x_ct on g.tract=x_ct.czone
			where county = @countyfip and sumlevel = @sumlevelct  		  
			group by x_ct.pzone			

		),
		
	ppinFam as (	
		SELECT x_ct.pzone as puma
			,SUM(B09016_004) AS numOfPersonsAsHolders
			,SUM(B09016_007) AS numOfPersonsAsSpouse
			,SUM(B09016_008) AS numOfPersonsAsChild
			,SUM(B09016_009+B09016_010+B09016_011+B09016_012) AS numOfPersonsAsOthers
			,SUM(B09016_013) AS numOfPersonsNotRelatedToHolder
			,SUM(B09016_001) as numOfPersonsGQIncluded
		FROM acs.dbo.sf_B09016 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on t1.logrecno = g.logrecno
		join x_ct on g.tract=x_ct.czone
		where county = @countyfip and sumlevel = @sumlevelct  
		GROUP BY x_ct.pzone
		),

	page_sex as(
		SELECT
			x.pzone AS puma	
			,sum(b01001_002) as male
			,sum(b01001_026) as female
			,sum(b01001_007+b01001_008+b01001_009+b01001_010+b01001_031+b01001_032+b01001_033+b01001_034) as age18to24
			,sum(b01001_011+b01001_012+b01001_035+b01001_036) as age25to34 
			,sum(b01001_013+b01001_014+b01001_015+b01001_037+b01001_038+b01001_039) as age35to49
			,sum(b01001_016+b01001_017+b01001_018+b01001_019+b01001_040+b01001_041+b01001_042+b01001_043) as age50to64
			,sum(b01001_020+b01001_021+b01001_022+b01001_023+b01001_044+b01001_045+b01001_046+b01001_047) as age65to79
			,sum(b01001_024+b01001_025+b01001_048+b01001_049) as age80plus
			,sum(b01001_003+b01001_004+b01001_005+b01001_006+b01001_027+b01001_028+b01001_029+b01001_030) as age17AndUnder
			,sum(b01001_007+b01001_008+b01001_009+b01001_010+b01001_011+b01001_012+b01001_013+b01001_014+b01001_015+b01001_016+b01001_017+b01001_018
			+b01001_019+b01001_031+b01001_032+b01001_033+b01001_034+b01001_035+b01001_036+b01001_037+b01001_038+b01001_039+b01001_040+b01001_041+b01001_042+b01001_043
			) as age18to64
			,sum(b01001_020+b01001_021+b01001_022+b01001_023+b01001_044+b01001_045+b01001_046+b01001_047+b01001_024+b01001_025+b01001_048+b01001_049) as age65plus
		FROM  acs.dbo.sf_b01001  t1 
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		join x on x.cZone = g.tract*10+g.blkgrp	
		where county = @countyfip and sumlevel = @sumlevelblkgp  	
		group by x.pzone
		),
		
	prace as (
		SELECT
			x.pzone AS puma	
			,sum(B03002_012) as hispanic
			,sum(b03002_003) as whiteAlone
			,SUM(b03002_004) AS blackAlone
			,SUM(b03002_005) AS indiAlaskaAlone
			,SUM(b03002_006) AS asianAlone
			,SUM(b03002_007) AS islanderAlone
			,SUM(B03002_008 +B03002_009) AS otherRaceAloneOrTwoPlusRaces			
		FROM acs.dbo.sf_b03002 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		join x on x.cZone = g.tract*10+g.blkgrp		
		where county = @countyfip and sumlevel = @sumlevelblkgp  
		group by x.pzone		
		),
		
	education as (
		 select x.pzone AS puma,SUM(B14002_001) AS age3Plus, 
			  SUM(B14002_004+B14002_007+B14002_010+B14002_013+B14002_016+B14002_028+B14002_031+B14002_034+B14002_037+B14002_040) AS enrolledNurseyToGrade12, 
			  SUM(B14002_019+B14002_022+B14002_043+B14002_046) AS enrollPostSecondary
		 FROM acs.dbo.sf_b14002 t1
		 join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		 join x on x.cZone = g.tract*10+g.blkgrp	
		 where county = @countyfip and sumlevel = @sumlevelblkgp  	           
		 group by x.pzone
        ),
       
      employment as (
          select x_ct.pzone AS puma, SUM(B23001_001) AS age16Plus, 
			  SUM(B23001_007+B23001_014+B23001_021+B23001_028+B23001_035+B23001_042+B23001_049+B23001_056+B23001_063+B23001_070+B23001_075+B23001_080+B23001_085+B23001_093+B23001_100+B23001_107+B23001_114+B23001_121+B23001_128+B23001_135+B23001_142+B23001_149+B23001_156+B23001_161+B23001_166+B23001_171) AS civilianEmployed, 
			  SUM(B23001_005+B23001_012+B23001_019+B23001_026+B23001_033+B23001_040+B23001_047+B23001_054+B23001_061+B23001_068+B23001_091+B23001_098+B23001_105+B23001_112+B23001_119+B23001_126+B23001_133+B23001_140+B23001_147+B23001_154) AS militaryEmployed,
			  SUM(B23001_008+B23001_015+B23001_022+B23001_029+B23001_036+B23001_043+B23001_050+B23001_057+B23001_064+B23001_071+B23001_076+B23001_081+B23001_086+B23001_094+B23001_101+B23001_108+B23001_115+B23001_122+B23001_129+B23001_136+B23001_143+B23001_150+B23001_157+B23001_162+B23001_167+B23001_172) AS unemployed,
			  SUM(B23001_009+B23001_016+B23001_023+B23001_030+B23001_037+B23001_044+B23001_051+B23001_058+B23001_065+B23001_072+B23001_077+B23001_082+B23001_087+B23001_095+B23001_102+B23001_109+B23001_116+B23001_123+B23001_130+B23001_137+B23001_144+B23001_151+B23001_158+B23001_163+B23001_168+B23001_173) AS notInLaborForce
          FROM acs.dbo.sf_b23001 t1
		  join acs.dbo.acs_2009_5yr_ca_geo g on t1.logrecno = g.logrecno
		  join x_ct on g.tract=x_ct.czone
          where county = @countyfip and sumlevel = @sumlevelct  
          group by x_ct.pzone
           
            ),            
      
      occp as (
		select x.pzone AS puma
			,SUM(C24010_003+C24010_040) AS occpMng
			,SUM(C24010_018+C24010_055) AS occpService
			,SUM(C24010_026+C24010_063) AS occpSales
			,SUM(C24010_029+C24010_030+C24010_066+C24010_067) AS occpConstruct
			,SUM(C24010_033+C24010_072) AS occpTransport
		
		from acs.dbo.sf_c24010 t1
		join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
		join x on x.cZone = g.tract*10+g.blkgrp	
		where county = @countyfip and sumlevel = @sumlevelblkgp  		
		group by x.pZone	  
      ),
      hoursworked as(
            select x.pzone AS puma,
				SUM(b23022_003+b23022_027-B23022_009-B23022_010-B23022_016-B23022_017-B23022_023-B23022_024-B23022_033-B23022_034-B23022_040-B23022_041-B23022_047-B23022_048) AS worked27plusWks, 
				SUM(B23022_005+B23022_006+B23022_007+B23022_008+B23022_029+B23022_030+B23022_031+B23022_032) AS worked27plusWks35PlusHrsPerWk, 
						SUM(B23022_012+B23022_013+B23022_014+B23022_015+B23022_036+B23022_037+B23022_038+B23022_040) AS worked27plusWks15to34HrsPerWk,
				SUM(B23022_019+B23022_020+B23022_021+B23022_022+B23022_043+B23022_044+B23022_045+B23022_046) AS worked27plusWks1to14HrsPerWk
		
			FROM acs.dbo.sf_b23022 t1
			join acs.dbo.acs_2009_5yr_ca_geo g on g.logrecno = t1.logrecno		 	  
			join x on x.cZone = g.tract*10+g.blkgrp		 
			where county = @countyfip and sumlevel = @sumlevelblkgp             
			group by x.pzone)	
				
		select 
			z.zone as puma,hhs,famHhs,nonFamHhs,hh1Person,hh2Persons,hh3Persons,hh4plusPersons,nonFamHhsbyHolderAge15to64,
			nonFamHhsbyHolderAge65Plus,famHhsOwnChildUnder18,famHhsNoOwnChildUnder18,
			famHhsAge18UnderPres,famHhsNoAge18UnderPres,nonFamHhsAge18UnderPres,nonFamHhsNoAge18UnderPres,
			hhs1PlusAge65Plus,hhsNoAge65Plus,hhs1PersonAge65Plus, fam2PlusPersons1PlusAge65Plus,nonFam2PlusPersons1PlusAge65Plus,
			hhs1PersonAge65Under, fam2PlusPersonsNoAge65Plus,nonFam2PlusPersonsNoAge65Plus,
			numOfWorkers0, 	numOfWorkers1, numOfWorkers2, numOfWorkers3Plus,
			incomeUnder30k, income30kto60k, income60kto100k, income100kto150k, 
			income150kPlus,	incomeUnder10k, income10kto20k,
			income20kto30k,	income30kto40k,	income40kto50k,	income50kto60k,
			income60kto75k,	income75kto100k,income60kUnder,
			singleFamily,multiUnit, mobileHome,	numOfOwned,numOfRent,	
			hh1per0wkr, hh1per1wkr, hh2per0wkr, hh2per1wkr, hh2per2wkr,
			hh3per0wkr,	hh3per1wkr,	hh3per2wkr,	hh3per3wkr,	
			hh4plusper0wkr,	hh4plusper1wkr,	hh4plusper2wkr,	hh4plusper3wkr,
			numPersonsHh,numPersonsFamilyHh, numPersonsNonFamilyHh,	sum21.numPersonsIncomeBelowPoverty,			
			numOfPersonsAsHolders, numOfPersonsAsSpouse	,numOfPersonsAsChild,
			numOfPersonsAsOthers,	numOfPersonsNotRelatedToHolder,	numOfPersonsGQIncluded,
			male,female,age18to24,age25to34,age35to49,age50to64, age65to79,age80plus, age17AndUnder,age18to64,age65plus,
			hispanic, whiteAlone,blackAlone, indiAlaskaAlone,
			asianAlone,islanderAlone, otherRaceAloneOrTwoPlusRaces,
			worked27plusWks, worked27plusWks35PlusHrsPerWk,worked27plusWks15to34HrsPerWk,worked27plusWks1to14HrsPerWk,			
			age16Plus, civilianEmployed, militaryEmployed,unemployed,notInLaborForce,
			occpMng, occpService, occpSales, occpConstruct,occpTransport,
			age3Plus, enrolledNurseyToGrade12, enrollPostSecondary
			
		INTO popsyn.dbo.acs_validation
		from 
		data_cafe.dbo.geography_zone z join
		hhs sum1 on z.zone = sum1.puma
		join hhsize sum2
		on sum2.puma = z.zone
		join nonfambyage sum3
		on sum3.puma = z.zone
		join fambyownchild sum4
		on sum4.puma = z.zone
		join hhsbyminors sum5
		on sum5.puma = z.zone
		join hhsizebyseniors sum6
		on sum6.puma = z.zone
		JOIN hhsbyworkers sum7
		on sum7.puma = z.zone
		join hhincome sum8
		on sum8.puma = z.zone
		join dwelltype sum9
		on sum9.puma = z.zone
		join tenure sum10
		on sum10.puma = z.zone
		join hhbyworker sum19
		on sum19.puma = z.zone
		join ppinHh sum11
		on  sum11.puma = z.zone
		join ppPoverty sum12
		on  sum12.puma = z.zone
		join ppinFam sum13
		on  sum13.puma = z.zone
		join page_sex sum14
		on sum14.puma = z.zone
		join prace sum15
		on sum15.puma = z.zone
		JOIN education sum16
		on sum16.puma = z.zone
		join employment sum17
		on sum17.puma = z.zone
		join hoursworked sum18
		on sum18.puma = z.zone
		join occp sum20
		on sum20.puma = z.zone
		join ppPoverty sum21
		on sum21.puma = z.zone
		where z.geo_type_id = 69
		
		ALTER TABLE dbo.acs_validation ADD CONSTRAINT pk_acs_validation
	PRIMARY KEY CLUSTERED (puma);
END
