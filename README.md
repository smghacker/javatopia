Javatopia Garbage Collection Wars for http://talentboost.org/
==

Platform for coding competition, the students collect garbage from from a graph via REST services. They need to follow some rules, very similar to the java GC.
Visit our wiki page at https://github.com/kspirov/javatopia/wiki for full description.

The platform is fully self contained and easy deployable - just  pom clean install,  run with java -jar (spring boot is used, so the embedded tomcat will start).

In order to see instruction and competition rules, login to  :8080.

The service accepts anonymous authentication, only if you want to restart the server, use any user name and password "password". If yo want to set a different password, use  -Dpassword=myPassword

As spring boot is quite new technology, we had to use the snapshot repository. If the build attempt fails, and the build is performed long time after the last commit here, most probably you will need to correct the dependencies in the main pom.xml - so don't panic, just change the properties.

The platform has an extra feature only for Windows - visualizing the graph by calling the native Graphwiz library.  If you want this visualization
for Linux too, install .deb or .rpm GraphWiz manually by following the instruction on http://www.graphviz.org/, then hack in ImageFacade around line 33:

Process proc = Runtime.getRuntime().exec(Bootstrap.BINARY_ROOT_FOLDER+"/release/bin/dot.exe -Tpng");

Have fun. 