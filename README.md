# mesosphere-test


Main class is Main.scala

It seems, I had not enough of time to implement 
more sophisticated scheduling solution then just to distribute PickUp's uniformly at random. 
Which btw is not the worst solution in such problems.

Solution was build using Akka actors. A Typed actor was used to make a bridge
between synchronous ElevatorControlSystem interface and asynchronous Elevator's, 
which were implemented using untyped actors. 
The 'status' method of ElevatorControlSystem was modified to return a Future[A] 
instead of an A for that purpose. 

Many things left to be done due to the lack of time(tests, scheduling, etc.).