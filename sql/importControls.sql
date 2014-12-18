--Setting up MAZ, TAZ and META control tables for Nashville MPO PopSynIII
--Sriram Narayanamoorthy, narayanamoorthys@pbworld.com, 052014

--This implementation of PopSyn uses these geographies
--	MAZ  -> Nashville MPO MAZ System
--	TAZ  -> Nashville MPO TAZ System
--	META -> PUMA
--------------------------------------------------------------------
USE [NVPopSynIII]
GO

SET NOCOUNT ON;	

--Removing existing tables from previous runs
IF OBJECT_ID('dbo.control_totals_maz') IS NOT NULL 
	DROP TABLE control_totals_maz;
IF OBJECT_ID('dbo.control_totals_taz') IS NOT NULL 
	DROP TABLE control_totals_taz;
IF OBJECT_ID('dbo.control_totals_meta') IS NOT NULL 
	DROP TABLE control_totals_meta;

IF OBJECT_ID('tempdb..#tazData') IS NOT NULL 
	DROP TABLE #tazData;
IF OBJECT_ID('tempdb..#mazData') IS NOT NULL 
	DROP TABLE #mazData;
IF OBJECT_ID('tempdb..#metaData') IS NOT NULL 
	DROP TABLE #metaData;
IF OBJECT_ID('tempdb..#geographicCWalk') IS NOT NULL 
	DROP TABLE #geographicCWalk;

/*###################################################################################################*/
--									INPUT FILE LOCATIONS
/*###################################################################################################*/

DECLARE @mazData_File VARCHAR(256);
DECLARE @tazData_File VARCHAR(256);
DECLARE @geographicCWalk_File VARCHAR(256);
DECLARE @query VARCHAR(1000);

--Input files
SET @mazData_File = (SELECT filename FROM csv_filenames WHERE dsc = 'mazData_File');
SET @tazData_File = (SELECT filename FROM csv_filenames WHERE dsc = 'tazData_File');
SET @geographicCWalk_File = (SELECT filename FROM csv_filenames WHERE dsc = 'geographicCWalk_File');

/*###################################################################################################*/
--							  SETTING UP TEMPORARY TABLES FOR RAW INPUTS
/*###################################################################################################*/

--Loading MAZ data table
CREATE TABLE #mazData ([MAZ] INT
	,[SFU_10] INT
	,[MFU_10] INT
	,[MH_10] INT
	,[HH] INT
	,[GQ_NONINST] INT
	,[GQ_UNIV] INT
	,[GQ_MILITARY] INT
	,[GQ_OTHNI] INT

	CONSTRAINT [PK tempdb.mazData MAZ] PRIMARY KEY CLUSTERED (MAZ)
);
SET @query = ('BULK INSERT #mazData FROM ' + '''' + @mazData_File + '''' + ' WITH (FIELDTERMINATOR = ' + 
				''',''' + ', ROWTERMINATOR = ' + '''\n''' + ', FIRSTROW = 2, MAXERRORS = 0, TABLOCK);');
EXEC(@query);


--Loading TAZ data table
CREATE TABLE #tazData ([TAZ] INT
	,[HHSIZE_1] INT
	,[HHSIZE_2] INT
	,[HHSIZE_3] INT
	,[HHSIZE_4PLUS] INT
	,[INCOME_25K] INT
	,[INCOME_50K] INT
	,[INCOME_75K] INT
	,[INCOME_100K] INT
	,[INCOME_100KPLUS] INT
	,[WORKERS_0] INT
	,[WORKERS_1] INT
	,[WORKERS_2] INT
	,[WORKERS_3PLUS] INT
	,[AGE0TO17] INT
	,[AGE18TO24] INT
	,[AGE25TO34] INT
	,[AGE35TO49] INT
	,[AGE50TO64] INT
	,[AGE65TO79] INT
	,[AGE80PLUS] INT
	,[CHILDREN_0] INT
	,[CHILDREN_1PLUS] INT
	,[MALE] INT
	,[FEMALE] INT

	CONSTRAINT [PK tempdb.tazData TAZ] PRIMARY KEY CLUSTERED (TAZ)
);
SET @query = ('BULK INSERT #tazData FROM ' + '''' + @tazData_File + '''' + ' WITH (FIELDTERMINATOR = ' + 
				''',''' + ', ROWTERMINATOR = ' + '''\n''' + ', FIRSTROW = 2, MAXERRORS = 0, TABLOCK);');
EXEC(@query);

--Loading the geographic correspondence TAZ->TRACT->PUMA
CREATE TABLE #geographicCWalk( [MAZ] INT
	,[TAZ] INT
	,[TAZID] INT
	,[MOE_DIST1] VARCHAR(50)
	,[MOE_DIST2] VARCHAR(50)
	,[PUMA] INT
	,[REGION] INT
	CONSTRAINT [PK tempdb.geographicCWalk MAZ,TAZ,PUMA] PRIMARY KEY CLUSTERED (MAZ, TAZ, PUMA, REGION)
)
SET @query = ('BULK INSERT #geographicCWalk FROM ' + '''' + @geographicCWalk_File + '''' + ' WITH (FIELDTERMINATOR = ' + 
				''',''' + ', ROWTERMINATOR = ' + '''\n''' + ', FIRSTROW = 2, MAXERRORS = 0, TABLOCK);');
EXEC(@query);
PRINT 'Created raw tables...'

/*###################################################################################################*/
--									CREATING MAZ CONTROL TABLE
/*###################################################################################################*/
--Creating MAZ Controls
SELECT * INTO control_totals_maz
FROM #mazData

UPDATE dbo.control_totals_maz
SET  HH = ROUND(HH, 0)

ALTER TABLE control_totals_maz
	ADD GQ_NONUNIV INT
	    ,TAZ INT
		,TAZID INT
		,MOE_DIST1 VARCHAR(50)
		,MOE_DIST2 VARCHAR(50)
		,PUMA INT
		,METAGEO INT
		,REGION INT
GO

UPDATE control_totals_maz 
SET GQ_NONUNIV = GQ_NONINST - GQ_UNIV

UPDATE control_totals_maz
	SET TAZ = t1.TAZ
		,TAZID = t1.TAZID
		,MOE_DIST1 = t1.MOE_DIST1
		,MOE_DIST2 = t1.MOE_DIST2
		,PUMA = t1.PUMA
		,METAGEO = t1.PUMA
		,REGION = t1.REGION
	FROM (SELECT DISTINCT MAZ, TAZ, TAZID, MOE_DIST1, MOE_DIST2, PUMA, REGION FROM #geographicCWalk) AS t1, 
		control_totals_maz t2
	WHERE (t1.MAZ = t2.MAZ)

ALTER TABLE dbo.control_totals_maz 
	ALTER COLUMN TAZ INT NOT NULL
GO

ALTER TABLE dbo.control_totals_maz 
	ALTER COLUMN PUMA INT NOT NULL
GO

ALTER TABLE dbo.control_totals_maz 
	ALTER COLUMN REGION INT NOT NULL
GO

ALTER TABLE dbo.control_totals_maz
	ADD CONSTRAINT [PK dbo.control_totals_maz MAZ, TAZ, PUMA, REGION] 
	PRIMARY KEY (MAZ, TAZ, PUMA, REGION)
GO
PRINT 'Created MAZ controls...'

/*###################################################################################################*/
--									CREATING TAZ CONTROL TABLE
/*###################################################################################################*/
--Creating TAZ Controls
SELECT * INTO control_totals_taz
FROM #tazData

ALTER TABLE dbo.control_totals_taz
	ADD CONSTRAINT [PK dbo.control_totals_taz TAZ] 
	PRIMARY KEY (TAZ)
GO

ALTER TABLE dbo.control_totals_taz
ADD  PUMA INT
	,METAGEO INT
	,REGION INT
GO

UPDATE control_totals_taz
	SET PUMA = t1.PUMA
		,METAGEO = t1.PUMA
		,REGION = t1.REGION
	FROM (SELECT DISTINCT TAZ, PUMA, REGION FROM #geographicCWalk) AS t1, 
		control_totals_taz t2
	WHERE (t1.TAZ = t2.TAZ)
PRINT 'Created TAZ controls...'

/*###################################################################################################*/
--								CREATING META CONTROL TABLE
/*###################################################################################################*/

--Computing total population based on HHSIZE using average HHSIZE for 4-plus derived from PUMS
DECLARE @avgHHSize4p REAL;
DECLARE @totalPop INT;
DECLARE @total_NONINST_GQPop INT;
DECLARE @region INT;

SET @avgHHSize4p = (SELECT (SUM(np * wgtp/1.0) / SUM(wgtp/1.0)) FROM hhtable WHERE np >= 4)
SET @totalPop = (SELECT ROUND(SUM(HHSIZE_1 + (2 * HHSIZE_2) + (3 * HHSIZE_3) + (@avgHHSize4p * HHSIZE_4PLUS)),0) FROM control_totals_taz
					GROUP BY REGION)
SET @region = '1'

SET @total_NONINST_GQPop = (SELECT SUM(GQ_NONINST) FROM control_totals_maz)

SELECT @region AS REGION, @totalPop AS POPBASE, @total_NONINST_GQPop AS NONINST_GQPOP INTO control_totals_meta
PRINT 'Created META controls...'

----Getting the total number of workers by PUMA from the PUMS data
--SELECT PUMA
--	,CAST(SUM(CASE WHEN employed=1 THEN wgtp ELSE 0 END) AS REAL) AS PUMSWORKERS
--	,CAST(SUM(CASE WHEN occp=1 THEN wgtp ELSE 0 END) AS REAL) AS OCCP1
--	,CAST(SUM(CASE WHEN occp=2 THEN wgtp ELSE 0 END) AS REAL) AS OCCP2
--	,CAST(SUM(CASE WHEN occp=3 THEN wgtp ELSE 0 END) AS REAL) AS OCCP3
--	,CAST(SUM(CASE WHEN occp=4 THEN wgtp ELSE 0 END) AS REAL) AS OCCP4
--	,CAST(SUM(CASE WHEN occp=5 THEN wgtp ELSE 0 END) AS REAL) AS OCCP5
--	,CAST(SUM(CASE WHEN occp=6 THEN wgtp ELSE 0 END) AS REAL) AS OCCP6
--	,CAST(SUM(CASE WHEN occp=7 THEN wgtp ELSE 0 END) AS REAL) AS OCCP7
--	,CAST(SUM(CASE WHEN occp=8 THEN wgtp ELSE 0 END) AS REAL) AS OCCP8
--INTO #metaData
--FROM perstable
--GROUP BY PUMA
--ORDER BY PUMA

--ALTER TABLE #metaData
--	ADD EMPLOYMENT INT
--	 ,METAGEO INT
--	 ,pcOOCP1 REAL
--	 ,pcOOCP2 REAL
--	 ,pcOOCP3 REAL
--	 ,pcOOCP4 REAL
--	 ,pcOOCP5 REAL
--	 ,pcOOCP6 REAL
--	 ,pcOOCP7 REAL
--	 ,pcOOCP8 REAL
--	 ,REGION INT
--GO

----Getting the total number of workers by PUMA (control data)
--UPDATE #metaData
--	SET EMPLOYMENT = t1.EMPLOYMENT
--		,METAGEO = t1.METAGEO
--	FROM (SELECT METAGEO, ROUND(SUM(TOTEMPL),0) AS EMPLOYMENT FROM control_totals_maz GROUP BY METAGEO) AS t1, 
--		#metaData t2
--	WHERE (t1.METAGEO = t2.PUMA)

----Computing the shares of employment by occupation category using PUMS
--UPDATE #metaData
--	SET pcOOCP1 = (OCCP1/PUMSWORKERS)
--	 ,pcOOCP2 = (OCCP2/PUMSWORKERS)
--	 ,pcOOCP3 = (OCCP3/PUMSWORKERS)
--	 ,pcOOCP4 = (OCCP4/PUMSWORKERS)
--	 ,pcOOCP5 = (OCCP5/PUMSWORKERS)
--	 ,pcOOCP6 = (OCCP6/PUMSWORKERS)
--	 ,pcOOCP7 = (OCCP7/PUMSWORKERS)
--	 ,pcOOCP8 = (OCCP8/PUMSWORKERS)

--UPDATE #metaData
--	SET REGION = t1.REGION
--	FROM (SELECT DISTINCT PUMA, REGION FROM #geographicCWalk) AS t1, 
--		#metaData t2
--	WHERE (t1.PUMA = t2.PUMA)

----Allocating the employment to different occupation category
--SELECT PUMA	
--	,METAGEO
--	,REGION
--	,EMPLOYMENT
--	,ROUND((EMPLOYMENT*pcOOCP1),0) AS OCCP1
--	,ROUND((EMPLOYMENT*pcOOCP2),0) AS OCCP2
--	,ROUND((EMPLOYMENT*pcOOCP3),0) AS OCCP3
--	,ROUND((EMPLOYMENT*pcOOCP4),0) AS OCCP4
--	,ROUND((EMPLOYMENT*pcOOCP5),0) AS OCCP5
--	,ROUND((EMPLOYMENT*pcOOCP6),0) AS OCCP6
--	,ROUND((EMPLOYMENT*pcOOCP7),0) AS OCCP7
--	,ROUND((EMPLOYMENT*pcOOCP8),0) AS OCCP8
--INTO control_totals_meta
--FROM #metaData

--ALTER TABLE control_totals_meta
--	ALTER COLUMN PUMA INT NOT NULL

--ALTER TABLE dbo.control_totals_meta
--	ADD CONSTRAINT [PK dbo.control_totals_meta PUMA] 
--	PRIMARY KEY (PUMA)
--GO

--Some housekeeping
ALTER INDEX ALL ON dbo.control_totals_maz
REBUILD WITH (FILLFACTOR = 80, SORT_IN_TEMPDB = ON,
              STATISTICS_NORECOMPUTE = ON);
GO

ALTER INDEX ALL ON dbo.control_totals_taz
REBUILD WITH (FILLFACTOR = 80, SORT_IN_TEMPDB = ON,
              STATISTICS_NORECOMPUTE = ON);
GO

ALTER INDEX ALL ON dbo.control_totals_meta
REBUILD WITH (FILLFACTOR = 80, SORT_IN_TEMPDB = ON,
              STATISTICS_NORECOMPUTE = ON);
GO
PRINT 'Finished rebuilding indexes...'
PRINT 'Control table creation complete!'

--SELECT * FROM control_totals_maz
--SELECT * FROM control_totals_taz
--SELECT * FROM control_totals_meta