#!/usr/bin/env python

"Kill all Java processes owned by the current user on the given cluster."

import myriadeploy

import subprocess
import sys

def get_hostname(node):
    return node[0]

def kill_java(host_entry, username):
    host = get_hostname(host_entry)
    cmd = ['ssh', '%s@%s' % (username, host), 'killall -KILL -v java']
    subprocess.call(cmd)

def stop_all(config):
    master = config['master']
    workers = config['workers']
    username = config['username']

    # Stop the Master
    kill_java(master, username)

    for worker in workers:
        kill_java(worker, username)


def main(argv):
    if len(argv) != 2:
        print >> sys.stderr, "Usage: %s <deployment.cfg>" % (argv[0])
        sys.exit(1)

    config = myriadeploy.read_config_file(argv[1])

    stop_all(config)

if __name__ == "__main__":
    main(sys.argv)
