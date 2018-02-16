
# navigate to directory
cd /home/nvidia/VisionCode2018
# check if jar is already running
if ! ps aux | grep java | grep -v -q grep ; then
	# run jar
	java -Djava.library.path=`pwd`/ -jar Vision2018-all.jar &
fi