USE [popsyn]
GO

-- ================================================================================
-- Author:		Ziying Ouyang
-- Create date: 1/12/2011
-- Description:	sum up social demographic information from 2000 census sf1, sf3, ctpp
-- by census block, block group and CTPP zone to PUMA
-- the final table is census_validation
-- modified: 11/18/2011 for three data groups, changed the sequence of tables to join.
--						started with geography_zone by type since it has full list of records 
--						to avoid any potential census tables not contain the full list of geo records.

-- ==================================================================================


ALTER PROCEDURE [dbo].[proc_census_validation]
AS
BEGIN
	--Census Block
	IF OBJECT_ID(N'dbo.#puma_hh_pop',N'U') is not null 
	DROP TABLE dbo.#puma_hh_pop;

	SELECT
		
		x.zone as puma
		,SUM(p026001)  AS hhs
		,SUM(p026002)  AS famHhs
		,SUM(p026009)  AS nonFamHhs
		,SUM(p020002)  AS hhsbyHolderAge15to64
		,SUM(p020017)  AS hhsbyHolderAge65Plus
		,SUM(p026010)  AS hh1Person
		,SUM(p026003+p026011)  AS hh2Persons
		,SUM(p026004+p026012)  AS hh3Persons
		,SUM(p026005+p026006+p026007+p026008+p026013+p026014+p026015+p026016)  AS hh4PlusPersons
		,SUM(p020003)  AS famHhsbyHolderAge15to64
		,SUM(p020018)  AS famHhsbyHolderAge65Plus
		,SUM(p020014)  AS nonFamHhsbyHolderAge15to64
		,SUM(p020029)  AS nonFamHhsbyHolderAge65Plus
		,SUM(p020005+p020009+p020012+p020020+p020024+p020027)  AS famHhsOwnChildUnder18
		,SUM(p020006+p020010+p020013+p020021+p020025+p020028)  AS famHhsNoOwnChildUnder18
		,SUM(p019003)  AS famHhsAge18UnderPres
		,SUM(p019012)  AS famHhsNoAge18UnderPres
		,SUM(p019008)  AS nonFamHhsAge18UnderPres
		,SUM(p019017)  AS nonFamHhsNoAge18UnderPres
		,SUM(p023002)  AS hhs1PlusAge65Plus
		,SUM(p023007)  AS hhsNoAge65Plus
		,SUM(p023003)  AS hhs1PersonAge65Plus
		,SUM(p023005)  AS fam2PlusPersons1PlusAge65Plus
		,SUM(p023006)  AS nonFam2PlusPersons1PlusAge65Plus
		,SUM(p023008)  AS hhs1PersonAge65Under
		,SUM(p023010)  AS fam2PlusPersonsNoAge65Plus
		,SUM(p023011)  AS nonFam2PlusPersonsNoAge65Plus
		,SUM(p027002)  AS numPersonsHh
		,SUM(p027003)  AS numPersonsFamilyHh
		,SUM(p027016)  AS numPersonsNonFamilyHh
		,SUM(p027004)  AS numOfPersonsAsHolders
		,SUM(p027007)  AS numOfPersonsAsSpouse
		,SUM(p027008)  AS numOfPersonsAsChild
		,SUM(p027011+p027012+p027013+p027014)  AS numOfPersonsAsOthers
		,SUM(p027015)  AS numOfPersonsNotRelatedToHolder
		,SUM(p027001)  AS numOfPersonsGQIncluded
		,SUM(p012002)  AS male
		,SUM(p012026)  AS female
		,SUM(p014003+p014004+p014005+p014006+p014007+p014008+p014024+p014025+p014026+p014027+p014028+p014029)  AS age0to5
		,SUM(p014009+p014010+p014011+p014012+p014013+p014014+p014030+p014031+p014032+p014033+p014034+p014035)  AS age6to11
		,SUM(p014015+p014016+p014017+p014018+p014036+p014037+p014038+p014039)  AS age12to15
		,SUM(p014019+p014020+p014040+p014041)  AS age16to17
		,SUM(p012007+p012008+p012009+p012010+p012031+p012032+p012033+p012034)  AS age18to24
		,SUM(p012011+p012012+p012035+p012036)  AS age25to34
		,SUM(p012013+p012014+p012015+p012037+p012038+p012039)  AS age35to49
		,SUM(p012016+p012017+p012018+p012019+p012040+p012041+p012042+p012043)  AS age50to64
		,SUM(p012020+p012021+p012022+p012023+p012044+p012045+p012046+p012047)  AS age65to79
		,SUM(p012024+p012025+p012048+p012049)  AS age80Plus
		,SUM(p012002-p012003+P012026-P012027+P014006+P014007+P014027+P014028)  AS age3Plus
		,SUM(p014002-p014021-p014022+p014023-p014042-p014043)  AS age17AndUnder
		,SUM(P012001-P012003-P012004-P012005-P012006-P012027-P012028-P012029-p012030+P014019+P014020+P014040+P014041) as age16Plus
		,SUM(P012001-P012003-P012004-P012005-P012006-P012020-P012021-P012022-P012023-P012024-P012025-P012027-P012028-P012029-P012030-P012044-P012045-P012046-P012047-P012048-P012049) AS age18to64
		,SUM(p012020+p012021+p012022+p012023+p012044+p012045+p012046+p012047+p012024+p012025+p012048+p012049)  AS age65Plus
		,SUM(p008010)  AS hispanic
		,SUM(p008003)  AS whiteAlone
		,SUM(p008004)  AS blackAlone
		,SUM(p008005)  AS indiAlaskaAlone
		,SUM(p008006)  AS asianAlone
		,SUM(p008007)  AS islanderAlone
		,SUM(p008008+p008009)  AS otherRaceAloneOrTwoPlusRaces
	--	,SUM(p037008) AS occp_miltary
	INTO dbo.#puma_hh_pop
	FROM
		data_cafe.dbo.geography_zone g LEFT JOIN	
		census.dbo.sf1p7sd_2000 t7 on g.zone = t7.BLOCK and g.geo_type_id = 39 LEFT JOIN
		census.dbo.sf1p8sd_2000 t8 ON t7.BLOCK = t8.BLOCK LEFT JOIN
		census.dbo.sf1p12sd_2000 t12 ON t8.BLOCK = t12.BLOCK LEFT JOIN 
		census.dbo.sf1p14sd_2000 t14 ON t12.BLOCK = t14.BLOCK LEFT JOIN
		census.dbo.sf1p19sd_2000 t19 ON t14.BLOCK = t19.BLOCK LEFT JOIN
		census.dbo.sf1p20sd_2000 t20 ON t19.BLOCK = t20.BLOCK LEFT JOIN
		census.dbo.sf1p23sd_2000 t23 ON t20.BLOCK = t23.BLOCK LEFT JOIN
		census.dbo.sf1p26sd_2000 t26 ON t23.BLOCK = t26.BLOCK LEFT JOIN
		census.dbo.sf1p27sd_2000 t27 ON t26.BLOCK = t27.BLOCK LEFT JOIN
		census.dbo.sf1p37sd_2000 t37 ON t27.BLOCK = t37.BLOCK 		 
		join data_cafe.dbo.geography_zone x on g.centroid.STIntersects(x.shape) = 1
		and x.geo_type_id = 69
	 GROUP BY x.zone
	 
	 
	PRINT 'census block summation finished'
	
	--Census Block Group
	IF OBJECT_ID(N'dbo.#puma_income_emp',N'U') is not null 
	DROP TABLE dbo.#puma_income_emp
	
	SELECT
		x.zone AS puma	
		,SUM(p047005+p047006+p047007+p047008+p047029+p047030+p047031+p047032+p047012
		+p047013+p047014+p047015+p047036+p047037+p047038+p047039+p047019+p047020+p047021+p047022+p047043+p047044+p047045+p047046)  AS worked27plusWks
		,SUM(p047005+p047006+p047007+p047008+p047029+p047030+p047031+p047032)  AS worked27plusWks35PlusHrsPerWk
		,SUM(p047012+p047013+p047014+p047015+p047036+p047037+p047038+p047039)  AS worked27plusWks15to34HrsPerWk
		,SUM(p047019+p047020+p047021+p047022+p047043+p047044+p047045+p047046)  AS worked27plusWks1to14HrsPerWk
		,SUM(p043006+p043013)  AS civilianEmployed
		,SUM(p043004+p043011)  AS militaryEmployed
		,SUM(p043007+p043014)  AS unemployed
		,SUM(p043008+p043015)  AS notInLaborForce
		,SUM(p036003+p036006+p036009+p036012+p036015+p036026+p036029+p036032+p036035+p036038)  AS enrolledNurseyToGrade12
		,SUM(p036018+p036021+p036041+p036044)  AS enrollPostSecondary
		,SUM(p018005+p018014)  AS marriedSpousePres
		,SUM(p087002)  AS numPersonsIncomeBelowPoverty
		,SUM(p052002+p052003+p052004+p052005+p052006)  AS incomeUnder30k
		,SUM(p052007+p052008+p052009+p052010+p052011)  AS income30kto60k
		,SUM(p052012+p052013)  AS income60kto100k
		,SUM(p052014+p052015)  AS income100kto150k
		,SUM(p052016+p052017)  AS income150kPlus
		,SUM(p052002)  AS incomeUnder10k
		,SUM(p052003+p052004)  AS income10kto20k
		,SUM(p052005+p052006)  AS income20kto30k
		,SUM(p052007+p052008)  AS income30kto40k
		,SUM(p052009+p052010)  AS income40kto50k
		,SUM(p052011)  AS income50kto60k
		,SUM(p052012)  AS income60kto75k
		,SUM(p052013)  AS income75kto100k
		,SUM(p052002+p052003+p052004+p052005+p052006+p052007+p052008+p052009+p052010+p052011)  AS income60kUnder
		,SUM(p050003+p050050) AS occpMng
		,SUM(p050023+p050070) AS occpService
		,SUM(p050031+p050078) AS occpSales
		,SUM(p050034+p050035+p050081+p050082) AS occpConstruct
		,SUM(p050041+p050088) AS occpTransport
		INTO #puma_income_emp
	FROM
	     data_cafe.dbo.geography_zone g LEFT JOIN 
		 census.dbo.sf3p18sd_2000  t18 ON g.zone = t18.BLKGRP and g.geo_type_id = 42 LEFT JOIN
		 census.dbo.sf3p36sd_2000 t36 ON t18.BLKGRP = t36.BLKGRP LEFT JOIN
		 census.dbo.sf3p43sd_2000 t43 ON t36.BLKGRP = t43.BLKGRP LEFT JOIN
		 census.dbo.sf3p87sd_2000 t87 ON t43.BLKGRP = t87.BLKGRP LEFT JOIN
		 census.dbo.sf3p52sd_2000 t52 ON t87.BLKGRP = t52.BLKGRP LEFT JOIN
		 census.dbo.sf3p47sd_2000 t47 ON t52.BLKGRP = t47.BLKGRP LEFT JOIN
		 census.dbo.sf3p50sd_2000 t50 ON t50.BLKGRP = t47.BLKGRP 		
		 join data_cafe.dbo.geography_zone x on g.centroid.STIntersects(x.shape) = 1
		 and x.geo_type_id = 69
		 GROUP BY x.zone
		
	 
	 PRINT 'census block group summation finished'
	 
	 --CTPP TAZ attributes
	IF OBJECT_ID(N'dbo.#ctpp_wker_dweltype',N'U') is not null 
	DROP TABLE dbo.#ctpp_wker_dweltype;
	 
	--cross reference between PUMA and CTPP
	IF OBJECT_ID(N'dbo.#xref_puma_ctpp') is not null 
	DROP TABLE dbo.#xref_puma_ctpp;
	
	SELECT *
	INTO dbo.#xref_puma_ctpp
	FROM  data_cafe.dbo.getxref(69, 57)
	INSERT INTO dbo.#xref_puma_ctpp
	VALUES (8112, 69, 4, 57),	  
		   (8101, 69, 117,57)
	 
	SELECT
		x.pzone AS puma
		,SUM(TAB62X2)  AS NumOfWorkers0
		,SUM(TAB62X3)  AS NumOfWorkers1
		,SUM(TAB62X4)  AS NumOfWorkers2
		,SUM(TAB62X5+TAB62X6)  AS NumOfWorkers3Plus
		,SUM(TAB69X2+TAB69X3)  AS singleFamily
		,SUM(TAB69X4+TAB69X5)  AS multiUnit
		,SUM(TAB69X6)  AS mobileHome
		,SUM(TAB78X2+TAB78X3)  AS numOfOwned
		,SUM(TAB78X4+TAB78X5)  AS numOfRent
		--2 way cross tab
		,SUM(TAB75X78) AS hh1per0wkr
		,SUM(TAB75X89) AS hh1per1wkr
		,SUM(TAB75X144) AS hh2per0wkr
		,SUM(TAB75X155) AS hh2per1wkr
		,SUM(TAB75X166) AS hh2per2wkr
		,SUM(TAB75X210) AS hh3per0wkr
		,SUM(TAB75X221) AS hh3per1wkr
		,SUM(TAB75X232) AS hh3per2wkr
		,SUM(TAB75X243) AS hh3per3wkr
		,SUM(TAB75X276) AS hh4plusper0wkr
		,SUM(TAB75X287) AS hh4plusper1wkr
		,SUM(TAB75X298) AS hh4plusper2wkr
		,SUM(TAB75X309+TAB75X320) AS hh4plusper3wkr
		,SUM(TAB75X68+TAB75X69+TAB75X70) AS hh1perHhinc0to30k
		,SUM(TAB75X71+TAB75X72+TAB75X73) AS hh1perHhinc30kto60k
		,SUM(TAB75X74+TAB75X75) AS hh1perHhinc60kto100k
		,SUM(TAB75X76+TAB75X77) AS hh1perHhinc100kplus
		,SUM(TAB75X134+TAB75X135+TAB75X136) AS hh2perHhinc0to30k
		,SUM(TAB75X137+TAB75X138+TAB75X139) AS hh2perHhinc30kto60k
		,SUM(TAB75X140+TAB75X141) AS hh2perHhinc60kto100k
		,SUM(TAB75X142+TAB75X143) AS hh2perHhinc100kplus
		,SUM(TAB75X200+TAB75X201+TAB75X202) AS hh3perHhinc0to30k
		,SUM(TAB75X203+TAB75X204+TAB75X205) AS hh3perHhinc30kto60k
		,SUM(TAB75X206+TAB75X207) AS hh3perHhinc60kto100k
		,SUM(TAB75X208+TAB75X209) AS hh3perHhinc100kplus
		,SUM(TAB75X266+TAB75X267+TAB75X268) AS hh4plusperHhinc0to30k
		,SUM(TAB75X269+TAB75X270+TAB75X271) AS hh4plusperHhinc30kto60k
		,SUM(TAB75X272+TAB75X273) AS hh4plusperHhinc60kto100k
		,SUM(TAB75X274+TAB75X275) AS hh4plusperHhinc100kplus
		,SUM(TAB75X13+TAB75X14+TAB75X15) AS hh0wkrHhinc0kto30k
		,SUM(TAB75X16+TAB75X17+TAB75X18) AS hh0wkrHhinc30kto60k
		,SUM(TAB75X19+TAB75X20) AS hh0wkrHhinc60kto100k
		,SUM(TAB75X21+TAB75X22) AS hh0wkrHhinc100kPlus
		,SUM(TAB75X24+TAB75X25+TAB75X26) AS hh1wkrHhinc0kto30k
		,SUM(TAB75X27+TAB75X28+TAB75X29) AS hh1wkrHhinc30kto60k
		,SUM(TAB75X30+TAB75X31) AS hh1wkrHhinc60kto100k
		,SUM(TAB75X32+TAB75X33) AS hh1wkrHhinc100kPlus
		,SUM(TAB75X35+TAB75X36+TAB75X37) AS hh2wkrHhinc0kto30k
		,SUM(TAB75X38+TAB75X39+TAB75X40) AS hh2wkrHhinc30kto60k
		,SUM(TAB75X41+TAB75X42) AS hh2wkrHhinc60kto100k
		,SUM(TAB75X43+TAB75X44) AS hh2wkrHhinc100kPlus
		,SUM(TAB75X46+TAB75X47+TAB75X48+TAB75X57+TAB75X58+TAB75X59) AS hh3wkrHhinc0kto30k
		,SUM(TAB75X49+TAB75X50+TAB75X51+TAB75X60+TAB75X61+TAB75X62) AS hh3wkrHhinc30kto60k
		,SUM(TAB75X52+TAB75X53+TAB75X63+TAB75X64) AS hh3wkrHhinc60kto100k
		,SUM(TAB75X54+TAB75X55+TAB75X65+TAB75X66) AS hh3wkrHhinc100kPlus

		
	INTO dbo.#ctpp_wker_dweltype
	FROM  
	    data_cafe.dbo.geography_zone g LEFT JOIN 
		census.dbo.ctpp62sd_2000 t62 ON g.zone = t62.CTPPTAZ and g.geo_type_id = 57 LEFT JOIN
		census.dbo.ctpp69sd_2000 t69 ON t62.CTPPTAZ = t69.CTPPTAZ LEFT JOIN 
		census.dbo.ctpp78sd_2000 t78 ON t69.CTPPTAZ = t78.CTPPTAZ LEFT JOIN 
		census.dbo.ctpp75sd_2000 t75 ON t69.CTPPTAZ = t75.CTPPTAZ  
		JOIN 
		#xref_puma_ctpp x on x.cZone =  t62.CTPPTAZ 		 
		 GROUP BY x.pzone
	
	PRINT 'ctpp zone summation finished'
	   
	--final summary table
	IF OBJECT_ID(N'popsyn.dbo.census_validation',N'U') is not null 
	DROP TABLE popsyn.dbo.census_validation;
 
	select 
			p1.puma
			,hhs, famHhs, nonFamHhs, hhsbyHolderAge15to64, hhsbyHolderAge65Plus, hh1Person, hh2Persons 
			,hh3Persons, hh4PlusPersons, famHhsbyHolderAge15to64, famHhsbyHolderAge65Plus, nonFamHhsbyHolderAge15to64
			,nonFamHhsbyHolderAge65Plus, famHhsOwnChildUnder18, famHhsNoOwnChildUnder18, famHhsAge18UnderPres
			,famHhsNoAge18UnderPres, nonFamHhsAge18UnderPres, nonFamHhsNoAge18UnderPres, hhs1PlusAge65Plus
			,hhsNoAge65Plus, hhs1PersonAge65Plus, fam2PlusPersons1PlusAge65Plus, nonFam2PlusPersons1PlusAge65Plus		
			,hhs1PersonAge65Under, fam2PlusPersonsNoAge65Plus, nonFam2PlusPersonsNoAge65Plus 
			,numOfWorkers0, numOfWorkers1, numOfWorkers2, numOfWorkers3Plus
			,incomeUnder30k, income30kto60k, income60kto100k, income100kto150k, income150kPlus, incomeUnder10k 
			,income10kto20k, income20kto30k, income30kto40k, income40kto50k, income50kto60k, income60kto75k
			,income75kto100k, income60kUnder
			,singleFamily, multiUnit, mobileHome, numOfOwned, numOfRent
			,hh1per0wkr ,hh1per1wkr
			,hh2per0wkr	,hh2per1wkr	,hh2per2wkr
			,hh3per0wkr	,hh3per1wkr	,hh3per2wkr	,hh3per3wkr
			,hh4plusper0wkr	,hh4plusper1wkr	,hh4plusper2wkr	,hh4plusper3wkr
			,hh1perHhinc0to30k ,hh1perHhinc30kto60k ,hh1perHhinc60kto100k ,hh1perHhinc100kplus
			,hh2perHhinc0to30k ,hh2perHhinc30kto60k ,hh2perHhinc60kto100k ,hh2perHhinc100kplus
			,hh3perHhinc0to30k ,hh3perHhinc30kto60k	,hh3perHhinc60kto100k ,hh3perHhinc100kPlus
			,hh4plusperHhinc0to30k ,hh4plusperHhinc30kto60k ,hh4plusperHhinc60kto100k ,hh4plusperHhinc100kplus
			,hh0wkrHhinc0kto30k ,hh0wkrHhinc30kto60k ,hh0wkrHhinc60kto100k ,hh0wkrHhinc100kPlus
			,hh1wkrHhinc0kto30k	,hh1wkrHhinc30kto60k ,hh1wkrHhinc60kto100k ,hh1wkrHhinc100kPlus
			,hh2wkrHhinc0kto30k	,hh2wkrHhinc30kto60k ,hh2wkrHhinc60kto100k ,hh2wkrHhinc100kPlus
			,hh3wkrHhinc0kto30k	,hh3wkrHhinc30kto60k ,hh3wkrHhinc60kto100k ,hh3wkrHhinc100kPlus
			,numPersonsHh, numPersonsFamilyHh, numPersonsNonFamilyHh, numPersonsIncomeBelowPoverty
			,numOfPersonsAsHolders, numOfPersonsAsSpouse
			,numOfPersonsAsChild, numOfPersonsAsOthers, numOfPersonsNotRelatedToHolder, numOfPersonsGQIncluded
			,male, female, age0to5, age6to11, age12to15, age16to17, age18to24, age25to34, age35to49, age50to64
			,age65to79, age80Plus, age17AndUnder, age18to64, age65Plus, marriedSpousePres
			,hispanic, whiteAlone, blackAlone, indiAlaskaAlone, asianAlone, islanderAlone, otherRaceAloneOrTwoPlusRaces
			,worked27plusWks, worked27plusWks35PlusHrsPerWk, worked27plusWks15to34HrsPerWk 
			,worked27plusWks1to14HrsPerWk 
			,age16Plus, civilianEmployed, militaryEmployed, unemployed, notInLaborForce 
			,occpMng,occpService,occpSales,occpConstruct,occpTransport
			--,occp_miltary
			,age3Plus, enrolledNurseyToGrade12, enrollPostSecondary 
	INTO popsyn.dbo.census_validation
	FROM
		dbo.#puma_income_emp p1 JOIN
		dbo.#puma_hh_pop p2 ON
		p1.puma = p2.puma JOIN
		dbo.#ctpp_wker_dweltype p3 ON
		p2.puma = p3.puma
	
	ALTER TABLE dbo.census_validation ADD CONSTRAINT pk_census_validation
	PRIMARY KEY CLUSTERED (puma);
		
	PRINT 'final table created'
		
	DROP TABLE dbo.#puma_income_emp
	DROP TABLE dbo.#puma_hh_pop
	DROP TABLE dbo.#ctpp_wker_dweltype
	
END