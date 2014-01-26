#!/usr/bin/env bash

# Copies the latest build to the remote server as well as a timestamp file to
# indicate when the last copy was made.
#
# Usage:
#     local $ sh copy-files.sh

host="doge@dogue.in"
jarloc=/cygdrive/c/Users/M/Desktop/dogue/dogue/out/artifacts
lib=$jarloc/dogue_common_jar/dogue-common.jar
starfire=$jarloc/starfire_jar/starfire.jardest=dogue_bin
stamp=timestamp
cmd="scp -C $starfire $lib $host:$dest"
echo Copying jars
echo $cmd
$cmd && \
echo Creating timestamp && \
touch timestamp && \
echo Sending timestamp &&\
scp timestamp $host:$dest

if [[ $? -ne 0 ]] ; then
    echo Failed
    exit 1
fi
echo Done
