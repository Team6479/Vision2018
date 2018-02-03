
#wireless connection
if ping -w 1 10.64.79.5 &> /dev/null; then
	echo "10.64.79.5" > ipaddr
#wired connection
elif ping -w 1 10.64.79.6 &> /dev/null; then
	echo "10.64.79.6" > ipaddr
#no connection found
else
	echo "NO_IP_FOUND" > ipaddr
fi

