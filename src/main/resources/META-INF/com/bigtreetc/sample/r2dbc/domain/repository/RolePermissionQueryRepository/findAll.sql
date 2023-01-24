SELECT
    rp.*
FROM
    role_permissions rp
WHERE
    1 = 1
/*%if criteria.roleCode != null */
    AND rp.role_code = /* criteria.roleCode */'admin'
/*%end*/
/*%if criteria.roleCodes != null */
    AND rp.role_code IN /* criteria.roleCodes */('admin', 'user')
/*%end*/
/*%if criteria.isEnabled != null */
    AND rp.is_enabled = /* criteria.isEnabled */1
/*%end*/
ORDER BY
    rp.role_code ASC
    , rp.permission_code ASC
