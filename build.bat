cd rsc-client
mkdir bin
javac -sourcepath src -cp libs/joml-1.9.19.jar;libs/xpp3.jar;libs/xstream.jar -d bin src/client/main/RuneClientLauncher.java
robocopy res bin/res /mir
