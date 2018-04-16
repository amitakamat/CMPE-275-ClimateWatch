#!/bin/bash
#author:Jing Ma

cd weather
# Get the latest and last data files(the newest two)
LATEST_FILE=`ls -ltm1 *.tbl | head -1`
LAST_FILE=`ls -ltm1 *.tbl | head -2 | tail -1`

echo "Latest file: $LATEST_FILE"
echo "Last file: $LAST_FILE"

# Sort
LATEST_SORTED_FILE="$LATEST_FILE.sorted"
LAST_SORTED_FILE="$LAST_FILE.sorted"

sort -u $LATEST_FILE > $LATEST_SORTED_FILE
sort -u $LAST_FILE > $LAST_SORTED_FILE

echo "Latest sorted file: $LATEST_SORTED_FILE"
echo "Last sorted file: $LAST_SORTED_FILE"

# Diff
UNIQ_DATA="latest_data.tbl"
comm -23 $LATEST_SORTED_FILE $LAST_SORTED_FILE > $UNIQ_DATA

echo "Latest unique data file for weather: $UNIQ_DATA"
#weather data Uniq file is created

cd ..
cd meta
# Get the latest and last data files(the newest two)
LATEST_FILE_meta=`ls -ltm1 *.tbl | head -1`
LAST_FILE_meta=`ls -ltm1 *.tbl | head -2 | tail -1`

echo "Latest file: $LATEST_FILE_meta"
echo "Last file: $LAST_FILE_meta"

# Sort
LATEST_SORTED_FILE_meta="$LATEST_FILE_meta.sorted"
LAST_SORTED_FILE_meta="$LAST_FILE_meta.sorted"

sort -u $LATEST_FILE_meta > $LATEST_SORTED_FILE_meta
sort -u $LAST_FILE_meta > $LAST_SORTED_FILE_meta

echo "Latest sorted file: $LATEST_SORTED_FILE_meta"
echo "Last sorted file: $LAST_SORTED_FILE_meta"

# Diff
UNIQ_DATA_meta="latest_data.tbl"
comm -23 $LATEST_SORTED_FILE_meta $LAST_SORTED_FILE_meta > $UNIQ_DATA_meta

echo "Latest unique data file for weather: $UNIQ_DATA_meta"

cd ..
echo "Begin to run java ..."
# Run java here
# add script to run java
java -jar HibernateETL.jar meta/$UNIQ_DATA_meta weather/$UNIQ_DATA

echo "Running java ..."

# Clean
cd weather
echo "Cleaning weather..."
rm $LATEST_SORTED_FILE $LAST_SORTED_FILE $UNIQ_DATA

cd ..
cd meta
echo "Cleaning meta..."
rm $LATEST_SORTED_FILE_meta $LAST_SORTED_FILE_meta $UNIQ_DATA_meta

echo "Done!"
