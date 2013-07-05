USE [popsyn]
GO

-- ========================================================================
-- Author:		Clint Daniels
-- Create date: Jan, 2010
-- Description:	insert discretized household from temp csv file to table 
-- ========================================================================

ALTER PROCEDURE [dbo].[proc_insert_household_allocated]
	@filename as varchar(200)

AS
BEGIN
DECLARE @sql nvarchar(4000)
SELECT @sql =
'bulk insert [popsyn].[dbo].[household_allocated] 
  FROM '''+@filename+'''
  WITH
      ( FIELDTERMINATOR = '','',
        ROWTERMINATOR = ''' + char(10) + ''')'
EXEC(@sql)
END


GO
