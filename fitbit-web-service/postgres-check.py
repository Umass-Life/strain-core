import socket
import sys

if (len(sys.argv) <= 1):
    print("ERR no arguments")
    sys.exit(1)

print sys.argv
hostname = sys.argv[0]
port_string = sys.argv[1]
print port_string
port = int(port_string)

print hostname, port

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    s.connect((hostname,port))
    s.close()
except socket.error, ex:
    print("Connection failed with errno {0}: {1}".format(ex.errno, ex.strerror))
    sys.exit(1);