wget -qO-  "https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/geonames-all-cities-with-a-population-1000/exports/csv/?delimiters=%3B&lang=en&timezone=Europe%2FZurich&use_labels=true" |\
	grep -v "12156429;Umm" | \
	sed '1s/^\xEF\xBB\xBF//;1 s/ /_/g;s/;/\t/g' | \
	mlr --itsv --ocsv cut -o -f Geoname_ID,Name,Population,ASCII_Name,Country_Code,Country_name_EN,Modification_date,Coordinates | \
	sed '1 s/_/ /g;1 s/Modification date/Modified/g;s/, /,/;
		;s/Country Code/isoCode/;s/ name EN//;s/Geoname ID/GeonameID/;
		;1 s/,Name,/,City,/;1 s/ASCII Name/AsciiName/' \
	| mlr --csv sort -nr Population \
	| awk 'BEGIN{FS=OFS=","}
       		NR==1 { print "Rank," $0; next }
       		{ print NR-1 "," $0 }'
