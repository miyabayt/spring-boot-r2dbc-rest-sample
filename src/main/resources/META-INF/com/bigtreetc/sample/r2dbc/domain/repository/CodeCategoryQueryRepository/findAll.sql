SELECT
    cc.*
FROM
    code_categories cc
WHERE
    1 = 1
/*%if criteria.id != null */
    AND cc.id = /* criteria.id */1
/*%end*/
/*%if criteria.categoryCode != null */
    AND cc.category_code = /* criteria.categoryCode */'x'
/*%end*/
ORDER BY
    cc.id ASC
    , cc.category_code ASC
