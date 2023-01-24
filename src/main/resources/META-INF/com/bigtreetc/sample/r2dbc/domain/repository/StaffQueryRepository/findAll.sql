SELECT
    s.*
FROM
    staffs s
WHERE
    1 = 1
/*%if criteria.id != null */
    AND s.id = /* criteria.id */1
/*%end*/
/*%if criteria.lastName != null */
    AND s.last_name LIKE /* @infix(criteria.lastName) */'john'
/*%end*/
/*%if criteria.firstName != null */
    AND s.first_name LIKE /* @infix(criteria.firstName) */'john'
/*%end*/
/*%if criteria.email != null */
    AND s.email = /* criteria.email */'aaaa@bbbb.com'
/*%end*/
ORDER BY
    s.id ASC
