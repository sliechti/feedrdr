# pg_dump postgres -U sliechti -n feedreader -s -C > prod_schema.sql 
sh dumpschema.sh > ../sql/schemas/feedreader.local.sql
scp root@feedrdr.co:prod_schema.sql ../sql/schemas/feedreader.prod.sql
java -jar apgdiff-2.4/apgdiff-2.4.jar ../sql/schemas/feedreader.prod.sql ../sql/schemas/feedreader.local.sql
java -jar apgdiff-2.4/apgdiff-2.4.jar ../sql/schemas/feedreader.prod.sql ../sql/schemas/feedreader.local.sql > ../sql/schemas/diff.sql
scp ../sql/schemas/diff.sql root@feedrdr.co:
# psql -U sliechti postgres -f diff.sql 
