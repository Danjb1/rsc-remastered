cd rsc-client
mkdir bin
javac -sourcepath src -cp libs/joml-1.9.19.jar;libs/netty-3.10.6.Final.jar;libs/xpp3.jar;libs/xstream.jar -d bin src/client/main/RuneClientLauncher.java
robocopy res bin/res /mir
