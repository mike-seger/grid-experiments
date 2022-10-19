# H2 Query examples

SELECT C.*,CC.name FROM CITY47K C,COUNTRY_CODE CC where C.COUNTRY = CC.ALPHA2

call CSVWRITE ( '/tmp/out.csv', 'SELECT C.*,CC.name FROM CITY47K C,COUNTRY_CODE CC where C.COUNTRY = CC.ALPHA2' ) 

create table dupes(id int, combo varchar(255)) as
SELECT ID,LATITUDE||'+'||LONGITUDE as combo FROM CITY47K where population >50000

select count(*) from dupes

select *, LATITUDE||longitude as l0 from CITY47K where id in (
select id from dupes where combo in (
SELECT combo --, COUNT(id) AS DuplicateRanks
FROM dupes
GROUP BY combo
HAVING COUNT(combo)>1
)
)
order by l0

drop table car, city, person, city47k,country_code,country_population,databasechangelog,databasechangeloglock