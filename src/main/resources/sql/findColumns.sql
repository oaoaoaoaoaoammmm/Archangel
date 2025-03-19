SELECT c.table_name,
       c.column_name,
       c.data_type,
       COALESCE(
               NULLIF(
                               array_agg(DISTINCT
                               CASE
                                   WHEN tc.constraint_type = 'PRIMARY KEY' THEN 'PRIMARY_KEY'
                                   WHEN tc.constraint_type = 'FOREIGN KEY' THEN 'FOREIGN_KEY'
                                   WHEN tc.constraint_type = 'UNIQUE' THEN 'UNIQUE'
                                   WHEN nn.is_not_null IS NOT NULL THEN 'NOT_NULL'
                                   WHEN tc.constraint_type IS NOT NULL THEN 'UNKNOWN'
                                   END
                                        )
                               FILTER (WHERE tc.constraint_type IS NOT NULL OR nn.is_not_null IS NOT NULL),
                               '{}'
               ),
               '{}'
       ) AS constraint_types
FROM information_schema.columns c
         LEFT JOIN information_schema.key_column_usage kcu
                   ON c.table_name = kcu.table_name
                       AND c.column_name = kcu.column_name
                       AND c.table_schema = kcu.table_schema
         LEFT JOIN information_schema.table_constraints tc
                   ON kcu.constraint_name = tc.constraint_name
                       AND kcu.table_schema = tc.table_schema
         LEFT JOIN (SELECT table_name, column_name, table_schema, 'NOT_NULL' AS is_not_null
                    FROM information_schema.columns
                    WHERE is_nullable = 'NO') nn
                   ON c.table_name = nn.table_name
                       AND c.column_name = nn.column_name
                       AND c.table_schema = nn.table_schema
WHERE c.table_schema = ?
GROUP BY c.table_name, c.column_name, c.data_type, nn.is_not_null
ORDER BY c.table_name, c.column_name;
