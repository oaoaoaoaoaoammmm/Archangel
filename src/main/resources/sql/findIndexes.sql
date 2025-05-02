SELECT
    i.relname AS index_name,
    CASE
        WHEN am.amname IN ('btree', 'hash') THEN UPPER(am.amname)
        ELSE 'UNKNOWN'
        END AS index_type,
    ix.indisunique AS is_unique,
    ix.indisprimary AS is_primary,
    pg_get_expr(ix.indpred, ix.indrelid) AS index_predicate,
    pg_get_indexdef(ix.indexrelid) AS index_definition,
    CASE
        WHEN ix.indkey::text = '0' THEN
            pg_get_expr(ix.indexprs, ix.indrelid)
        ELSE
            array_to_string(array_agg(a.attname), ', ')
        END AS columns
FROM pg_index ix
         JOIN pg_class t ON t.oid = ix.indrelid
         JOIN pg_class i ON i.oid = ix.indexrelid
         JOIN pg_namespace n ON n.oid = t.relnamespace
         JOIN pg_am am ON i.relam = am.oid
         LEFT JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY (ix.indkey) AND a.attnum > 0
WHERE t.relkind = 'r'
  AND n.nspname = ?
  AND t.relname = ?
GROUP BY i.relname, am.amname, ix.indisunique, ix.indisprimary, ix.indpred,
         ix.indexrelid, ix.indrelid, ix.indkey, ix.indexprs
ORDER BY i.relname;