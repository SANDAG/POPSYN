USE [popsyn]
GO


IF OBJECT_ID (N'[dbo].[target]') IS NOT NULL
DROP TABLE [dbo].[target]

IF OBJECT_ID (N'[dbo].[target_category]') IS NOT NULL
DROP TABLE [dbo].[target_category]

IF OBJECT_ID (N'[dbo].[target_lu_version]') IS NOT NULL
DROP TABLE [dbo].[target_lu_version]

IF OBJECT_ID (N'[dbo].[target_growth_factor]') IS NOT NULL
DROP TABLE [dbo].[target_growth_factor]


CREATE TABLE [dbo].[target_category](
	[id] [smallint] IDENTITY(1,3) NOT NULL,
	[name] [varchar](32) NOT NULL,
	[data_source] [nvarchar](50) NULL,
 CONSTRAINT [PK_target_category] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[target_lu_version](
	[lu_major_version] [smallint] NOT NULL,
	[lu_minor_version] [smallint] NOT NULL,
	[source] [nvarchar](120) NULL,
 CONSTRAINT [PK_target_lu_maj_version] PRIMARY KEY CLUSTERED 
(
	[lu_major_version] ASC,
	[lu_minor_version] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[target](
	[category] [smallint] NOT NULL,
	[geo_type_id] [smallint] NOT NULL,
	[geo_zone] [int] NOT NULL,
	[year] [smallint] NOT NULL,
	[value] [real] NOT NULL,
	[lu_revision_no] [smallint] NOT NULL,
	[lu_major_version] [smallint] NOT NULL,
	[lu_minor_version] [smallint] NOT NULL,
 CONSTRAINT [PK_target_1] PRIMARY KEY CLUSTERED 
(
	[category] ASC,
	[geo_type_id] ASC,
	[geo_zone] ASC,
	[year] ASC,
	[lu_revision_no] ASC,
	[lu_major_version] ASC,
	[lu_minor_version] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[target]  WITH CHECK ADD  CONSTRAINT [FK_target_target_category] FOREIGN KEY([category])
REFERENCES [dbo].[target_category] ([id])
ON UPDATE CASCADE
GO

ALTER TABLE [dbo].[target] CHECK CONSTRAINT [FK_target_target_category]
GO

ALTER TABLE [dbo].[target]  WITH CHECK ADD  CONSTRAINT [FK_target_target_lu_maj_version] FOREIGN KEY([lu_major_version], [lu_minor_version])
REFERENCES [dbo].[target_lu_version] ([lu_major_version], [lu_minor_version])
GO

ALTER TABLE [dbo].[target] CHECK CONSTRAINT [FK_target_target_lu_maj_version]
GO


CREATE TABLE [dbo].[target_growth_factor](
	[category] [int] NOT NULL,
	[geoType] [int] NOT NULL,
	[geoZone] [int] NOT NULL,
	[baseYear] [int] NOT NULL,
	[growthYear] [int] NOT NULL,
	[lu_revision_no] [smallint] NULL,
	[lu_major_version] [smallint] NULL,
	[lu_minor_version] [smallint] NULL,
	[value] [float] NOT NULL,
 CONSTRAINT [PK_growthFactor] PRIMARY KEY CLUSTERED 
(
	[category] ASC,
	[geoType] ASC,
	[geoZone] ASC,
	[baseYear] ASC,
	[growthYear] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO