#!/bin/bash
#
# To copy from the archive (USB drive) a vertical sample set by 
# month-day-hour (MM-dd_HH) for all years

# where the data resides
ARCH=/Volumes/Data/mesonet

# copy to
if [ "$#" -ne 1 ]; then
   echo -e "\nSaving to default location: $out"
   out=~/mesonet-vertical-out
else
   out=$1
   # TODO test if $1 is valid
fi

# search for 
#
# TODO add args to accept MON, DAY, and HR (24hr time)

MON=06
DAY=21
HR=0800

# ---------------------------------------------------------


if [ ! -d "$out" ]; then
   echo -e "Creating ${out}"
   mkdir -p ${out}
else
   echo "${out} exists..."
fi

echo -e "\ncopying...\n"
for yr in {2001..2014}; do 
   fn=${yr}${MON}${DAY}_${HR}.gz
   here=${ARCH}/${yr}/${MON}-${DAY}/${fn}
   if [ -f ${here} ] && [ ! -f ${out}/${fn} ] ; then
      echo "copying ${fn}"
      cp ${here} ${out}/.
      chmod 440 ${out}/${fn}
   fi
done

#dirout=`basename ${out}`
#tar cf ${out}.tar.bz2 ${out}

echo -e "\nDone\n"


