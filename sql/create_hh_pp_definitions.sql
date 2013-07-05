USE popsyn
GO

-- ================================================================================
-- Author:		Ziying Ouyang
-- Create date: 1/25/2012
-- Description:	create household and person definition tables such as  
--				dwelling and unit type; employment, grade, occupation, race, relationship,
--				person type, and work hour type
--				also create data source table for PUMS data
-- ==================================================================================


IF OBJECT_ID (N'[dbo].[person]') IS NOT NULL
DROP TABLE [dbo].[person]

IF OBJECT_ID (N'[dbo].[household]') IS NOT NULL
DROP TABLE [dbo].[household]

IF OBJECT_ID (N'[dbo].[dwelling_type]') IS NOT NULL
DROP TABLE [dbo].[dwelling_type]


IF OBJECT_ID (N'[dbo].[employment_type]') IS NOT NULL
DROP TABLE [dbo].[employment_type]

IF OBJECT_ID (N'[dbo].[grade_type]') IS NOT NULL
DROP TABLE [dbo].[grade_type]

IF OBJECT_ID (N'[dbo].[occupation_type]') IS NOT NULL
DROP TABLE [dbo].[occupation_type]

IF OBJECT_ID (N'[dbo].[person_type]') IS NOT NULL
DROP TABLE [dbo].[person_type]

IF OBJECT_ID (N'[dbo].[race_type]') IS NOT NULL
DROP TABLE [dbo].[race_type]

IF OBJECT_ID (N'[dbo].[relationship_type]') IS NOT NULL
DROP TABLE [dbo].[relationship_type]

IF OBJECT_ID (N'[dbo].[source]') IS NOT NULL
DROP TABLE [dbo].[source]

IF OBJECT_ID (N'[dbo].[unit_type]') IS NOT NULL
DROP TABLE [dbo].[unit_type]

IF OBJECT_ID (N'[dbo].[work_hour_type]') IS NOT NULL
DROP TABLE [dbo].[work_hour_type]



CREATE TABLE [dbo].[dwelling_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_dwelling_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[employment_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_employment_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[grade_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_grade_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[occupation_type](
	[occupation_id] [smallint] NOT NULL,
	[description] [nvarchar](60) NULL,
 CONSTRAINT [PK_occupation_type] PRIMARY KEY CLUSTERED 
(
	[occupation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[person_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_person_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO




CREATE TABLE [dbo].[race_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_race_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[relationship_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_relationship_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[source](
	[id] [int] IDENTITY(1,3) NOT NULL,
	[year] [smallint] NOT NULL,
	[source_type] [varchar](10) NOT NULL,
 CONSTRAINT [PK_source] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[unit_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_unit_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[work_hour_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_work_hour_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO