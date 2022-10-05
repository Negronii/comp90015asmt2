# comp90015asmt2

### Easy way (IntelliJ):

1. Open project in IntelliJ 
2. Use maven to build the project 
3. Go to Server.java, modify run configuration, insert <port number> at arguments field 
4. Go to CreateWhiteBoard.java, modify run configuration,  insert <username> <ip> <port> at argument field 
5. Go to JoinWhiteBoard.java, modify run configuration,  insert <username> <ip> <port> at argument field

### Lauch using Command:

1. Go to https://gluonhq.com/products/javafx/, slide downwards to Downloads, download the javafx library, we used https://download2.gluonhq.com/openjfx/19/openjfx-19_osx-x64_bin-sdk.zip for MacBook x64
2. Unzip the downloaded file, copy the absolute path of lib. It will be something like /Users/r/Downloads/javafx-sdk-19/lib 
3. cd <directory path contains COMP90015ASMT2-1.0-SNAPSHOT.jar>
4. Run the server with command below ():

    ``java --module-path <lib path from step 2> --add-modules javafx.controls,javafx.fxml,javafx.swing -cp COMP90015ASMT2-1.0-SNAPSHOT.jar com.ruiming.comp90015asmt2.Server <port number>``

    E.g. ``java --module-path /Users/r/Downloads/javafx-sdk-19/lib --add-modules javafx.controls,javafx.fxml,javafx.swing -cp COMP90015ASMT2-1.0-SNAPSHOT.jar com.ruiming.comp90015asmt2.Server 3201``

5. Run the CreateWhiteBoard with command below:

    ``java --module-path <lib path from step 2> --add-modules javafx.controls,javafx.fxml,javafx.swing -cp COMP90015ASMT2-1.0-SNAPSHOT.jar com.ruiming.comp90015asmt2.CreateWhiteBoard <username> <server ip address> <server port number>``

    E.g. ``java --module-path /Users/r/Downloads/javafx-sdk-19/lib --add-modules javafx.controls,javafx.fxml,javafx.swing -cp COMP90015ASMT2-1.0-SNAPSHOT.jar com.ruiming.comp90015asmt2.CreateWhiteBoard manager localhost 3201``

6. Run the JoinWhiteBoard with command below:

    ``java --module-path <lib path from step 2> --add-modules javafx.controls,javafx.fxml,javafx.swing -cp COMP90015ASMT2-1.0-SNAPSHOT.jar com.ruiming.comp90015asmt2.JoinWhiteBoard <username> <server ip address> <server port number>``

   E.g. ``java --module-path /Users/r/Downloads/javafx-sdk-19/lib --add-modules javafx.controls,javafx.fxml,javafx.swing -cp COMP90015ASMT2-1.0-SNAPSHOT.jar com.ruiming.comp90015asmt2.JoinWhiteBoard client localhost 3201``
