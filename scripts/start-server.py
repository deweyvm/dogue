#!/usr/bin/env python

import sys
import os
import subprocess

def println_err(s):
    print(s, file=sys.stderr)


def do_process(args):
    proc = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out,err = procs.communicate()
    lines = out.splitlines()
    errlines = err.splitlines()
    if (len(errlines) > 0):
        for line in errlines:
            println_err(line)
            sys.exit(1)
    if (proc.returncode != 0):
        sys.exit(proc.returncode)
    return lines

# this function has a race condition where if a process is started
# between when we check for duplicates and when we kill the found process
#
# returns the number of processes found matching procname
def check_processes(procname):
    lines = do_process(['pgrep', procname])
    if (len(lines) > 1):
        println_err("Multiple processes detected")
        for line in lines:
            println_err("    " + line)
        println_err("Unable to disambiguate. Aborting")
        sys.exit(1)
    return len(lines)

def kill_all(procname):
    do_process(['killall', procname])
    #we dont care about output, only errors

def start_daemon():
    do_process([
        '/sbin/start-stop-daemon',
        '--background',
        '--make-pidfile',
        '-S',
        '-o',
        '--user', 'doge',
        '--name', 'build-server',
        '--no-close',
        '--chuid', 'doge:doge',
        '--exec', '/bin/bash',
        '--',
        '-c', "python /home/doge/whatever/build-server.py"
    ]

def main():
    procname = sys.argv[1]
    num_processes = check_processes(procname)
    if (num_processes > 0):
        kill_all(procname)
    start_daemon()


#killall $procname &> /dev/null
#/sbin/start-stop-daemon -b -S -o --user doge --name build-server --no-close --chuid doge:doge --exec /bin/bash -- -c "python /home/doge/whatever/build-server.py" > /var/log/doge/error.log 2>&1

if __name__ == '__main__':
    main()
