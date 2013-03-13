#!/usr/bin/env python

import socket
import subprocess
import sys
import time

def read_workers(filename):
    ret = []
    for line in open(filename,'r'):
        line = line.strip()
        # Skip blank lines or comments
        if len(line) == 0 or line[0] == '#':
            continue

        # Extract the host:port string
        hostline = line.split(':')
        if len(hostline) != 2:
            raise Exception("expected host:port instead of %s" % (line))
        hostname = hostline[0]
        try:
            socket.gethostbyname(hostname)
        except socket.error:
            raise Exception("unable to resolve hostname %s" % (hostname))
        try:
            port = int(hostline[1])
        except:
            raise Exception("unable to convert %s to an int" % (port))
        ret.append((hostname, port))
    return ret

def start_master(description, root, workers):
    for (hostname, port) in workers:
    	cmd = "cd %s/%s-files; nohup java -cp myriad-0.1.jar:conf -Djava.library.path=sqlite4java-282 edu.washington.escience.myriad.daemon.MasterDaemon %s 0</dev/null 1>master_stdout 2>master_stderr &" % (root, description, description)
    	args = ["ssh", hostname, cmd];
   	#print args
    	if subprocess.call(args):
        	print >> sys.stderr, "error starting master %s" % (hostname)
	time.sleep(0.5);
    	break;

def main(argv):
    # Usage
    if len(argv) != 4:
        print >> sys.stderr, "Usage: %s <description> <expt_root> <workers.txt>" % (argv[0])
        print >> sys.stderr, "       description: any alphanumeric plus '-_' string."
        print >> sys.stderr, "       expt_root: where the files should be stored locally, e.g., /scratch."
        print >> sys.stderr, "       workers.txt: a list of host:port strings;"
        print >> sys.stderr, "                    the first entry is the master."
        sys.exit(1)

    # Command-line arguments
    DESCRIPTION = argv[1]
    EXPT_ROOT = argv[2]
    WORKERS_FILE = argv[3]

    # Figure out the master and workers
    workers = read_workers(WORKERS_FILE)

    start_master(DESCRIPTION, EXPT_ROOT, workers)

if __name__ == "__main__":
    main(sys.argv)
