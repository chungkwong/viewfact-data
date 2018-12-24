# viewfact-data

Supplement datasets for the search engine viewfact

## Usage

'''SQL
INSERT INTO DATASETS(URL,TITLE,AUTHOR,ACTION,FREQUENCY) VALUES('https://raw.githubusercontent.com/chungkwong/viewfact-data/master/data/index.json','Index of viewfact selected datasets','chungkwong','[{"step":"from-url"},{"step":"to-text"},{"step":"parse-json","path":"$[*]"},{"step":"to-datasets"}]',86400000);
'''