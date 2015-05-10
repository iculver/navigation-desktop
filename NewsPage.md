## 16-DEC-2013 ##
### Console User-Exit ###
See [this link](http://donpedrodalfaroubeira.blogspot.com/2013/12/introducing-console-user-exits.html).
## 24-OCT-2013 ##
### Headless Console ###
There is now a "headless console" for the NMEA Data read from the station.
It can read several channels, and re-broadcast the data on others.
Typically, it reads the Serial port, and re-broadcasts on TCP, or HTTP (for the html5 console)...

To start the headless console:
<br />
Get some help first
<br />
**On Windows**
```
set JAVA_OPTIONS=%JAVA_OPTIONS% -Dverbose=false
set JAVA_OPTIONS=%JAVA_OPTIONS% -Dheadless=yes
java %JAVA_OPTIONS% -classpath %CP% olivsoftdesktop.OlivSoftDesktop -h
```
**On Linux & MacOS** (bash shell)
```
export JAVA_OPTIONS=${JAVA_OPTIONS} -Dverbose=false
export JAVA_OPTIONS=${JAVA_OPTIONS} -Dheadless=yes
java ${JAVA_OPTIONS} -classpath ${CP} olivsoftdesktop.OlivSoftDesktop -h
```
Here is the output:
```
Available parameters (system variables) are:
  -Dheadless=yes|no
  -Dverbose=true|false
If headless=yes :
To replay logged data, use :
  -Dlogged.nmea.data=path/to/logged-data-file
To read the serial port (Baud Rate 4800), use :
  -Dserial.port=COM15
To read the TCP, UDP or RMI port, use :
  -Dnet.port=7001
  -Dhostname=raspberry.boat.net
  -Dnet.transport=TCP|UDP|RMI
-------------------------------------------------------------------
 Important ++ : Calibration data are stored in nmea-prms.properties
-------------------------------------------------------------------
For the re-broadcast, use the -output parameter:
  -output=HTTP:9999              Data will be re-broadcasted using XML over HTTP, on this port
  -output=TCP:7001               Data will be re-broadcasted as they are, on this port
  -output=UDP:230.0.0.1:8001     Data will be re-broadcasted as they are, on this port and address
  -output=FILE:path/to/logfile   Data will be logged as they are, in this file
```
This also allows you to log the data, into a file, so they can be replayed later on.
The memory consumption is ridiculous, the goal will be to run this headless console on a Raspberry PI.

To read the serial port and then rebroadcast on TCP and HTTP, while logging the data, type
<br />
**On Windows**
```
set JAVA_OPTIONS=%JAVA_OPTIONS% -Dverbose=false
set JAVA_OPTIONS=%JAVA_OPTIONS% -Dheadless=yes
set JAVA_OPTIONS=%JAVA_OPTIONS% -Dserial.port=COM15
set CONSOLE_OUTPUT=-output=HTTP:9999 -output=TCP:7001 -output=FILE:.\logged-data\headless.nmea
java %JAVA_OPTIONS% -classpath %CP% olivsoftdesktop.OlivSoftDesktop %CONSOLE_OUTPUT%
```
**On Linux & MacOS**
```
set JAVA_OPTIONS=${JAVA_OPTIONS} -Dverbose=false
set JAVA_OPTIONS=${JAVA_OPTIONS} -Dheadless=yes
set JAVA_OPTIONS=${JAVA_OPTIONS} -Dserial.port=COM15
set CONSOLE_OUTPUT=-output=HTTP:9999 -output=TCP:7001 -output=FILE:./logged-data/headless.nmea
java ${JAVA_OPTIONS} -classpath ${CP} olivsoftdesktop.OlivSoftDesktop ${CONSOLE_OUTPUT}
```
This way, you can visualize your data in the Swing Console (the regular graphical one), see the data live in a browser on a tablet (connected on the boat's ad-hoc network) and have the boat position displayed in OpenCPN simultaneously (if you've set the input connection to TCP, port 7001 in this case).
<br />

#### It runs on a Raspberry PI ####
The headless console has actually been designed to run on a Raspberry PI. The Raspberry PI can read the NMEA Port (usually a Serial port), and the rebroadcast the data on HTTP, UDP, or TCP. This way, several devices _and_ programs can access the NMEA data simultaneously. And the Raspberry PI only draws 700 mA.
<br />
It has been successfully tested. See [this post](http://donpedrodalfaroubeira.blogspot.com/2013/11/multiple-access-for-real.html), and the earlier ones.

#### Headless Update ####
For the Raspberry PI too, there is an option to check for software updates, without GUI.
```
java -Dheadless=yes olivsoftdesktop.OlivSoftDesktop --check-update
```
Make sure the classpath is set correctly.
Make sure the proxy settings are OK (if needed)...