Javatopia Garbage Collection Wars
==

Platform for coding competition, the students collect garbage from from a graph via REST services. They need to follow some rules, very similar to the java GC.

The platform is fully self contained and easy deployable - just  pom clean install,  run with java -jar (spring boot is used, so the embedded tomcat will start).

In order to see instruction and competition rules, login to  :8080.

The service accepts anonymous authentication, only if you want to restart the server, use any user name and password "password". If yo want to set a different password, use  -Dpassword=myPassword

The platform has an extra feature only for Windows - visualizing the graph by calling the native Graphwiz library.  If you want this visualization
for Linux too, install .deb or .rpm GraphWiz manually by following the instruction on http://www.graphviz.org/, then hack in ImageFacade around line 33:

Process proc = Runtime.getRuntime().exec(Bootstrap.BINARY_ROOT_FOLDER+"/release/bin/dot.exe -Tpng");

Have fun. 