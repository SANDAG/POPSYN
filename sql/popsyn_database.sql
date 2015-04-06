USE [master]
GO

CREATE DATABASE $(db_name) ON  PRIMARY 
( NAME = N'$(db_name)', FILENAME = N'$(db_path)$(db_name).mdf' , SIZE = 5GB , MAXSIZE = 5GB , FILEGROWTH = 0),
 FILEGROUP [fg_main]  DEFAULT
( NAME = N'$(db_name)_1', FILENAME = N'$(db_path)$(db_name)_1.ndf' , SIZE = 10GB , MAXSIZE = 10GB , FILEGROWTH = 0),
( NAME = N'$(db_name)_2', FILENAME = N'$(db_path)$(db_name)_2.ndf' , SIZE = 10GB , MAXSIZE = 10GB , FILEGROWTH = 0),
( NAME = N'$(db_name)_3', FILENAME = N'$(db_path)$(db_name)_3.ndf' , SIZE = 10GB , MAXSIZE = 10GB , FILEGROWTH = 0)
 LOG ON 
( NAME = N'$(db_name)_log', FILENAME = N'$(log_path)$(db_name).ldf' , SIZE = 10GB , MAXSIZE = 10GB , FILEGROWTH = 0 )
GO

ALTER DATABASE $(db_name) SET COMPATIBILITY_LEVEL = 120
GO

IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC $(db_name).[dbo].[sp_fulltext_database] @action = 'enable'
end
GO

ALTER DATABASE $(db_name) SET ANSI_NULL_DEFAULT OFF 
GO

ALTER DATABASE $(db_name) SET ANSI_NULLS OFF 
GO

ALTER DATABASE $(db_name) SET ANSI_PADDING OFF 
GO

ALTER DATABASE $(db_name) SET ANSI_WARNINGS OFF 
GO

ALTER DATABASE $(db_name) SET ARITHABORT OFF 
GO

ALTER DATABASE $(db_name) SET AUTO_CLOSE OFF 
GO

ALTER DATABASE $(db_name) SET AUTO_CREATE_STATISTICS
GO

ALTER DATABASE $(db_name) SET AUTO_SHRINK OFF 
GO

ALTER DATABASE $(db_name) SET AUTO_UPDATE_STATISTICS ON 
GO

ALTER DATABASE $(db_name) SET CURSOR_CLOSE_ON_COMMIT OFF 
GO

ALTER DATABASE $(db_name) SET CURSOR_DEFAULT  GLOBAL 
GO

ALTER DATABASE $(db_name) SET CONCAT_NULL_YIELDS_NULL OFF 
GO

ALTER DATABASE $(db_name) SET NUMERIC_ROUNDABORT OFF 
GO

ALTER DATABASE $(db_name) SET QUOTED_IDENTIFIER OFF 
GO

ALTER DATABASE $(db_name) SET RECURSIVE_TRIGGERS OFF 
GO

ALTER DATABASE $(db_name) SET  DISABLE_BROKER 
GO

ALTER DATABASE $(db_name) SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO

ALTER DATABASE $(db_name) SET DATE_CORRELATION_OPTIMIZATION OFF 
GO

ALTER DATABASE $(db_name) SET TRUSTWORTHY OFF 
GO

ALTER DATABASE $(db_name) SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO

ALTER DATABASE $(db_name) SET PARAMETERIZATION SIMPLE 
GO

ALTER DATABASE $(db_name) SET READ_COMMITTED_SNAPSHOT OFF 
GO

ALTER DATABASE $(db_name) SET HONOR_BROKER_PRIORITY OFF 
GO

ALTER DATABASE $(db_name) SET  READ_WRITE 
GO

ALTER DATABASE $(db_name) SET RECOVERY SIMPLE 
GO

ALTER DATABASE $(db_name) SET  MULTI_USER 
GO

ALTER DATABASE $(db_name) SET PAGE_VERIFY CHECKSUM  
GO

ALTER DATABASE $(db_name) SET DB_CHAINING OFF 
GO