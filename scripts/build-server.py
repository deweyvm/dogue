#!/usr/bin/env python
import socket
import traceback
import subprocess
import os
import time



def say(s):
    logfile = "/var/log/doge/error.log"
    msg = "Server: " + s
    print(msg)
    with open(logfile, "a") as f:
        f.write(msg)


def send(client, s):
    client.send("Server: " + s + '\0')

def update_server(client):
    kill_server()
    update_server(client)
    run_server()

def restart_server(client):
    if client is not None:
        send(client, "Creating fresh instance")
    kill_server()
    run_server()

def get_last_run(file):
    if (os.path.exists(file)):
        return open(file, "r").read()
    else:
        return ""

def get_last_modified(path):
    return str(time.ctime(os.path.getmtime(path)))

def kill_server():
    say("Killing server")
    proc = subprocess.Popen(['pgrep', 'java'], stdout=subprocess.PIPE)
    out, err = proc.communicate()
    for line in out.splitlines():
        pid = int(line)
        os.kill(pid, 9)

def update_log(run_log, time_modified):
    with open(run_log, "w") as f:
        f.write(time_modified + "\n")

def update_server(client):
    send(client, "Awaiting updated executable")
    run_log = "/home/doge/whatever/last"
    file_to_check = "/home/doge/whatever/whatever_server_jar/jar.tar.gz"
    last_modified = get_last_modified(file_to_check)
    def inner(iters):
        last_run = get_last_run(run_log)
        if (last_run != last_modified):
            send(client, "New version found, updating log")
            update_log(run_log, last_modified)
            return
        elif (iters > 0):
            send(client, "New version not found. Sleeping")
            time.sleep(5)
            inner(iters - 1)
        else:
            send("Error: timeout. Server not updated.")
    inner(10)


def run_server():
    say("Starting server")
    code = subprocess.call(['/home/doge/whatever/dogue-server-run'], shell=True)
    if (code != 0):
        say("Warning: server failed to start for some reason")


def do_command(client, desc, f):
    send(client, desc)
    try:
        f()
        send(client, "Success")
    except:
        send(client, traceback.format_exc())
    client.close()

def main():
    restart_server(None)
    host = ''
    port = 27181
    acceptBacklog = 3
    size = 4096
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind((host, port))
    sock.listen(acceptBacklog)

    while True:
        client, address = sock.accept()
        #todo, accept data until client closes
        data = ""
        next = client.recv(size)
        while '\0' not in next:
            data += next
            next = client.recv(size)
        data += next
        say("Command received: " + data)
        data.replace('\0', '')
        if ("restart" in data):
            do_command(client, "Restarting server", lambda: restart_server(client))
        elif ("update" in data):
            do_command(client, "Updating server", lambda: update_server(client))
        else:
            send(client, "Unknown command \"%s\".\nSelf destruct in 0 seconds" % data)
            client.close()
            exit(0)

if __name__ == '__main__':
    main()
