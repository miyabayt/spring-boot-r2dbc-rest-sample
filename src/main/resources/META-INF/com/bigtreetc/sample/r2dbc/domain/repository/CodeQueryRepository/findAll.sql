SELECT
    c.*
    , cc.category_name
FROM
    codes c
    INNER JOIN code_categories cc
        ON c.category_code = cc.category_code
WHERE
    1 = 1
/*%if criteria.id != null */
    AND c.id = /* criteria.id */1
/*%end*/
/*%if criteria.categoryCode != null */
    AND c.category_code = /* criteria.categoryCode */'GNR0001'
/*%end*/
/*%if criteria.codeValue != null */
    AND c.code_value = /* criteria.codeValue */'01'
/*%end*/
ORDER BY
    c.id ASC
    , cc.category_code ASC
    , c.display_order ASC
