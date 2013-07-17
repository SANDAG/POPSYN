/*
POPSYN MASTER TABLES
*/
-- VERSION --


-- SOURCE TABLE --
ALTER TABLE [source]  WITH CHECK ADD  CONSTRAINT [cst_source_type] CHECK  (([source_type]='acs' OR [source_type]='census'))
GO

ALTER TABLE [source] CHECK CONSTRAINT [cst_source_type]
GO

ALTER TABLE [source]  WITH CHECK ADD  CONSTRAINT [cst_year] CHECK  (([year]>(1970) AND [year]<=datepart(year,getdate())))
GO

ALTER TABLE [source] CHECK CONSTRAINT [cst_year]
GO

/*
Create Geography Tables
*/
-- GEOGRAPHY TABLE --


-- GEOGRAPHY TYPE TABLE --
ALTER TABLE [geography_type]  WITH CHECK ADD  CONSTRAINT [FK_geography_type_geography] FOREIGN KEY([geography])
REFERENCES [geography] ([geography]) ON UPDATE CASCADE
GO

ALTER TABLE [geography_type] CHECK CONSTRAINT [FK_geography_type_geography]
GO

-- GEOGRAPHY ZONE TABLE --
ALTER TABLE [geography_zone]  WITH CHECK ADD  CONSTRAINT [FK_geography_zone_geography_type] FOREIGN KEY([geo_type_id])
REFERENCES [geography_type] ([id]) ON UPDATE CASCADE
GO

ALTER TABLE [geography_zone] CHECK CONSTRAINT [FK_geography_zone_geography_type]
GO

ALTER TABLE [geography_zone]  WITH CHECK ADD  CONSTRAINT [ck_geography_zone_centroid] CHECK  (([centroid].[STDimension]()=(0)))
GO

ALTER TABLE [geography_zone] CHECK CONSTRAINT [ck_geography_zone_centroid]
GO

/*
LOOK-UP TABLES
*/
-- DWELLING TYPES --


-- EMPLOYMENT TYPES --


-- GRADE TYPES --


--OCCUPATION TYPES --


-- PERSON TYPES --


-- RACE TYPES --


-- RELATIONSHIP TYPES --


-- TARGET CATEGORIES --


-- TARGET LAND USES VERSIONS --


-- UNIT TYPES --


-- WORK HOUR TYPES --


/*
INPUT TABLES
*/
-- HOUSEHOLDS --
ALTER TABLE [household]  WITH CHECK ADD  CONSTRAINT [FK_household_dwelling_type] FOREIGN KEY([dwelling_type])
REFERENCES [dwelling_type] ([id])
GO

ALTER TABLE [household] CHECK CONSTRAINT [FK_household_dwelling_type]
GO

ALTER TABLE [household]  WITH CHECK ADD  CONSTRAINT [FK_household_source] FOREIGN KEY([source_id])
REFERENCES [source] ([id])
GO

ALTER TABLE [household] CHECK CONSTRAINT [FK_household_source]
GO

ALTER TABLE [household]  WITH CHECK ADD  CONSTRAINT [FK_household_unit_type] FOREIGN KEY([unit_type])
REFERENCES [unit_type] ([id])
GO

ALTER TABLE [household] CHECK CONSTRAINT [FK_household_unit_type]
GO

-- PERSON TABLE --
ALTER TABLE [person]  WITH CHECK ADD  CONSTRAINT [FK_person_employment_type] FOREIGN KEY([employ_type])
REFERENCES [employment_type] ([id])
GO

ALTER TABLE [person] CHECK CONSTRAINT [FK_person_employment_type]
GO

ALTER TABLE [person]  WITH CHECK ADD  CONSTRAINT [FK_person_grade_type] FOREIGN KEY([grade])
REFERENCES [grade_type] ([id])
GO

ALTER TABLE [person] CHECK CONSTRAINT [FK_person_grade_type]
GO

ALTER TABLE [person]  WITH CHECK ADD  CONSTRAINT [FK_person_household] FOREIGN KEY([source_id], [household_serial_no])
REFERENCES [household] ([source_id], [serial_no])
GO

ALTER TABLE [person] CHECK CONSTRAINT [FK_person_household]
GO

ALTER TABLE [person]  WITH CHECK ADD  CONSTRAINT [FK_person_occupation] FOREIGN KEY([occupation])
REFERENCES [occupation_type] ([occupation_id])
GO

ALTER TABLE [person] CHECK CONSTRAINT [FK_person_occupation]
GO

ALTER TABLE [person]  WITH CHECK ADD  CONSTRAINT [FK_person_person_type] FOREIGN KEY([person_type])
REFERENCES [person_type] ([id])
GO

ALTER TABLE [person] CHECK CONSTRAINT [FK_person_person_type]
GO

ALTER TABLE [person]  WITH CHECK ADD  CONSTRAINT [FK_person_race_type] FOREIGN KEY([race])
REFERENCES [race_type] ([id])
GO

ALTER TABLE [person] CHECK CONSTRAINT [FK_person_race_type]
GO

ALTER TABLE [person]  WITH CHECK ADD  CONSTRAINT [FK_person_relationship_type] FOREIGN KEY([relationship])
REFERENCES [relationship_type] ([id])
GO

ALTER TABLE [person] CHECK CONSTRAINT [FK_person_relationship_type]
GO

ALTER TABLE [person]  WITH CHECK ADD  CONSTRAINT [FK_person_work_hour_type] FOREIGN KEY([work_hour_type])
REFERENCES [work_hour_type] ([id])
GO

ALTER TABLE [person] CHECK CONSTRAINT [FK_person_work_hour_type]
GO

-- TARGETS --
ALTER TABLE [target]  WITH CHECK ADD  CONSTRAINT [FK_target_target_category] FOREIGN KEY([category])
REFERENCES [target_category] ([id])
ON UPDATE CASCADE
GO

ALTER TABLE [target] CHECK CONSTRAINT [FK_target_target_category]
GO

ALTER TABLE [target]  WITH CHECK ADD  CONSTRAINT [FK_target_target_lu_maj_version] FOREIGN KEY([lu_major_version], [lu_minor_version])
REFERENCES [target_lu_version] ([lu_major_version], [lu_minor_version])
GO

ALTER TABLE [target] CHECK CONSTRAINT [FK_target_target_lu_maj_version]
GO

-- TARGET GROWTH FACTOR --


/*
OUTPUT TABLES
*/
-- HOUSEHOLD ALLOCATED --


-- HOUSEHOLD DISCRETIZED --
ALTER TABLE [household_discretized]  WITH NOCHECK ADD  CONSTRAINT [FK_household_discretized_version] FOREIGN KEY([run_version])
REFERENCES [version] ([id])
GO

ALTER TABLE [household_discretized] CHECK CONSTRAINT [FK_household_discretized_version]
GO

/*
VALIDATION TABLES
*/
-- CENSUS VALIDATION --


-- ACS Validation --