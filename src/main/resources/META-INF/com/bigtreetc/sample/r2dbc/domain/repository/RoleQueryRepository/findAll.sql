SELECT
    r.*
FROM
    roles r
WHERE
    1 = 1
/*%if criteria.id != null */
    AND r.id = /* criteria.id */1
/*%end*/
/*%if criteria.roleCode != null */
    AND r.role_code = /* criteria.roleCode */'x'
/*%end*/
/*%if criteria.roleName != null */
    AND r.role_name LIKE /* @infix(criteria.roleName) */'x'
/*%end*/
ORDER BY
    r.id ASC
