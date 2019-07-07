# BrainWavePassword
Mobile app to use EEG brain wave signals for user authentication. Implemented using machine learning algorithms implemented on Cloud, Fog and Native servers. 

In Android Open the “Group10_Brainer”, folder in android studio. “Build the Project”. For Cloud server the ip address in the file “Constants.java” doesn’t change. But in Fog server, the ip address changes based on the network. We should substitute the public ip of the fog server in the value of “public final static String FOG_URL = http://192.168.43.43:3000/server/ “ 
Put the folder server code in the folder “/home/server/” in the fog and cloud server. (i.e create a new folder called server and put all the contents of server code folder into that). 
Requirements:
1.	Cloud server must have python3, with pandas, scipy, numpy and sklearn libraries installed.
2.	Cloud server must have nodejs and nodejs-express installed.
Run the nodejs script called “connapp.js” with the command “nodejs connapp,js” on port 3000. Now the port is opened and listening.  
Run the app and test it.
File Structure
1.	The folder “Group10_brainer” contains the code for the android app.
2.	The folder “Server code” contains the train data and test data of the 4 subjects and edf files and all the machine learning algorithms python executable files.
3.	There is test data file for each subject which is exclusive of train data.
