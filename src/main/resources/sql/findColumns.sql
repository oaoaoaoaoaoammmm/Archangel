SELECT c.table_name,
       c.column_name,
       c.data_type,
       COALESCE(NULLIF(ARRAY_AGG(DISTINCT
                       CASE
                           WHEN tc.constraint_type = 'PRIMARY KEY' THEN 'PRIMARY_KEY'
                           WHEN tc.constraint_type = 'FOREIGN KEY' THEN 'FOREIGN_KEY'
                           WHEN tc.constraint_type = 'UNIQUE' THEN 'UNIQUE'
                           WHEN c.is_nullable = 'NO' THEN 'NOT_NULL'
                           WHEN tc.constraint_type IS NOT NULL THEN 'UNKNOWN'
                           END) FILTER (WHERE tc.constraint_type IS NOT NULL OR c.is_nullable = 'NO'),
                       '{}'),
                '{}') AS constraint_types
FROM information_schema.columns c
         LEFT JOIN information_schema.key_column_usage kcu
                   ON c.table_name = kcu.table_name AND c.column_name = kcu.column_name AND
                      c.table_schema = kcu.table_schema
         LEFT JOIN information_schema.table_constraints tc
                   ON kcu.constraint_name = tc.constraint_name AND kcu.table_schema = tc.table_schema
WHERE c.table_schema = ?
GROUP BY c.table_name, c.column_name, c.data_type
ORDER BY c.table_name, c.column_name;
