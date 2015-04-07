/*

Assumes existence of the following in the database:
	[db_meta].[add_xp]
	objects created by popsyn_tables.sql

Creates all data dictionary entries for objects and columns created by popsyn_tables.sql

Author: Gregor Schroeder

*/




-- base schemas
EXECUTE [db_meta].[add_xp] 'ref', 'ms_description', 'schema to hold lookup and reference tables'
EXECUTE [db_meta].[add_xp] 'popsyn', 'ms_description', 'schema to hold popsyn output data'
EXECUTE [db_meta].[add_xp] 'popsyn_input', 'ms_description', 'schema to hold popsyn input data'
EXECUTE [db_meta].[add_xp] 'popsyn_staging', 'ms_description', 'schema to hold staging tables used by popsyn'
GO




-- ref tables
EXECUTE [db_meta].[add_xp] 'ref.gq_type', 'subsystem', 'reference'
EXECUTE [db_meta].[add_xp] 'ref.gq_type', 'ms_description', 'gq_type lookup'

EXECUTE [db_meta].[add_xp] 'ref.hh_type', 'subsystem', 'reference'
EXECUTE [db_meta].[add_xp] 'ref.hh_type', 'ms_description', 'hh_type lookup'

EXECUTE [db_meta].[add_xp] 'ref.lu_version', 'subsystem', 'reference'
EXECUTE [db_meta].[add_xp] 'ref.lu_version', 'ms_description', 'land use version table to manage inputs from land use modelers manually inserted into when necessary with hardcoded values'

EXECUTE [db_meta].[add_xp] 'ref.popsyn_data_source', 'subsystem', 'reference'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_data_source', 'ms_description', 'data sources used to populate base data used by popsyn program'

EXECUTE [db_meta].[add_xp] 'ref.popsyn_run', 'subsystem', 'reference'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run', 'ms_description', 'popsyn runs with relevant settings'

EXECUTE [db_meta].[add_xp] 'ref.target_category', 'subsystem', 'reference'
EXECUTE [db_meta].[add_xp] 'ref.target_category', 'ms_description', 'household and demographic control categories'
GO




-- ref columns
EXECUTE [db_meta].[add_xp] 'ref.gq_type.gq_type_id', 'ms_description', 'gq_type surrogate key'
EXECUTE [db_meta].[add_xp] 'ref.gq_type.gq_type_desc', 'ms_description', 'gq_type description'

EXECUTE [db_meta].[add_xp] 'ref.hh_type.hh_type_id', 'ms_description', 'hh_type surrogate key'
EXECUTE [db_meta].[add_xp] 'ref.hh_type.hh_type_desc', 'ms_description', 'hh_type description'

EXECUTE [db_meta].[add_xp] 'ref.lu_version.lu_version_id', 'ms_description', 'lu_version surrogate key'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.lu_major_version', 'ms_description', 'hardcoded land use major version'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.lu_minor_version', 'ms_description', 'hardcoded land use minor version'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.lu_scenario_id', 'ms_description', 'scenario within land use forecast major and minor version'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.increment', 'ms_description', 'increment within scenario within land use forecast major and minor version'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.minor_geography_type_id', 'ms_description', 'geography_type_id of minor geography unit'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.middle_geography_type_id', 'ms_description', 'geography_type_id of middle geography unit'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.major_geography_type_id', 'ms_description', 'geography_type_id of major geography unit'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.lu_scenario_desc', 'ms_description', 'description of the land use version scenario'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.popsyn_targets_created', 'ms_description', 'date popsyn control targets were created for this land use version'
EXECUTE [db_meta].[add_xp] 'ref.lu_version.mgra_based_input_file_created', 'ms_description', 'date mgra based input file was created for this land use version'

EXECUTE [db_meta].[add_xp] 'ref.popsyn_data_source.popsyn_data_source_id', 'ms_description', 'popsyn_data_source surrogate key'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_data_source.popsyn_data_source_desc', 'ms_description', 'popsyn_data_source description'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_data_source.popsyn_data_source_inputs_created', 'ms_description', 'date data from the popsyn_data_source was created'

EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.popsyn_run_id', 'ms_description', 'popsyn_run surrogate key'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.popsyn_data_source_id', 'ms_description', 'popsyn_data_source surrogate key'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.lu_version_id', 'ms_description', 'lu_version surrogate key'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.user_name', 'ms_description', 'user who ran popsyn'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.start_time', 'ms_description', 'start time'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.end_time', 'ms_description', 'end time'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.validated', 'ms_description', 'date validated'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.puma', 'ms_description', 'puma of single zone popsyn run'
EXECUTE [db_meta].[add_xp] 'ref.popsyn_run.taz', 'ms_description', 'taz of single zone popsyn run'

EXECUTE [db_meta].[add_xp] 'ref.target_category.target_category_id', 'ms_description', 'target_category surrogate key'
EXECUTE [db_meta].[add_xp] 'ref.target_category.target_category_col_nm', 'ms_description', 'target_category land use column name'
EXECUTE [db_meta].[add_xp] 'ref.target_category.target_category_desc', 'ms_description', 'target_category description'
GO




-- popsyn_input tables
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets', 'ms_description', 'control targets for balancing synthetic population'

EXECUTE [db_meta].[add_xp] 'popsyn_input.hh', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh', 'ms_description', 'households used to create synthetic population'

EXECUTE [db_meta].[add_xp] 'popsyn_input.person', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person', 'ms_description', 'persons used to create synthetic population'
GO




-- popsyn_input columns
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets.control_targets_id', 'ms_description', 'control_targets surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets.lu_version_id', 'ms_description', 'lu_version surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets.target_category_id', 'ms_description', 'target_category surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets.geography_zone_id', 'ms_description', 'geography_zone surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets.value', 'ms_description', 'control target value'

EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.popsyn_data_source_id', 'ms_description', 'popsyn_data_source surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.hh_id', 'ms_description', 'hh surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.region', 'ms_description', 'region geography_zone zone'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.puma', 'ms_description', 'puma geography_zone zone from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.wgtp', 'ms_description', 'housing/gq person weight from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.serialno', 'ms_description', 'housing unit/gq person serial number from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.np', 'ms_description', 'number of persons from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.hh_income_adj', 'ms_description', 'hh income adjusted to 2010 dollars'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.bld', 'ms_description', 'units in structure from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.workers', 'ms_description', 'number of workers'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.veh', 'ms_description', 'vehicle (1 ton or less) available from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.hht', 'ms_description', 'household/family type from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.gq_type_id', 'ms_description', 'gq_type surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.hh_type_id', 'ms_description', 'hh_type surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.hh.hh_child', 'ms_description', 'child indicator'

EXECUTE [db_meta].[add_xp] 'popsyn_input.person.popsyn_data_source_id', 'ms_description', 'popsyn_data_source surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.person_id', 'ms_description', 'person surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.hh_id', 'ms_description', 'hh surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.puma', 'ms_description', 'puma geography_zone zone from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.wgtp', 'ms_description', 'housing/gq person weight from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.serialno', 'ms_description', 'housing unit/gq person serial number from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.sporder', 'ms_description', 'person number from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.agep', 'ms_description', 'age from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.sex', 'ms_description', 'sex from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.wkhp', 'ms_description', 'wkhp from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.esr', 'ms_description', 'employment status recode from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.schg', 'ms_description', 'grade level attending from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.employed', 'ms_description', 'employment indicator'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.wkw', 'ms_description', 'weeks worked during past 12 months from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.mil', 'ms_description', 'military status from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.schl', 'ms_description', 'educational attainment from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.indp02', 'ms_description', ' from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.indp07', 'ms_description', ' from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.occp02', 'ms_description', ' from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.occp10', 'ms_description', ' from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.socp00', 'ms_description', ' from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.socp10', 'ms_description', ' from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.gq_type_id', 'ms_description', 'gq_type surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.soc', 'ms_description', ' from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.occp', 'ms_description', 'occupation recode 2010 from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.hisp', 'ms_description', 'recoded detailed hispanic origin from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.rac1p', 'ms_description', 'recoded detailed race code from pums'
EXECUTE [db_meta].[add_xp] 'popsyn_input.person.popsyn_race_id', 'ms_description', 'popsyn_race surrogate key'
GO




-- popsyn tables
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_hh', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_hh', 'ms_description', 'synthetic population household results'

EXECUTE [db_meta].[add_xp] 'popsyn.synpop_person', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_person', 'ms_description', 'synthetic population person results'
GO




-- popsyn table columns
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_hh.popsyn_run_id', 'ms_description', 'popsyn_run surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_hh.synpop_hh_id', 'ms_description', 'synpop_hh surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_hh.hh_id', 'ms_description', 'hh surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_hh.final_weight', 'ms_description', 'final balancing weight assigned by java program'

EXECUTE [db_meta].[add_xp] 'popsyn.synpop_person.popsyn_run_id', 'ms_description', 'popsyn_run surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_person.synpop_person_id', 'ms_description', 'synpop_person surrogate key'
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_person.synpop_hh_id', 'ms_description', 'synpop_hh surrogate key'
GO
