# Example Sata

## Refreshing the data

```
./get-cities.sh >cities-140491.csv 
```

## Sources

- https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/geonames-all-cities-with-a-population-1000/exports/csv/?delimiters=%3B&lang=en&timezone=Europe%2FZurich&use_labels=true
- https://burntsushi.net/stuff/worldcitiespop.csv

## CSV tools
- https://github.com/BurntSushi/xsv
- https://github.com/wireservice/csvkit`(`brew install csvkit`)

## grid teat

In repository root, run the following command
```
python3 -m http.server 8001
```

- http://localhost:8001/w2ui/?csv=cities-1400.csv
- http://localhost:8001/w2ui/?csv=cities-5000.csv
- http://localhost:8001/w2ui/?csv=cities-10000.csv
- http://localhost:8001/w2ui/?csv=cities-10000.csv
- http://localhost:8001/w2ui/?csv=cities-20000.csv
- http://localhost:8001/w2ui/?csv=cities-140491.csv
