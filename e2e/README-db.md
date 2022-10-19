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

drop table car, city, person, city47k,country_code,country_population,DATABASECHANGELOG,DATABASECHANGELOGLOCK;


# mysql

LOAD DATA LOCAL INFILE 'all.csv' INTO TABLE city CHARACTER SET UTF8 FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS (city,population,country,iso_country,geo_id,latitude,longitude,modified);

SELECT geo_id
FROM city
WHERE city like '%?%'
INTO LOCAL OUTFILE 'bad.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

docker exec -it adminux-mysql mysql -uroot -pmysql   <<< "select database(); show users;"

## gez schema encoding
SELECT default_character_set_name FROM information_schema.SCHEMATA 

## get table encoding
SELECT CCSA.character_set_name FROM information_schema.`TABLES` T, information_schema.`COLLATION_CHARACTER_SET_APPLICABILITY` CCSA WHERE CCSA.collation_name = T.table_collation  AND T.table_schema = "db" AND T.table_name = "city";

## get column encoding
SELECT character_set_name FROM information_schema.`COLUMNS` WHERE table_schema = "db" AND table_name = "city" AND column_name = "city";
