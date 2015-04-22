/*
Borrowed from leversandturtles presentation and materials:
T:\ABM\user\gks\ABM DataBase\Learning Materials\Metadata\leversandturtles
*/
SET NOCOUNT ON;

-- Create db_meta schema
IF NOT EXISTS (SELECT schema_name FROM information_schema.schemata WHERE schema_name='db_meta')
BEGIN

EXEC ('CREATE SCHEMA [db_meta]')

EXEC sys.sp_addextendedproperty 
    @name = 'ms_description'
   ,@value = 'schema for metadata utilities'
   ,@level0type = 'schema'
   ,@level0name = 'db_meta'
   ,@level1type = NULL
   ,@level1name = NULL
   ,@level2type = NULL
   ,@level2name = NULL

END

GO


-- Create [db_meta].[object_info] function
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[db_meta].[object_info]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
	DROP FUNCTION [db_meta].[object_info]
GO

CREATE FUNCTION [db_meta].[object_info](@obj varchar(max))
RETURNS TABLE AS RETURN (
	SELECT ObjectSchema = OBJECT_SCHEMA_NAME(o.object_id)
			,ObjectName = OBJECT_NAME(o.object_id)
			,FullObjectName = QUOTENAME(OBJECT_SCHEMA_NAME(o.object_id)) + '.' + QUOTENAME(o.name)
			,ObjectType = o.type_desc
			,IsSchema = CAST(0 AS bit)
			,level0type = 'schema'
			,level0name = OBJECT_SCHEMA_NAME(o.object_id)
			,level1type = CASE WHEN o.type_desc = 'VIEW' THEN 'view'
								WHEN o.type_desc = 'USER_TABLE' THEN 'table'
								WHEN o.type_desc = 'SQL_STORED_PROCEDURE' THEN 'procedure'
								WHEN o.type_desc = 'SQL_INLINE_TABLE_VALUED_FUNCTION' THEN 'function'
								ELSE o.type_desc
							END
			,level1name = OBJECT_NAME(o.object_id)
			,level2type = NULL
			,level2name = NULL
	FROM sys.objects AS o
	WHERE o.object_id = OBJECT_ID(@obj)

	UNION ALL

	SELECT ObjectSchema = SCHEMA_NAME(SCHEMA_ID(@obj))
		,ObjectName = NULL
		,FullObjectName = QUOTENAME(SCHEMA_NAME(SCHEMA_ID(@obj)))
		,ObjectType = 'schema'
		,IsSchema = CAST(1 AS bit)
		,level0type = 'schema'
		,level0name = SCHEMA_NAME(SCHEMA_ID(@obj))
		,level1type = NULL
		,level1name = NULL
		,level2type = NULL
		,level2name = NULL
	WHERE SCHEMA_ID(@obj) IS NOT NULL

	UNION ALL
	
	SELECT ObjectSchema = OBJECT_SCHEMA_NAME(o.object_id)
			,ObjectName = OBJECT_NAME(o.object_id)
			,FullObjectName = QUOTENAME(OBJECT_SCHEMA_NAME(o.object_id)) + '.' + QUOTENAME(o.name)
			,ObjectType = 'column'
			,IsSchema = CAST(0 AS bit)
			,level0type = 'schema'
			,level0name = OBJECT_SCHEMA_NAME(o.object_id)
			,level1type = 'table'
			,level1name = OBJECT_NAME(o.object_id)
			,level2type = 'column'
			,level2name = PARSENAME(@obj, 1)
	FROM sys.objects AS o
	WHERE PARSENAME(@obj, 1) IS NOT NULL
		AND PARSENAME(@obj, 2) IS NOT NULL
		AND PARSENAME(@obj, 3) IS NOT NULL
		AND PARSENAME(@obj, 4) IS NULL
		AND o.object_id = OBJECT_ID(QUOTENAME(PARSENAME(@obj, 3)) + '.' + QUOTENAME(PARSENAME(@obj, 2)))
)
GO

EXEC sys.sp_addextendedproperty 
	@name = 'ms_description'
	,@value = 'return xp friendly object types'
	,@level0type = 'schema'
	,@level0name = 'db_meta'
	,@level1type = 'function'
	,@level1name = 'object_info'
	,@level2type = NULL
	,@level2name = NULL
GO
EXEC sys.sp_addextendedproperty 
	@name = 'subsystem'
	,@value = 'meta'
	,@level0type = 'schema'
	,@level0name = 'db_meta'
	,@level1type = 'function'
	,@level1name = 'object_info'
	,@level2type = NULL
	,@level2name = NULL
GO


-- Create [db_meta].[add_xp] function
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[db_meta].[add_xp]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [db_meta].[add_xp]
GO

CREATE PROCEDURE [db_meta].[add_xp]
	@obj AS varchar(max)
	,@name AS sysname
	,@value AS varchar(7500)
AS
BEGIN
	DECLARE @level0type AS varchar(128)
	DECLARE @level0name AS sysname
	DECLARE @level1type AS varchar(128)
	DECLARE @level1name AS sysname
	DECLARE @level2type AS varchar(128)
	DECLARE @level2name AS sysname
	
	SELECT @level0type = level0type
		,@level0name = level0name
		,@level1type = CASE WHEN level1type = 'SQL_TABLE_VALUED_FUNCTION' THEN 'function' ELSE level1type END
		,@level1name = level1name
		,@level2type = level2type
		,@level2name = level2name
	FROM db_meta.object_info(@obj)

	EXEC sys.sp_addextendedproperty 
		@name = @name
		,@value = @value
		,@level0type = @level0type
		,@level0name = @level0name
		,@level1type = @level1type
		,@level1name = @level1name
		,@level2type = @level2type
		,@level2name = @level2name
END
GO

--EXEC sys.sp_addextendedproperty 
--	@name = 'MS_Description'
--	,@value = 'Procedure to make sys.extended_properties easier to use'
--	,@level0type = 'SCHEMA'
--	,@level0name = 'db_meta'
--	,@level1type = 'PROCEDURE'
--	,@level1name = 'add_xp'
--	,@level2type = NULL
--	,@level2name = NULL

EXEC db_meta.add_xp 'db_meta.add_xp', 'ms_description', 'procedure to make sys.extended_properties easier to use'
GO
EXEC db_meta.add_xp 'db_meta.add_xp', 'subsystem', 'meta'
GO


-- Create [db_meta].[update_xp] function
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[db_meta].[update_xp]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [db_meta].[update_xp]
GO

CREATE PROCEDURE [db_meta].[update_xp]
	@obj AS varchar(max)
	,@name AS sysname
	,@value AS varchar(7500)
AS
BEGIN
	DECLARE @level0type AS varchar(128)
	DECLARE @level0name AS sysname
	DECLARE @level1type AS varchar(128)
	DECLARE @level1name AS sysname
	DECLARE @level2type AS varchar(128)
	DECLARE @level2name AS sysname
	
	SELECT @level0type = level0type
		,@level0name = level0name
		,@level1type = CASE WHEN level1type = 'SQL_TABLE_VALUED_FUNCTION' THEN 'function' ELSE level1type END
		,@level1name = level1name
		,@level2type = level2type
		,@level2name = level2name
	FROM db_meta.object_info(@obj)

	EXEC sys.sp_updateextendedproperty 
		@name = @name
		,@value = @value
		,@level0type = @level0type
		,@level0name = @level0name
		,@level1type = @level1type
		,@level1name = @level1name
		,@level2type = @level2type
		,@level2name = @level2name
END
GO

EXEC db_meta.add_xp 'db_meta.update_xp', 'ms_description', 'procedure to make sys.extended_properties easier to use'
GO
EXEC db_meta.update_xp 'db_meta.update_xp', 'ms_description', 'procedure to make sys.extended_properties easier to update'
GO
EXEC db_meta.add_xp 'db_meta.update_xp', 'subsytem', 'meta'
GO




-- Create [db_meta].[drop_xp] function
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[db_meta].[drop_xp]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [db_meta].[drop_xp]
GO

CREATE PROCEDURE [db_meta].[drop_xp]
	@obj AS varchar(max)
	,@name AS sysname
AS
BEGIN
	DECLARE @level0type AS varchar(128)
	DECLARE @level0name AS sysname
	DECLARE @level1type AS varchar(128)
	DECLARE @level1name AS sysname
	DECLARE @level2type AS varchar(128)
	DECLARE @level2name AS sysname
	
	SELECT @level0type = level0type
		,@level0name = level0name
		,@level1type = CASE WHEN level1type = 'SQL_TABLE_VALUED_FUNCTION' THEN 'function' ELSE level1type END
		,@level1name = level1name
		,@level2type = level2type
		,@level2name = level2name
	FROM db_meta.object_info(@obj)

	EXEC sys.sp_dropextendedproperty 
		@name = @name
		,@level0type = @level0type
		,@level0name = @level0name
		,@level1type = @level1type
		,@level1name = @level1name
		,@level2type = @level2type
		,@level2name = @level2name
END
GO

EXEC db_meta.add_xp 'db_meta.drop_xp', 'ms_description', 'procedure to make sys.extended_properties easier to use'
GO
EXEC db_meta.drop_xp 'db_meta.drop_xp', 'ms_description'
GO
EXEC db_meta.add_xp 'db_meta.drop_xp', 'ms_description', 'procedure to make sys.extended_properties easier to drop'
GO
EXEC db_meta.add_xp 'db_meta.drop_xp', 'subsystem', 'meta'
GO


-- Create View for extended properties
IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[db_meta].[vi_data_dictionary]'))
	DROP VIEW [db_meta].[vi_data_dictionary]
GO

CREATE VIEW [db_meta].[vi_data_dictionary] AS
	SELECT 'SCHEMA' AS ObjectType
			,QUOTENAME(s.name) AS FullObjectName
			,s.name as ObjectSchema
			,NULL AS ObjectName
			,NULL AS SubName
			,xp.name AS PropertyName
			,xp.value AS PropertyValue
	FROM sys.extended_properties AS xp
	INNER JOIN sys.schemas AS s
		ON s.schema_id = xp.major_id
		AND xp.class_desc = 'schema'

	UNION ALL

	SELECT o.type_desc AS ObjectType
			,QUOTENAME(OBJECT_SCHEMA_NAME(o.object_id)) + '.' + QUOTENAME(o.name) AS FullObjectName
			,OBJECT_SCHEMA_NAME(o.object_id) AS ObjectSchema
			,o.name AS ObjectName
			,NULL AS SubName
			,xp.name AS PropertyName
			,xp.value AS PropertyValue
	FROM sys.extended_properties AS xp
	INNER JOIN sys.objects AS o
		ON o.object_id = xp.major_id
		AND xp.minor_id = 0
		AND xp.class_desc = 'object_or_column'

	UNION ALL

	SELECT 'COLUMN' AS ObjectType
			,QUOTENAME(OBJECT_SCHEMA_NAME(t.object_id)) + '.' + QUOTENAME(t.name) + '.' + QUOTENAME(c.name) AS FullObjectName
			,OBJECT_SCHEMA_NAME(t.object_id) AS ObjectSchema
			,t.name AS ObjectName
			,c.name AS SubName
			,xp.name AS PropertyName
			,xp.value AS PropertyValue
	FROM sys.extended_properties AS xp
	INNER JOIN sys.objects AS t
		ON t.object_id = xp.major_id
		AND xp.minor_id <> 0
		AND xp.class_desc = 'object_or_column'
		AND t.type_desc = 'USER_TABLE'
	INNER JOIN sys.columns AS c
		ON c.object_id = t.object_id
		AND c.column_id = xp.minor_id
		
GO
EXEC db_meta.add_xp 'db_meta.vi_data_dictionary', 'ms_description', 'view to see extended properties of database objects'
GO
EXEC db_meta.add_xp 'db_meta.vi_data_dictionary', 'subsystem', 'meta'
GO




IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[db_meta].[schema_change_log]') AND type in (N'U'))
DROP TABLE [db_meta].[schema_change_log]
GO

CREATE TABLE [db_meta].[schema_change_log] (
	[schema_change_log_id] [smallint] IDENTITY(1,1) NOT NULL,
	[major_release_no] [varchar](2) NOT NULL,
	[minor_release_no] [varchar](2) NOT NULL,
	[point_release_no] [varchar](4) NOT NULL,
	[script_name] [varchar](50) NOT NULL,
	[date_applied] [datetime] NOT NULL,
	CONSTRAINT [pk_schema_change_log] PRIMARY KEY CLUSTERED ([schema_change_log_id])
	) 
WITH 
	(DATA_COMPRESSION = PAGE);

INSERT INTO [db_meta].[schema_change_log]
VALUES
	(3, 0, 0, 'popsyn_3_0 release', GETDATE())
GO


-- Add metadata to schema change log
EXEC db_meta.add_xp 'db_meta.schema_change_log', 'ms_description', 'database version and changes tracking table'
GO
EXEC db_meta.add_xp 'db_meta.schema_change_log', 'subsystem', 'meta'
GO
EXEC db_meta.add_xp 'db_meta.schema_change_log.schema_change_log_id', 'ms_description', 'schema_change_log surrogate key'
GO
EXEC db_meta.add_xp 'db_meta.schema_change_log.major_release_no', 'ms_description', 'major release'
GO
EXEC db_meta.add_xp 'db_meta.schema_change_log.minor_release_no', 'ms_description', 'minor release'
GO
EXEC db_meta.add_xp 'db_meta.schema_change_log.point_release_no', 'ms_description', 'point release'
GO
EXEC db_meta.add_xp 'db_meta.schema_change_log.script_name', 'ms_description', 'sql script applied to database'
GO
EXEC db_meta.add_xp 'db_meta.schema_change_log.date_applied', 'ms_description', 'date script was applied'
GO