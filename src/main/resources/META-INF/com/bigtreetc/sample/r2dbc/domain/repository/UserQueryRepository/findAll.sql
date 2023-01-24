SELECT
    u.*
FROM
    users u
WHERE
    1 = 1
/*%if criteria.id != null */
    AND u.id = /* criteria.id */1
/*%end*/
/*%if criteria.email != null */
    AND u.email = /* criteria.email */'aaaa@bbbb.com'
/*%end*/
/*%if criteria.firstName != null */
    AND u.first_name LIKE /* @infix(criteria.firstName) */'john'
/*%end*/
/*%if criteria.lastName != null */
    AND u.last_name LIKE /* @infix(criteria.lastName) */'doe'
/*%end*/
/*%if criteria.tel != null */
    AND u.tel LIKE /* @prefix(criteria.tel) */'0901234'
/*%end*/
/*%if criteria.zip != null */
    AND u.zip LIKE /* @prefix(criteria.zip) */'10600'
/*%end*/
/*%if criteria.address != null */
    AND u.address LIKE /* @infix(criteria.address) */'東京都港区'
/*%end*/
/*%if criteria.onlyNullAddress */
    AND u.address IS NULL
/*%end*/
ORDER BY
    u.id ASC
