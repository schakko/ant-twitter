ant-twitter
---
Features

    * Support for URL shortening service bit.ly via bitlyj
    * Support for messages more than 140 characters – the message will be splitted into two or more messages. A small source snippet is taken  from jtwitter – thanks a lot guys!
    * Open source – Apache license
    * It is simple :)

---
Installation

I assume you have already installed Ant-1.70 (1.6x should work although) and configured your Ant working environment. I refer to $ANT_LIB$ which is a directory where your Ant installation can found further JARs (should be appended to your classpath).

    * Download needed packages and extract them to $ANT_LIB$:
          o bitlyj-1.0-snapshot
          o twitter4j-2.0.10 – you have to extract all files in directory lib, too!
          o json-lib-2.3 – needed by bitlyj
          o and twitter-ant-1.0 (see link below)
    * Create a file twitter4j.properties in your classpath ($ANT_LIB$) and paste the following code into it:
      view source
      print?
      1.twitter4j.http.useSSL=true
      2.twitter4j.debug=false
---
Usage

After you have installed the needed dependencies and upset your configuration you have to edit your build.xml. Put the following taskdef in top of your build file:

<project name="TwitterTestTask" default="main" basedir=".">
  <taskdef name="twitter" classname="de.ecw.ant.twitter.AntTwitterTask"/>

You are now able to use the twitter task in form of

<twitter message="Hello World from Ant!" username="twitter-username" password="twitter-password" bitlyUsername="bitly-username" bitlyApiKey="bitly-key" enableBitly="false" />

ant-twitter has the following options:

    * message (required, String): Your Twitter tweet
    * username (required, String): Your Twitter screenname
    * password (required, String): Your Twitter password
    * bitlyUsername (optional, required if enableBitly=true, String): Your bit.ly username
    * bitlyApiKey (optional, required if enableBitly=true, String): Your bit.ly API key
    * enableBitly (optional, true|false): Enable bit.ly support


---
Please read the detailed instruction or leave comments on http://wap.ecw.de/archives/1232

Christopher Klein <christopher[dot]klein[at]ecw[dot]de> - http://wap.ecw.de