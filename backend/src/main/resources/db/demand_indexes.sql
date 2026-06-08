-- Optional indexes for the first version of demand search.
-- LIKE '%keyword%' on description/title usually cannot use a normal B-tree index effectively.
-- idx_demand_type and idx_demand_stat are already created by database/02_create_tables.sql.
CREATE INDEX idx_demand_create_time ON demand(create_time);
CREATE INDEX idx_demand_type_stat_create_time ON demand(type, stat, create_time);
