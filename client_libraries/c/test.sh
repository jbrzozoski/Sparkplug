#!/bin/sh

#echo "Running static example..."
#./test/test_static

echo ""
echo "Running dynamic example..."
#echo "Starting LD_LIBRARY_PATH:  ${LD_LIBRARY_PATH}"
PWD=`pwd`
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${PWD}/lib
#echo "New LD_LIBRARY_PATH:       ${LD_LIBRARY_PATH}"
./test/test_dynamic
