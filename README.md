GloBox
======

Distributed system for ticket reservation

This project was implemented by myself and two university colleagues on Information Security Master.

It is a distributed ticket reservation system, where servers all located on several places. Each theater belongs to a zone, so it makes sense that for each theater the user will access to local server. From here there is a lot of challenges: I can be in Lisbon and reserving tickets for OPorto but I should be accessing to the closest server (Lisbon). What happens if two persons are buying the last ticket, someone using the server in OPorto and the other one in Lisbon? What happend if the Lisbon guy buys the ticket first? Will it be valid in OPorto? 

We use ZooKeeper for group management (membership, leader election, etc.). Fault tolerance and distributed systems techniques as well. We implemented load balancers.
