### CSV data sources
https://public.opendatasoft.com/explore/dataset/geonames-all-cities-with-a-population-1000/export/?disjunctive.cou_name_en&sort=population
(printf "geoname_id\tname\tcountry_code\tcountry_name_en\tpopulation\tmodification_date\tlongitude\tlatitude\n";cat ~/Downloads/geonames-all-cities-with-a-population-1000.csv | tr ";" "\t" |cut -f1,2,7,8,14,18,20| sort -t "$(printf '\t')" -k5rn|sed -e "s/\([0-9]\),/\1\t/"|head -1400) >src/main/resources/db/changelog/data/cities-1400.tsv

https://www.kaggle.com/datasets/tanuprabhu/population-by-country-2020?resource=download
https://datahub.io/core/world-cities#data
https://people.sc.fsu.edu/~jburkardt/data/csv/csv.html

### CSV Tools
https://csvkit.readthedocs.io/en/latest/

csvsql --query "select * from C order by population desc, name" csvfile