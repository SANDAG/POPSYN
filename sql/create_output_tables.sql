USE popsyn
GO

-- ================================================================================
-- Author:		Ziying Ouyang
-- Create date: 1/25/2012
-- Description:	create version, household_discretized and household_allocated tables
--				to store outputs from PopSynII
-- ==================================================================================

IF OBJECT_ID (N'[dbo].[household_discretized]') IS NOT NULL
DROP TABLE [dbo].household_discretized


IF OBJECT_ID (N'[dbo].[household_allocated]') IS NOT NULL
DROP TABLE [dbo].household_allocated


IF OBJECT_ID (N'[dbo].[version]') IS NOT NULL
DROP TABLE [dbo].version

CREATE TABLE [dbo].[version](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[source_id] [int] NOT NULL,
	[max_loop] [int] NOT NULL,
	[convergence_criteria] [float] NOT NULL,
	[major_version] [smallint] NOT NULL,
	[minor_version] [int] NOT NULL,
	[description] [varchar](255) NOT NULL,
	[target_year] [int] NOT NULL,
	[target_geography] [smallint] NOT NULL,
	[balance_geography] [smallint] NOT NULL,
	[start_time] [datetime] NOT NULL,
	[end_time] [datetime] NULL,
	[contact] [varchar](25) NOT NULL,
	[validate_source_id] [int] NULL,
	[validation_count] [int] NULL,
	[target_method] [varchar](25) NULL,
	[regenerate_targets] [bit] NULL,
	[select_zones] [varchar](50) NULL,
	[work_directory] [varchar](100) NULL,
	[bulk_load_tmp_dir] [varchar](100) NULL,
	[allocation_geography] [smallint] NULL,
	[small_value] [bigint] NULL,
	[large_value] [bigint] NULL,
	[puma_start_zone] [smallint] NULL,
	[num_external_zones] [smallint] NULL,
	[hh_allocate_attribute] [varchar](50) NULL,
	[gq_allocate_attribute] [varchar](50) NULL,
	[use_init_weight] [bit] NULL,
	[balance_selection_factor] [float] NULL,
	[balance_all_puma_samples] [bit] NULL,
	[fix_random_seed] [bit] NULL,
	[random_seed] [int] NULL,
	[acs_year] [int] NULL,
	[pop_syn_version] [float] NULL,
 CONSTRAINT [PK_run_version] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

EXEC sys.sp_addextendedproperty @name=N'created_by', @value=N'Daniels, Clint' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'version'
GO

EXEC sys.sp_addextendedproperty @name=N'created_date', @value=N'6/30/2010' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'version'
GO

EXEC sys.sp_addextendedproperty @name=N'description', @value=N'The table stores run parameters and metadata for each run of the PopSyn II tool.' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'version'
GO

EXEC sys.sp_addextendedproperty @name=N'history', @value=N'6/30/2010: Table Created' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'version'
GO

EXEC sys.sp_addextendedproperty @name=N'modified_date', @value=N'6/30/2010' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'version'
GO


CREATE TABLE [dbo].[household_discretized](
	[run_version] [int] NOT NULL,
	[zone] [int] NOT NULL,
	[household_serial_no] [nchar](50) NOT NULL,
	[source_id] [int] NOT NULL,
	[weight] [float] NOT NULL,
	[geo_type_id] [int] NOT NULL,
 CONSTRAINT [PK_household_discretized] PRIMARY KEY CLUSTERED 
(
	[run_version] ASC,
	[zone] ASC,
	[household_serial_no] ASC,
	[source_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[household_discretized]  WITH NOCHECK ADD  CONSTRAINT [FK_household_discretized_version] FOREIGN KEY([run_version])
REFERENCES [dbo].[version] ([id])
GO

ALTER TABLE [dbo].[household_discretized] CHECK CONSTRAINT [FK_household_discretized_version]
GO


CREATE TABLE [dbo].[household_allocated](
	[run_version] [int] NOT NULL,
	[zone] [int] NOT NULL,
	[household_serial_no] [nchar](50) NOT NULL,
	[source_id] [int] NOT NULL,
	[geo_type_i] [int] NOT NULL
) ON [PRIMARY]

GO