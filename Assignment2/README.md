# Problem 1: Minotaur’s Birthday Party
## My solution: <br>
Each guest will only eat a cupcake once. If a guest has yet to eat a cupcake and there is one present when they enter the labyrinth, they will eat the cupcake. Otherwise, the guest will neither eat the cupcake nor request a refill. <br>
One guest will be in charge of informing the minotaur that everyone has had a cupcake. This guest will also be the only guest who will request a cupcake refill if no cupcake is present. Once this guest has refilled the cupcake n-Guest minus 1 times, they will inform the Minotaur that everyone has had a cupcake. 
 
 
## Proof of Correctness
This problem was simulated by using an array of Booleans in which the elements represent whether the n-th guest has had a cup cake. The simulation is ran in a loop until each guest has had a cupcake. On each iteration a single guest is randomly chosen, and an available thread is called from a thread pool that calls the a function, protected by a mutex, that simulates a guest entering the labyrinth.
In the long run this solution is guaranteed to work, though it gets much slower as N increases. For N = 100 it runs in about half a second on my machine, for N = 200 it runs in about 2.5 seconds and the performance continues to grow worse. I have included a graph of runtimes in the submission folder.

## Efficiency and Experimental evaluation
The solution runs well and simulates the problem correctly. But it runs slower as the number of guests increases.  <br>
For my computer, 150 guests had an execution time of 291 ms. And 300 guests had an execution time of 1064 ms, a 27% increase from 150 guests. <br>


# Problem 2: Minotaur’s Crystal Vase (50 points)
## My solution: <br>
I chose the third solution to simulate this problem. This involves creating a guest queue, with every guest exiting the room being responsible to notify the guest standing in front of the queue that the showroom is available, and guests being allowed to queue multiple times.

## Proof of Correctness
This problem was simulated by using thread suspension and ticks. The user chooses the number of ticks and for each tick a guest is chosen at random to enter view the vase on a thread separate from the main thread, that thread sleeps for 2 seconds to simulate the guest viewing the vase, and simultaneously, on the main thread guests are randomly placed in a queue. The main thread also sleeps for a random amount of time to invoke variation. When the tick ends, the guest that was viewing the vase is returned to randomized guest queue. And this process is repeated for the number of ticks chosen by the user. 

## Efficiency and Experimental evaluation
The third solution is superior to all other provided solutions It is superior to solution one because it avoids the possible overcrowding and inactivity of guests which would cause performance issues as threads would be idle for long periods of time, waiting for access to the vase which may never happen, making solution one a TAS lock. <br>
The third solution is also superior to solution two because solution two is essentially a backoff lock and its efficiency would greatly decline with higher guest counts because there would be too many threads.   <br>
With solution three essentially being a queue-based lock, it is the most efficient out of the three provided solutions.


### [Updated after deadline in order to fix several typos.]
