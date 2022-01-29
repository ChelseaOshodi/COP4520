# COP4520

This program finds prime numbers up to 10^8.<br>
To run the program in enter the following on the command line:<br>
	$ g++ -std=c++11 -pthread Assignment1-OSHODI_4530011.cpp<br>
	$ ./a.out
  
### Approach
  I used a segmented sieve in order to compute the primes. I chose this algorithm because it's fast and efficiet.   
  A segmented sieve divides the range of 10^8 in different segments and compute primes in all segments one by one.
  
### Correctness and Efficiency
  I believe my program is correct because the sieve algorithim is well known and because my program provides consistent output on every run. It also computes all the primes in less than 3s, making it pretty efficient,  considering there are 5761455 primes found. 
  
### Experimental Evaluation
