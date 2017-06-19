<h1>Chat Group Android Client</h1>

<h3>This project is a client of a chat group for Android OS.</h3>

It aims to connect to a server that manages the group connections and the messages received. After connection is successful, the user can send message to the group. A simple UI is used for testing functionalities.<br>

IDE: Android Studio 2.3.3<br>
Language: Java 7

* Thread safe
* Socket connection
* Ready for another connection after failures
* Handles connection failure
   * when there is no internet connection
   * when loses internet connection while connected
   * when server is off before connection
   * when server stops running while connected

For compile details see app/build.grade file.

<h2>Class Diagram</h2>

![alt tag](https://github.com/claudiomarpda/ChatGroupAndroidClient/blob/master/uml/class_diagram.png)
