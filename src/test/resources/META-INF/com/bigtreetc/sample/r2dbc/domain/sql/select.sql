SELECT
  *
FROM
  system_admin
WHERE
/*%if criteria.name != null */
  AND name = /* criteria.name */'test'
/*%end*/
/*%if criteria.email != null */
  AND email = /* criteria.email */'a@bigtreetc.com'
/*%end*/
