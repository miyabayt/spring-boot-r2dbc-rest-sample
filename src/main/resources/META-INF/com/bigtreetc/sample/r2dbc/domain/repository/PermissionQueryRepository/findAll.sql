SELECT
    p.*
FROM
    permissions p
WHERE
    1 = 1
/*%if criteria.id != null */
    AND permission_id = /* criteria.id */1
/*%end*/
ORDER BY
    p.id ASC
