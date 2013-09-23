USE [popsyn]
GO


ALTER PROCEDURE [dbo].[proc_generate_hh_pp] 
AS 

--description: store procedure to generate person table based on 2000 CENSUS PUMS
--				and 2008 ACS PUMS
--author: Ziying Ouyang
--history:
-- first created in March 2011
-- modified poverty using PUMS poverty in Aug 2011
-- added occupation code on Nov 14, 2011

-- 1. drop tables, sequence matters

IF OBJECT_ID (N'[dbo].[person]') IS NOT NULL
DROP TABLE [dbo].person

IF OBJECT_ID (N'[dbo].[household]') IS NOT NULL
DROP TABLE [dbo].household

-- 2. create household table
CREATE TABLE [dbo].[household](
	[source_id] [int] NOT NULL,
	[serial_no] [bigint] NOT NULL,
	[puma] [int] NOT NULL,
	[adj_income] [int] NOT NULL,
	[family] [bit] NOT NULL,
	[owner_occ] [bit] NOT NULL,
	[unit_type] [smallint] NOT NULL,
	[dwelling_type] [smallint] NOT NULL,
	[init_weight] [int] NOT NULL,
 CONSTRAINT [PK_houshold] PRIMARY KEY CLUSTERED 
(
	[source_id] ASC,
	[serial_no] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY];

EXEC sys.sp_addextendedproperty @name=N'Domain', @value=N'dbo.source.id' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'source_id'


EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'data source id' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'source_id'


EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'serial number' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'serial_no'


EXEC sys.sp_addextendedproperty @name=N'Domain', @value=N'1: true, 0: false' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'family'


EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'family household' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'family'


EXEC sys.sp_addextendedproperty @name=N'Domain', @value=N'1:true, 0: false' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'owner_occ'


EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'owner occupied household' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'owner_occ'


EXEC sys.sp_addextendedproperty @name=N'Domain', @value=N'dbo.unit_type.id' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'unit_type'


EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'housing unit/group quarter' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'unit_type'


EXEC sys.sp_addextendedproperty @name=N'Domain', @value=N'dbo.dwelling_type.id' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'dwelling_type'


EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'SF/MF/Mobile Home' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'dwelling_type'


EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'initial weight' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household', @level2type=N'COLUMN',@level2name=N'init_weight'


EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'PUMS household record' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'household'


ALTER TABLE [dbo].[household]  WITH CHECK ADD  CONSTRAINT [FK_household_dwelling_type] FOREIGN KEY([dwelling_type])
REFERENCES [dbo].[dwelling_type] ([id])


ALTER TABLE [dbo].[household] CHECK CONSTRAINT [FK_household_dwelling_type]


ALTER TABLE [dbo].[household]  WITH CHECK ADD  CONSTRAINT [FK_household_source] FOREIGN KEY([source_id])
REFERENCES [dbo].[source] ([id])


ALTER TABLE [dbo].[household] CHECK CONSTRAINT [FK_household_source]


ALTER TABLE [dbo].[household]  WITH CHECK ADD  CONSTRAINT [FK_household_unit_type] FOREIGN KEY([unit_type])
REFERENCES [dbo].[unit_type] ([id])


ALTER TABLE [dbo].[household] CHECK CONSTRAINT [FK_household_unit_type];


-- 3. insert 2000 CENSUS PUMS household records into household table
WITH T as
		(
		SELECT
			h.SERIALNO,p.AGE,p.MILTARY,p.GRADE,h.UNITTYPE,p.PWEIGHT
		FROM
			data_cafe.dbo.pums_hh_sd h ,
			data_cafe.dbo.pums_pp_sd p
		WHERE
			h.SERIALNO = p.SERIALNO and h.UNITTYPE in (1,2)
		)

INSERT INTO household
SELECT 1
	,h.SERIALNO
	,puma5
	,hinc
	,family = CASE WHEN HHT BETWEEN 1 AND 3 THEN 1 ELSE 0 END
	,owner_occ = CASE WHEN TENURE IN (1,2) THEN 1 ELSE 0 END
	,unit_type = CASE WHEN h.UNITTYPE = 0 THEN 1
					  WHEN h.UNITTYPE = 1 THEN 2
					  WHEN h.UNITTYPE = 2 and T.GRADE IN (6,7) THEN 3
					  WHEN h.UNITTYPE = 2 and T.MILTARY = 1 THEN 4					
					  ELSE 5 END
	,dwelling_type = CASE WHEN BLDGSZ IN (2,3) THEN 1
						  WHEN BLDGSZ = 1 THEN 3
						  when BLDGSZ between 4 and 9 then 2
						  else 0 END --include other & GQ
	,VEHICL --Wu added, needed by the ABM core model
	,HWEIGHT = case when h.UNITTYPE = 0 then HWEIGHT				 
				else t.PWEIGHT end					 
from data_cafe.dbo.pums_hh_sd h
	LEFT JOIN T ON
	h.serialno = T.serialno ;
	
-- 4. insert 2005-2009 5yr ACS PUMS household records into household table

WITH T as
		(
		SELECT
			h.SERIALNO,p.AGEP,p.MIL,p.SCHG,h.TYPE,p.pwgtp
		FROM
			acs.dbo.v_pums_hh_sd_2009e5 h,
			acs.dbo.v_pums_pp_sd_2009e5 p
		WHERE
			h.SERIALNO = p.SERIALNO and h.TYPE in (2,3)
		)


INSERT INTO household
SELECT 4
	,h.SERIALNO
	,puma
	,hincp
	,family = CASE WHEN HHT BETWEEN 1 AND 3 THEN 1 ELSE 0 END
	,owner_occ = CASE WHEN TEN IN (1,2) THEN 1 ELSE 0 END
	,unit_type = CASE WHEN h.TYPE = 1 THEN 1
					  WHEN h.TYPE = 2 THEN 2
					  WHEN h.TYPE = 3 and T.schg IN (15,16) THEN 3
					  WHEN h.TYPE = 3 and T.MIL = 1 THEN 4					
					  ELSE 5 END
	,dwelling_type = CASE WHEN BLD IN (2,3) THEN 1
						  WHEN BLD = 1 THEN 3
						  WHEN BLD BETWEEN 4 AND 9 THEN 2
						  ELSE 0 END --include other & GQ
	,WGTP = CASE WHEN h.TYPE = 1 THEN WGTP
		ELSE pwgtp END					 
from acs.dbo.v_pums_hh_sd_2009e5 h 
	LEFT JOIN T ON
	h.serialno = T.serialno ;


-- 5. create person table


CREATE TABLE [dbo].[person](
	[source_id] [int] NOT NULL,
	[household_serial_no] [bigint] NOT NULL,
	[person_id] [int] NOT NULL,
	[age] [smallint] NOT NULL,
	[relationship] [smallint] NOT NULL,
	[employ_type] [smallint] NOT NULL,
	[work_hour_type] [smallint] NOT NULL,
	[military] [bit] NOT NULL,
	[grade] [smallint] NOT NULL,
	[male] [bit] NOT NULL,
	[race] [smallint] NOT NULL,
	[person_type] [smallint] NOT NULL,
	[hispanic] [bit] NOT NULL,
	[married] [bit] NOT NULL,
	[poverty] [bit] NOT NULL,
	[earnings] [int] NULL,
	[income_total] [int] NULL,
	[occupation] smallint not null,
	[wgt] [int] NULL
 CONSTRAINT [PK_person] PRIMARY KEY CLUSTERED 
(
	[source_id] ASC,
	[household_serial_no] ASC,
	[person_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: CENSUS 2000
4: ACS' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'source_id'


EXEC sys.sp_addextENDedproperty @name=N'MS_Description', @value=N'data source id' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'source_id'

EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: Householder
2: Spouse
3: Child
4: Other Relative
5: Not Related
6: Inst. Group Quaters
7: Non-Inst. Group Quarters
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'relationship'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'0: NOT_IN_UNIVERSE
1: EMPLOYED
2: MILITARY
3: UNEMPLOYED
4: NOT_IN_LABOR_FORCE
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'employ_type'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'0: Not In Universe
1: 35+ Hours
2: 15 - 34 Hours
3: 1 - 14 Hours
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'work_hour_type'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: military on active duty
0: non military' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'military'


EXEC sys.sp_addextENDedproperty @name=N'MS_Description', @value=N'military status' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'military'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'0: NON_STUDENT
1: PRESCHOOL
2: GRADE_K_TO_8
3: GRADE_9_TO12
4: COLLEGE_AND_UP
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'grade'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: male
0: female' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'male'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: White
2: Black
3: American Indian
4: Alaska Native
5: Am. Indian / Alaska Tribe
6: Asian
7: Native Pacific Islander
8: Other Race Alone
9: Two or More Races
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'race'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: Full Time Worked
2: Part Time Worker
3: University Student
4: Non-working Adult
5: Retiree
6: Driving Age Schooler
7: Non Driving Age Schooler
8: Preschooler
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'person_type'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: hispanic
0: non hispanic' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'hispanic'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: married spouse present
0: widowed, divorced, separated, never married, married, spouse absent ' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'married'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'1: family income below poverty
0: family income above poverty' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'poverty'


EXEC sys.sp_addextENDedproperty @name=N'Domain', @value=N'' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'wgt'


EXEC sys.sp_addextENDedproperty @name=N'MS_Description', @value=N'person weight from PUMS data' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'person', @level2type=N'COLUMN',@level2name=N'wgt'


ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [FK_person_employment_type] FOREIGN KEY([employ_type])
REFERENCES [dbo].[employment_type] ([id])


ALTER TABLE [dbo].[person] CHECK CONSTRAINT [FK_person_employment_type]


ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [FK_person_grade_type] FOREIGN KEY([grade])
REFERENCES [dbo].[grade_type] ([id])


ALTER TABLE [dbo].[person] CHECK CONSTRAINT [FK_person_grade_type]


ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [FK_person_household] FOREIGN KEY([source_id], [household_serial_no])
REFERENCES [dbo].[household] ([source_id], [serial_no])


ALTER TABLE [dbo].[person] CHECK CONSTRAINT [FK_person_household]


ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [FK_person_person_type] FOREIGN KEY([person_type])
REFERENCES [dbo].[person_type] ([id])


ALTER TABLE [dbo].[person] CHECK CONSTRAINT [FK_person_person_type]


ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [FK_person_race_type] FOREIGN KEY([race])
REFERENCES [dbo].[race_type] ([id])


ALTER TABLE [dbo].[person] CHECK CONSTRAINT [FK_person_race_type]


ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [FK_person_relationship_type] FOREIGN KEY([relationship])
REFERENCES [dbo].[relationship_type] ([id])


ALTER TABLE [dbo].[person] CHECK CONSTRAINT [FK_person_relationship_type]


ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [FK_person_work_hour_type] FOREIGN KEY([work_hour_type])
REFERENCES [dbo].[work_hour_type] ([id])


ALTER TABLE [dbo].[person] CHECK CONSTRAINT [FK_person_work_hour_type]


ALTER TABLE [dbo].[person]  WITH CHECK ADD  CONSTRAINT [FK_person_occupation] FOREIGN KEY([occupation])
REFERENCES [dbo].[occupation_type] ([occupation_id])

ALTER TABLE [dbo].[person] CHECK CONSTRAINT [FK_person_occupation]

--6. insert PUMS records from census 2000 source_id = 1

INSERT INTO person
SELECT 1
	,SERIALNO
	,PNUM
	,AGE
	,RELATE =(CASE 
                      WHEN RELATE = 1  THEN 1 
                      WHEN RELATE = 2  THEN 2      
                      WHEN RELATE BETWEEN 3 AND 5 THEN 3 
                      WHEN RELATE BETWEEN 6 AND 16 THEN 4 
                      WHEN RELATE BETWEEN 17 AND 21 THEN 5                          
                      WHEN RELATE = 22 THEN 6                       
                      WHEN RELATE = 23 THEN 7                               
                  END)
     , employ_type = (CASE 
                      WHEN ESR BETWEEN 1 AND 2 THEN 1
                      WHEN ESR BETWEEN 4 AND 5 THEN 2
                      WHEN ESR = 3 THEN 3
                      WHEN ESR = 6 THEN 4
                      ELSE 0
                     END)
	  ,wkHour = (CASE
                      WHEN WEEKS >= 27 AND HOURS >= 35 THEN 1
                      WHEN WEEKS >= 27 AND HOURS BETWEEN 15 AND 34 THEN 2
                      WHEN WEEKS >= 27 AND HOURS BETWEEN 1 AND 14 THEN 3
                      ELSE 0
                  END)
      ,MILTARY = CASE WHEN MILTARY in (1,3) THEN 1 ELSE 0 END
      ,grade = CASE WHEN GRADE = 1 THEN 1
					WHEN GRADE between 2 and 4 THEN 2
					WHEN GRADE = 5 THEN 3
					WHEN GRADE between 6 and 7 THEN 4
					ELSE 0 
					END 
	 ,MALE = CASE WHEN SEX = 1 THEN 1 ELSE 0 END
     ,RACE1
     ,PTYPE = (CASE 
                      WHEN AGE < 6 THEN 8
                      WHEN AGE BETWEEN 6 AND 15 THEN 7
                      WHEN ESR IN (1,2,4,5) AND WEEKS >= 30 AND HOURS >= 35 THEN 1
                      WHEN ESR IN (1,2,4,5)
                        AND ((WEEKS < 30 AND HOURS >= 35)
                        OR  ( WEEKS < 30 AND HOURS < 35) 
                        OR (WEEKS >=30 AND HOURS <35))THEN 2
                      WHEN GRADE between 6 and 7 or (AGE >= 20 and GRADE > 0)THEN 3
                      WHEN AGE BETWEEN 16 AND 19 AND GRADE between 4 and 5 THEN 6
                      WHEN ESR IN (3, 6) AND GRADE =0 AND AGE < 65 THEN 4
                      ELSE 5
                 END)
      ,HISPANIC = CASE 
                      WHEN HISPAN > 1 THEN 1 ELSE 0 END
      ,MSP = CASE 
                      WHEN MSP = 1 THEN 1 ELSE 0 END
      ,POVERTY =  CASE WHEN POVERTY between 1 and 100 THEN 1 ELSE 0 END               
      ,EARNS 
      ,inctot
      ,occupation = case 
						when occcen5 = 0 then 0
						when occcen5 between 1 and 359 then 1 --management
						when occcen5 between 360 and 469 then 2 --service
						when occcen5 between 470 and 599 then 3	--sales
						when occcen5 between 600 and 769 then 4 --natural resources/construction/maintenan
						when occcen5 between 770 and 979 then 5 --product/trans		
						when occcen5 between 980 and 983 then 6
						else 0	end		--unemployed	
	  ,INDCEN  --Wu added ABM core model needs this attribute
	  ,OCCCEN5 --Wu added ABM core model needs this attribute
      ,PWEIGHT
from data_cafe.dbo.pums_pp_sd;


-- 8. insert ACS PUMS person records, source_id = 4
INSERT INTO person
SELECT 
	4
	,SERIALNO
	,SPORDER
	,agep
	,relationship =  (CASE                         
                         WHEN REL = 0  THEN 1                                              
                         WHEN REL = 1  THEN 2      
                         WHEN REL = 2 THEN 3 
                         WHEN REL BETWEEN 3 AND 7 THEN 4 
                         WHEN REL BETWEEN 8 AND 12 THEN 5
                         WHEN REL = 13  THEN 6
                         WHEN REL = 14  THEN 7                                                                        
                END) 
     ,empStatus = (CASE  WHEN ESR BETWEEN 1 AND 2 THEN 1
                         WHEN ESR BETWEEN 4 AND 5 THEN 2
                         WHEN ESR = 3 THEN 3
                         WHEN ESR = 6 THEN 4
                         ELSE 0
                     END)
    ,wkHour = (CASE      WHEN WKW <= 4 AND WKHP >= 35 THEN 1
                         WHEN WKW <= 4 AND WKHP BETWEEN 15 AND 34 THEN 2
                         WHEN WKW <= 4 AND WKHP BETWEEN 1 AND 14 THEN 3
                         ELSE 0
                   END)
     ,military = CASE    WHEN MIL in (1,2,4) THEN 1 ELSE 0 END
     ,gradeType =  (CASE 
                         WHEN SCHG =1 THEN 1                         
                         WHEN SCHG BETWEEN 2 AND 4  THEN 2                                              
                         WHEN SCHG = 5 THEN 3      
                         WHEN SCHG BETWEEN 6 AND 7 THEN 4   
                         ELSE 0                                               
                      END)
     ,MALE = CASE     WHEN SEX = 1 THEN 1 ELSE 0 END
     ,RAC1P
    ,TYPE = (CASE    WHEN AGEP < 6 THEN 8
                     WHEN AGEP BETWEEN 6 AND 15 THEN 7
                     WHEN ESR IN (1,2,4,5) AND WKW BETWEEN 1 AND 4 AND WKHP >= 35 THEN 1
                     WHEN ESR IN (1,2,4,5)
                       AND ((WKW BETWEEN 5 AND 6 AND WKHP < 35)
                       OR (WKW BETWEEN 5 AND 6 AND WKHP >= 35)
                       OR (WKW BETWEEN 1 AND 4 AND WKHP < 35))    THEN 2
                     WHEN SCHG between 6 and 7  or (AGEP >= 20 and SCHG > 0) THEN 3
                     WHEN AGEP between 16 and 19 AND SCHG between 4 and 5 THEN 6
                     WHEN ESR in (3,6) AND SCHG =0 AND AGEP < 65 THEN 4
                     ELSE 5
                END)
    ,HISP = CASE WHEN HISP > 1 THEN 1 ELSE 0 END
    ,MSP = CASE WHEN MSP = 1 THEN 1 ELSE 0 END
    ,POVERTY = CASE WHEN povpip < 100 THEN 1 ELSE 0 END
    ,PERNP 
    ,PINCP
    ,occupation  = case 
				when occp = 0 then 0
				when occp between 1 and 3590 then 1 --management
				when occp between 3600 and 4690 then 2 --service
				when occp between 4700 and 5990 then 3	--sales
				when occp between 6000 and 7690 then 4 --natural resources/construction/maintenan
				when occp between 7700 and 9790 then 5 --product/trans		
				when occp between 9800 and 9830 then 6 --military
				else 0	end		--unemployed 9920		 
    ,PWGTP         
	from acs.dbo.v_pums_pp_sd_2009e5 p
	


GO