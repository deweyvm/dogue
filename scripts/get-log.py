#!/usr/bin/env python
#
# Gets the most recent log file in the /var/log directory for a given
# subdirectory.
#
# Usage:
#     python get-log.py <name>
#
import sys
import time
import os
import traceback
DEBUG=False

def main():
    global DEBUG
    loc = "/var/log/" + sys.argv[1]
    DEBUG = "--debug" in sys.argv
    try:
        distance = int(sys.argv[2])
    except:
        distance = 1400000000

    onlyfiles = [ f for f in os.listdir(loc) if os.path.isfile(os.path.join(loc,f)) ]
    now = time.time()
    all_matching = []
    for f in onlyfiles:
        prefix, date = get_date(f)
        if abs(now - int(date)) < distance:
            all_matching.append((prefix, date))
    if len(all_matching) == 0:
        sys.exit(1)
    else:
        prefix, date = max(all_matching, key=lambda p: p[1])
        path = os.path.join(loc, prefix + "_" + date)
        print(path)
        sys.exit(0)

def get_date(s):
    pair = s.split("_")
    return (pair[0], pair[1])



if __name__ == '__main__':
    try:
        main()
    except Exception:
        if DEBUG:
            traceback.print_exc()
        sys.exit(1)
