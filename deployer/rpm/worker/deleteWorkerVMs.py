#!/bin/python
import os
import sys

VIRTUALIZATION_TYPE = sys.argv[1]

arq = open("/etc/ourgrid/vmnames.conf","r")
line = arq.readline()
while line != "":
	os.system("/usr/bin/destroyVM %s ourgrid %s" %(VIRTUALIZATION_TYPE, line))
	line = arq.readline()
