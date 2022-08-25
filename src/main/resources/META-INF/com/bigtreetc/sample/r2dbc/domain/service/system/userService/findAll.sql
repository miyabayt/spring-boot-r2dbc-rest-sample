SELECT
  u.*
FROM
  users u
WHERE
/*%if criteria.firstName != null */
  AND u.first_name = /* criteria.firstName */'john'
/*%end*/
/*%if criteria.lastName != null */
  AND u.last_name = /* criteria.lastName */'doe'
/*%end*/
/*%if criteria.email != null */
  AND u.email = /* criteria.email */'test@example.com'
/*%end*/
