#!/usr/bin/env bash
host="doge@dogue.in"
jarloc=/cygdrive/c/Users/M/Desktop/whatever/whatever/out/artifacts
lib=$jarloc/whatever_common_jar/whatever-common.jar
exe=$jarloc/whatever_server_jar/whatever-server.jar
dest=whatever_bin
stamp=timestamp
cmd="scp -C $exe $lib $host:$dest"
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
