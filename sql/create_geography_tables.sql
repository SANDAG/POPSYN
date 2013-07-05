USE [data_cafe]
GO

IF OBJECT_ID (N'[dbo].[geography_zone]') IS NOT NULL
DROP TABLE [dbo].[geography_zone]

IF OBJECT_ID (N'[dbo].[geography_type]') IS NOT NULL
DROP TABLE [dbo].[geography_type]

CREATE TABLE [dbo].[geography_type](
	[id] [smallint] IDENTITY(33,3) NOT NULL,
	[name] [varchar](75) NOT NULL,
	[alias] [varchar](20) NOT NULL,
	[eff_year] [smallint] NULL,
 CONSTRAINT [PK_geography_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [con_unique_name_alias_year] UNIQUE NONCLUSTERED 
(
	[name] ASC,
	[alias] ASC,
	[eff_year] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[geography_zone](
	[zone] [int] NOT NULL,
	[geo_type_id] [smallint] NOT NULL,
	[alias] [varchar](75) NULL,
	[shape] [geometry] NOT NULL,
	[centroid] [geometry] NULL,
 CONSTRAINT [PK_geography_zone] PRIMARY KEY CLUSTERED 
(
	[zone] ASC,
	[geo_type_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


ALTER TABLE [dbo].[geography_zone]  WITH CHECK ADD  CONSTRAINT [FK_geography_zone_geography_type] FOREIGN KEY([geo_type_id])
REFERENCES [dbo].[geography_type] ([id])
ON UPDATE CASCADE
GO

ALTER TABLE [dbo].[geography_zone] CHECK CONSTRAINT [FK_geography_zone_geography_type]
GO