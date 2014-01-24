#!/usr/bin/env python

# Communicates with the remote starfire instance without the need to log in
# to the remote server
#
# Usage:
#     local $ python server-remote.py <commands>
import socket
import time
import sys

def main():
    sys.argv.pop(0)
    command = " ".join(sys.argv)

    encoding = 'CP437'
    host = 'dogue.in'
    port = 27181
    acceptBacklog = 3
    size = 4096

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))

    sock.send(bytes(command + '\0', encoding))
    while True:
        data = sock.recv(size).decode(encoding)
        if (data == ""):
            break
        s = data.split('\0')
        last = s.pop(len(s) - 1)
        for line in s:
            print(line)
        data = last




if __name__ == '__main__':
    main()


