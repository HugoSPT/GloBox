GloBox
======

Distributed system for ticket reservation

This project was implemented by myself and two university colleagues on Information Security Master.

It is a distributed ticket reservation system, where servers all located on several places. Each theater belongs to a zone, so it makes sense that for each theater the user will access to local server. From here there is a lot of challenges: I can be in Lisbon and reserving tickets for OPorto. So, I need to use OPorto servers.

We use ZooKeeper. Fault tolerance techcniques and distributed systems techniques as well.
