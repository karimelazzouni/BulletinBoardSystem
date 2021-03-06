# BulletinBoardSystem
Assignment for the Distributed Systems (CS 432) course taught in the 10th semester in the Computer and Systems Engineering 
Department, Faculty of Engineering, Alexandria University. 
In this assignment we will be simulating a news bulletin board system. 
In this system there are several processes accessing the system, either getting the news from the system or updating the news. 
The system will consist of a server that implements the actual news bulletin board and several remote clients that communicate 
with the server using the RPC/RMI; So, we will be developing a distributed client/server version of the solution using a 
service-oriented request/reply scheme. The remote clients will have to communicate with the server to write their news to the 
bulletin board and also to read the news from the bulletin board. 
We will be using RMI (Remote Method Invocation) for all necessary communication instead of the Sockets (already introduced in 
previous courses). Also, while RMI will take care of the client threads (we don’t need to handle it like sockets), we still 
need to provide the necessary synchronization. 
