--Post-processing PopSynIII output to generate a fully expanded synthetic population
--Sriram Narayanamoorthy, narayanamoorthys@pbworld.com, 052014

--Updated to accomodate group quarters population
--Sriram Narayanamoorthy, narayanamoorthys@pbworld.com, 062014
------------------------------------------------------------------------------------

USE [NVPopSynIII]
GO

SET NOCOUNT ON;

--Cleaning up objects created during previous SQL transactions
IF OBJECT_ID('dbo.persons') IS NOT NULL 
	DROP TABLE dbo.persons;
IF OBJECT_ID('dbo.households') IS NOT NULL 
	DROP TABLE dbo.households;
IF OBJECT_ID('dbo.all_person') IS NOT NULL 
	DROP TABLE dbo.all_person;
IF OBJECT_ID('dbo.all_hh') IS NOT NULL 
	DROP TABLE dbo.all_hh;
IF OBJECT_ID('tempdb..#Numbers') IS NOT NULL 
	DROP TABLE #Numbers;

IF EXISTS (SELECT * 
	FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
	WHERE CONSTRAINT_TYPE = 'PRIMARY KEY' 
	AND TABLE_NAME = 'all_hh'
	AND TABLE_SCHEMA ='dbo')
		ALTER TABLE [dbo].[all_hh] 
			DROP CONSTRAINT [PK dbo.all_hh GQFLAG,tempId];

IF EXISTS (SELECT * 
	FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
	WHERE CONSTRAINT_TYPE = 'CHECK' 
	AND TABLE_NAME = 'all_hh'
	AND TABLE_SCHEMA ='dbo')
		ALTER TABLE [dbo].[all_hh] 
			DROP CONSTRAINT [CK dbo.all_hh finalweight 1-500]
	
IF EXISTS (SELECT * 
	FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
	WHERE CONSTRAINT_TYPE = 'PRIMARY KEY' 
	AND TABLE_NAME = 'all_person'
	AND TABLE_SCHEMA ='dbo')
		ALTER TABLE [dbo].[all_person] 
			DROP CONSTRAINT [PK dbo.all_person GQFLAG,tempId,sporder]

IF EXISTS (SELECT * 
	FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
	WHERE CONSTRAINT_TYPE = 'CHECK' 
	AND TABLE_NAME = 'all_person'
	AND TABLE_SCHEMA ='dbo')
		ALTER TABLE [dbo].[all_person] 
			DROP CONSTRAINT [CK dbo.all_person finalweight 1-500]

------------------------------------------------------------------------------------
--Creating an auxiliary table of numbers for inner join
--Auxiliary table of numbers credit [http://sqlblog.com/blogs/paul_white/default.aspx]
CREATE TABLE tempdb.#Numbers
(
    n INTEGER NOT NULL,

    CONSTRAINT [PK tempdb.#Numbers n]
        PRIMARY KEY CLUSTERED (n)
);

WITH
    N1 AS (SELECT N1.n FROM (VALUES (1),(1),(1),(1),(1),(1),(1),(1),(1),(1)) AS N1 (n)),
    N2 AS (SELECT L.n FROM N1 AS L CROSS JOIN N1 AS R),
    N3 AS (SELECT L.n FROM N2 AS L CROSS JOIN N2 AS R),
    N4 AS (SELECT L.n FROM N3 AS L CROSS JOIN N3 AS R),
    N AS (SELECT ROW_NUMBER() OVER (ORDER BY @@SPID) AS n FROM N4)
INSERT tempdb.#Numbers
    (n)
SELECT TOP (1000000)
    n
FROM N
ORDER BY N.n
OPTION (MAXDOP 1);

------------------------------------------------------------------------------------
--Joining the general population and the synthetic population tables
SELECT * INTO all_hh
FROM
(
SELECT * FROM synpop_hh
	UNION
SELECT * FROM gq_hh
) a;

SELECT * INTO all_person
FROM
(
SELECT * FROM synpop_person
	UNION
SELECT * FROM gq_person
) a;

------------------------------------------------------------------------------------

--Making the PRIMARY KEY columns non-NULLABLE
ALTER TABLE dbo.all_hh
	ALTER COLUMN GQFLAG INT NOT NULL
	GO

--Post-processing synthesized household records	
ALTER TABLE dbo.all_hh
	ADD CONSTRAINT [PK dbo.all_hh GQFLAG,tempId] 
	PRIMARY KEY (GQFLAG,tempId)

ALTER TABLE dbo.all_hh
	ADD CONSTRAINT [CK dbo.all_hh finalweight 1-500]
	CHECK (finalweight BETWEEN 1 AND 500)

SELECT * INTO dbo.households
FROM dbo.all_hh AS HH
JOIN tempdb.#Numbers AS N
    ON N.n BETWEEN 1 AND HH.finalweight;

--Adding primary key to the table
ALTER TABLE dbo.households
	ADD CONSTRAINT [PK dbo.households GQFLAG,tempId,n] 
	PRIMARY KEY (GQFLAG,tempId,n);

------------------------------------------------------------------------------------

--Making the PRIMARY KEY columns non-NULLABLE
ALTER TABLE dbo.all_person
	ALTER COLUMN sporder INT NOT NULL
	GO

ALTER TABLE dbo.all_person
	ALTER COLUMN GQFLAG INT NOT NULL
	GO

--Post-processing synthesized person records
ALTER TABLE dbo.all_person
	ADD CONSTRAINT [PK dbo.all_person GQFLAG,tempId,sporder] 
	PRIMARY KEY (GQFLAG,tempId,sporder)
	GO

ALTER TABLE dbo.all_person
	ADD CONSTRAINT [CK dbo.all_person finalweight 1-500]
	CHECK (finalweight BETWEEN 1 AND 500)
	GO

SELECT * INTO dbo.persons
FROM dbo.all_person AS PER
JOIN tempdb.#Numbers AS N
    ON N.n BETWEEN 1 AND PER.finalweight;
    
--Adding primary key to the table
ALTER TABLE dbo.persons
	ADD CONSTRAINT [PK dbo.persons GQFLAG,tempId,n,sporder] 
	PRIMARY KEY (GQFLAG,tempId,n,sporder)

------------------------------------------------------------------------------------
--Generating household and person ID for use in ABM
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
GO
BEGIN TRANSACTION
ALTER TABLE dbo.households
   ADD HHID INT IDENTITY 
   CONSTRAINT [UQ dbo.households HHID] UNIQUE
   GO
   
ALTER TABLE dbo.persons
   ADD PERID INT IDENTITY
   CONSTRAINT [UQ dbo.persons HHID] UNIQUE
   GO
COMMIT TRANSACTION
SET TRANSACTION ISOLATION LEVEL READ COMMITTED

ALTER TABLE dbo.persons
   ADD HHID INT
   GO

ALTER INDEX ALL ON dbo.households
REBUILD WITH (FILLFACTOR = 80, SORT_IN_TEMPDB = ON,
              STATISTICS_NORECOMPUTE = ON);
GO

ALTER INDEX ALL ON dbo.persons
REBUILD WITH (FILLFACTOR = 80, SORT_IN_TEMPDB = ON,
              STATISTICS_NORECOMPUTE = ON);
GO

UPDATE P
SET HHID = H.HHID
FROM dbo.households AS H
JOIN dbo.persons AS P
    ON P.tempId = H.tempId
    AND P.n = H.n
	AND P.GQFLAG = H.GQFLAG
OPTION (LOOP JOIN);