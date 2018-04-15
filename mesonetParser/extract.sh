#!/bin/bash
#
# This script is used to extract data
#

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

JAVA_MAIN='gash.obs.madis.MesonetProcessor'
JAVA_ARGS="/Users/asonvane/Documents/data/2013/01-02/20130102_0100.gz ./catalog.csv ./output"

# see http://java.sun.com/performance/reference/whitepapers/tuning.html
JAVA_TUNE='-Xms256m -Xmx1000m'


java ${JAVA_TUNE} -cp .:${here}/parser/lib/'*':${here}/parser/bin ${JAVA_MAIN} ${JAVA_ARGS} 
