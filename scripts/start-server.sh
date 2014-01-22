#!/usr/bin/env bash
procname=python
user=doge
num=`pgrep $procname | wc -l`
if [[ $num -gt 1 ]] ; then
    echo "Found more than one process to kill:"
    pgrep $procname
    echo "Cannot disambiguate. Aborting."
    exit 1
fi
killall $procname &> /dev/null
/sbin/start-stop-daemon --background --start -o --user $user --name build-server --no-close --chuid doge:doge --exec /bin/bash -- -c "python /home/doge/whatever/build-server.py" > /var/log/doge/error.log 2>&1

#/sbin/start-stop-daemon -b -S --user doge --name dogue-server --chuid doge --exec /usr/bin/java -- -jar /home/doge/whatever/whatever_server_jar/whatever-server.jar
