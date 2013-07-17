USE [popsyn]
GO



-- ========================================================================
-- Author:		Wu Sun
-- Create date: April 26, 2011
-- Description:	insert discretized household from temp csv file to table 
-- ========================================================================
ALTER PROCEDURE [dbo].[proc_insert_household_discretized]
	@filename as varchar(200)

AS
BEGIN
DECLARE @sql nvarchar(4000)
SELECT @sql =
'bulk insert [popsyn].[dbo].[household_discretized] 
  FROM '''+@filename+'''
  WITH
      ( FIELDTERMINATOR = '','',
        ROWTERMINATOR = ''' + char(10) + ''')'
EXEC(@sql)
END

GO