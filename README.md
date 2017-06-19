<h1>Chat Group Android Client</h1>

<h3>This project is a client of a chat group for Android OS.</h3>

It aims to connect to a server that manages the group connections and the messages received. After connection is successful, the user can send message to the group. A simple UI is used for testing functionalities.<br>

* Socket connection
* Ready for another connection after failures
* Handles connection failure<br>
    when there is no internet connection<br>
    when loses internet connection while connected<br>
    when server is off before connection<br>
    when server stops running while connected<br>

For compile details see app/build.grade file.

<h2>Class Diagram</h2>

![alt tag](https://github.com/claudiomarpda/ChatGroupAndroidClient/blob/master/uml/class_diagram.png)
