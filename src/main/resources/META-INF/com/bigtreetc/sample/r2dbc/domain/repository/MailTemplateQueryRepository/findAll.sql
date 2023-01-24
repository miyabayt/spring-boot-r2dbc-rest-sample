SELECT
    mt.*
FROM
    mail_templates mt
WHERE
    1 = 1
/*%if criteria.id != null */
    AND mt.id = /* criteria.id */1
/*%end*/
/*%if criteria.templateCode != null */
    AND mt.template_code = /* criteria.templateCode */'x'
/*%end*/
ORDER BY
    mt.id ASC
    , mt.template_code ASC
