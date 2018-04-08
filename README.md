# CMPE-275-ClimateWatch

Tips:
1) To regenerate .proto files, make a small edit and save, eclipse IDE will regenerate .java files from .proto files.
2) protoc-dependencies error will show as we don`t have env for go , C#, etc, but it will affect our compile, build, intellisense.
3) If you are using Eclipse, you need to install an additional Eclipse plugin because m2e does not evaluate the extension specified in a pom.xml. Download os-maven-plugin-1.5.0.Final.jar from http://repo1.maven.org/maven2/kr/motd/maven/os-maven-plugin/1.5.0.Final/os-maven-plugin-1.5.0.Final.jar and put it into the <ECLIPSE_HOME>/plugins directory.

4)finally to run any file , having an entry point , ie "main" , right click on file in eclipse, run As -> java application /jUnit test.

To run Server code : mvn exec:java -Dexec.mainClass=com.service.grpc.App    
To run Client code : mvn exec:java -Dexec.mainClass=com.service.grpc.Client  
NOTE: you don`t need mvn package exec, it just cleans and recompiles everything. mvn exec should do the trick




  
Navigate to ClimateWatch folder.  
  
To run Server code : mvn package exec:java -Dexec.mainClass=com.service.grpc.App  
  
To run Client code : mvn package exec:java -Dexec.mainClass=com.service.grpc.Client  
  
  
*** For testing purposes only ***  
Run the following commands(test dataset) in Mongo DB to test the querying functionality
use messagesDB
db.createCollection("messages")
db.messages.insert([  
{ "_id" : "25f9865d-8a4e-4205-8e99-7b9dbb95d2b4", "time" : new Date("2011-01-01T16:00:00Z"), "data" : "sample data" },  
{ "_id" : "b54523f1-44b6-42d7-ac18-6e3d6863bc93", "time" : new Date("2011-01-02T12:00:00Z"), "data" : "sample data" },  
{ "_id" : "4026f4d5-1d2c-40e1-a32f-e04c54cb69a5", "time" : new Date("2012-05-03T16:00:00Z"), "data" : "sample data" },  
{ "_id" : "ffe1f3e0-d6b8-4121-944e-0852f86745b3", "time" : new Date("2011-07-05T16:00:00Z"), "data" : "sample data" },  
{ "_id" : "5fef460a-2c4f-40f3-888c-6e5b7d01eb27", "time" : new Date("2012-11-04T16:00:00Z"), "data" : "sample data" },  
{ "_id" : "6e13367e-5141-41f1-8db4-674ba0a2dce8", "time" : new Date("2011-03-22T16:00:00Z"), "data" : "sample data" },  
{ "_id" : "7f513858-14ac-40d3-b65a-d2efc84513fa", "time" : new Date("2012-01-13T16:00:00Z"), "data" : "sample data" },  
{ "_id" : "b796d29a-9b45-4bf5-abc0-dde42c1be56a", "time" : new Date("2013-03-07T16:00:00Z"), "data" : "sample data" },  
{ "_id" : "83ef461f-5ff2-46b3-91e5-8ce8f25eb605", "time" : new Date("2014-09-19T16:00:00Z"), "data" : "sample data" },  
{ "_id" : "5b298454-cdbb-47eb-9a3d-9af8bd18bad9", "time" : new Date("2015-10-05T16:00:00Z"), "data" : "sample data" }  
])  
  
  
db.messages.find( { "time": { $gte: new Date("2011-01-01T00:00:00Z"), $lte: new Date("2013-01-01T00:00:00Z") } } );




