SELECT
    h.*
FROM
    holidays h
WHERE
    1 = 1
/*%if criteria.id != null */
    AND h.id = /* criteria.id */1
/*%end*/
/*%if criteria.holidayName != null */
    AND h.holiday_name LIKE /* @infix(criteria.holidayName) */'john'
/*%end*/
ORDER BY
    h.holiday_date asc
    , h.holiday_name ASC
