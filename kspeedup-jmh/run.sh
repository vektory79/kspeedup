#!/bin/bash
#java -jar target/benchmarks.jar recursion.* -gc true -f 2 -i 20 -wi 20 -t 8 -bm sample
#java -jar target/benchmarks.jar recursion.* -gc true -f 2 -i 20 -wi 20 -t 50 -bm sample
#java -jar target/benchmarks.jar recursion.* -gc true -f 2 -i 10 -wi 10 -t 8 -bm sample -rf csv -rff recursion-t8-qqq-1000ns.csv
#java -jar target/benchmarks.jar recursion.* -gc true -f 2 -i 10 -wi 10 -t 8 -bm sample
#java -jar target/benchmarks.jar inclusion.* -gc true -f 2 -i 10 -wi 10 -t 8 -bm sample -rf csv -rff result-max.csv
#java -jar target/benchmarks.jar -jvmArgs -Xms20m -jvmArgs -Xmx50m inclusion.* -gc true -f 2 -i 10 -wi 10 -t 8 -bm sample -rf csv -rff result-50.csv
#java -jar target/benchmarks.jar -jvmArgs -Xms5m -jvmArgs -Xmx30m inclusion.* -gc true -f 2 -i 10 -wi 10 -t 8 -bm sample -rf csv -rff result-30.csv

java -jar target/benchmarks.jar sequential.* -gc true -f 2 -i 10 -wi 10 -t 16 -bm sample  -rf csv -rff sequential-jre8-t16.csv
