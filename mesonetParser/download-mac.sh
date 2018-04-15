#!/bin/bash
#
# download archived data
#
# Note:
# this bash script is setup for Mac OS X 
#
# pattern: 
# src pattern:
#          https://madis-data.noaa.gov/madisPublic1/data/archive/2012/01/01/LDAD/mesonet/netCDF/20120101_0000.gz 
# target:  $here/madis/$type/$yr/$mo-$dy/


#here=`pwd`
here=/Volumes/Data/mesonet
base="https://madis-data.noaa.gov/madisPublic1/data/archive"
type="mesonet"

#for yr in {2001..2003}; do
for yr in {2001..2014}; do
  echo -e "\n--------------------------------------------\n\n${yr}\n"
  #for mo in 01 02 03 04 05 06 07 08 09 10 11 12; do
  #for mo in 01 02 03 ; do
  for mo in 07 08 09 ; do
     for dy in 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31; do
        target="${here}/${yr}/${mo}-${dy}"
        mkdir -m 755 -p ${target}
        if [ -d ${target} ]; then
           echo "downloading for day ${mo}/${dy}/${yr}"
           cd ${target}
           for hr in 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23; do
              fn="${yr}${mo}${dy}_${hr}00.gz"
              src="${base}/${yr}/${mo}/${dy}/LDAD/${type}/netCDF/${fn}"
              #echo $src
              wget  -N --no-check-certificate --no-cache ${src}
              if [ -f ${fn} ]; then
                 chmod 544 ${fn}
              fi
           done
        fi
     done
  done
done
