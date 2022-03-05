# Problem 1: Minotaur’s Birthday Party
## My solution: <br>
Each guest will only eat a cupcake once. If a guest has yet to eat a cupcake and there is one present when they enter the labyrinth they will eat the cupcake. Otherwise the guest will neither eat the cupcake or request a refill. <br>
One guest will be in charge of informing the minotaur that everyone has had a cupcake. This guest will also be the only guest who will request a cupcake refill if no cupcake is presnt. Once this guest has refilled the cupcake n-Guest minus 1 times, they will inform the Minotaur that everyone has had a cupcake. 
 
 
## Proof of Correctness
This problem was simulated but using an array of booleans in which the elements represent whether the n-th guest has had a cup cake. The simulation is ran in a loop until each guest has had a cupcake. On each iteration a single guest is randomly chosen and an available thread is called from a thread pool that calls the a function, protected by a mutex, that simulates a guest entering the labyrinth.
In the long run this solution is guaranteed to work, though it gets much slower as N increases. For N = 100 it runs in about half a second on my machine, for N = 200 it runs in about 2.5 seconds and the performance continues to grow worse. I have included a graph of runtimes in the submission folder.

## Efficiency and Experimental evaluation
The solution runs well and simulates the problem correctly. But it runs slower as the number of guests increases.  <br>
For my computer, 150 guests had an execution time of 291 ms. And 300 guests had an execution time of 1064 ms, a 27% increase from 150 guests. <br>


# Problem 2: Minotaur’s Crystal Vase (50 points)
