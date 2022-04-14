import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Assignment3_2 {
	public static void main(String[] args) throws InterruptedException {
		
		int i;
		int sensors = 8;
		int duration = 24; 
		long start, end, eTime;	 
		long bMem, aMem,actMem;
		
		start = System.currentTimeMillis();
		bMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		
		AtomicBoolean done = new AtomicBoolean(false);
		tModule record = new tModule(done);	
		Thread[] thread = new Thread[sensors];
		
		for(i = 0; i < sensors; i++) {
			thread[i] = new Thread(new sensor(record, done, duration), "Sensor " + i);
		}
			
		for(i = 0; i < sensors; i++) {
			thread[i].start();
		}
			
		for(i = 0; i < sensors; i++) {
			thread[i].join();
		}
		
		aMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		end = System.currentTimeMillis();
		
		eTime = end - start;
		actMem = aMem - bMem;
		
		System.out.println("\n   -------------------------------------------------------");
		System.out.println("\t Exetution time: " + (int)(eTime)/100 + "ms");
		System.out.println("\t Memory usage: " + actMem);
		
		
	}
}

class sensor implements Runnable {
		
	AtomicBoolean done;
	private AtomicInteger duration;
	private tModule record;
		
	public sensor(tModule record, AtomicBoolean done, int duration) {
		this.record = record;
		this.duration = new AtomicInteger(duration);
		this.done = done;
	}
		
	public void run() {
		while(!done.get()) {						
			try {
				record.operation(record, duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class readTemp {
		
	int temp;
	int start;
	int end;
	readTemp next;
	
	public readTemp(int temp, int start, int end) {
		this.temp = temp;
		this.start = start;
		this.end = end;
		this.next = null;
	}
}


class tModule {
		
	int num = 0;
	readTemp head;
	Random rand = new Random(); 
	Lock lock = new ReentrantLock();
	AtomicInteger temp = new AtomicInteger();
	AtomicInteger start = new AtomicInteger(-1);
	AtomicInteger end = new AtomicInteger(0);
	public AtomicInteger hour = new AtomicInteger(0);
	public AtomicInteger counter = new AtomicInteger(0); /*being used to detect time*/
	public AtomicBoolean done = new AtomicBoolean(false);
		
	public tModule (AtomicBoolean done) {
		head = new readTemp(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		head.next = new readTemp(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		this.done = done;
	}
		
	public void add(int temp, int start, int end) {
		lock.lock();
		
		try {
			if(isEmpty()) {
				readTemp newNode = new readTemp(temp, start, end);
				head = newNode;
				head.next = null;
				counter.incrementAndGet();
			}
			else {
				readTemp pred = head;
								
				while(pred.next != null && pred.next.start < start) {
					pred = pred.next;	
				}
					
				readTemp newNode = new readTemp(temp, start, end);
				newNode.next = pred.next;
				pred.next = newNode;				
				counter.incrementAndGet();
			}
		}finally {
			lock.unlock();
		}
	}
	
	public boolean isEmpty() {
		lock.lock();
			
		try {
			return (head == null) ? true : false; 
				
		} finally {
			lock.unlock();
		}
	}
			
    //Prints values
	public void display(int s_interval, int e_interval) {	
		lock.lock();
		
		try {				
			if(isEmpty()) {	
				System.out.printf("No records for following interval.\n");
			}				
			else {
				readTemp pred = head;
					
				while(pred.next != null && pred.next.start >= s_interval && pred.next.start <= e_interval) {

					if(pred.start != Integer.MIN_VALUE && pred.start != Integer.MAX_VALUE) {
						 if(hour.get() == 0)
							System.out.printf(" \tIntervals: %d - %d    Temp: %dF\n", pred.start, pred.end, pred.temp);
					     else
					       	System.out.printf(" \tIntervals: %d - %d    Temp: %dF\n", pred.start%60, pred.end%60, pred.temp);
					}
					pred = pred.next;	
				}	
			}
		} finally {
			lock.unlock();
		}
	}
	
	//Gets lowest tempurature
	public void lowest() {		
		lock.lock();
		
		try {				
			if(!isEmpty()) {
				
				ArrayList<Integer> lowest = new ArrayList<Integer>();
				
				readTemp pred = head;
				
				while(pred.next != null) {

					if(pred.temp != Integer.MIN_VALUE && pred.temp != Integer.MAX_VALUE) 
						lowest.add(pred.temp);
						
					pred = pred.next;	
				}	
				
				Collections.sort(lowest);
				
				int i = 0;
				for(int temp: lowest){
					if(i < 5)
						System.out.printf(" \t Lowest Temperatures: %dF\n", temp);
					i++;
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	//Gets Highest Temperature
	public void highest() {	
		lock.lock();
		try {				
			if(!isEmpty()) {
				
				ArrayList<Integer> highest = new ArrayList<Integer>();
				readTemp pred = head;
				
				while(pred.next != null) {

					if(pred.temp != Integer.MIN_VALUE && pred.temp != Integer.MAX_VALUE) 
						highest.add(pred.temp);
						
					pred = pred.next;	
				}	
				
				Collections.sort(highest);
				
				int i = 0;
				for(int temp: highest){
					if(i >= (highest.size() - 5) && i < highest.size())
						System.out.printf(" \t Highest Temperatures: %dF\n", temp);
					i++;
				}
			}
		} finally {
			lock.unlock();
		}	
	}
	
    //Gets temperature difference
	public void difference() {
		lock.lock();

		try {				
			if(!isEmpty()) {
				int i = 0, j = 0;
				int start = 0, end = 0, diff;
				readTemp pred = head;
				readTemp curr = head.next;
				int[][] differences = new int[50][3];
				
				while(curr != null && j < 50) {
					if(pred.temp != Integer.MIN_VALUE && curr.temp != Integer.MAX_VALUE) {
						start = pred.temp;	
						while(curr.next != null && i < 9) {
							curr= curr.next;
							i++;
						}
						
						end = curr.temp;
						diff = start - end;
						differences[j][0] = diff; 
						differences[j][1] = pred.start; 
						differences[j][2] =	curr.start;	
						j++;		
					}	
					pred = pred.next;
					curr = curr.next; 
				}
				
				i = 0;
				int max = differences[0][i];
				
		        for (j = 1; j < 50; j++)
		        {
		        	if (differences[j][0] > max) 
		        	{
		        		max = differences[j][0];
		        		start =differences[j][1];
		        		end = differences[j][2];
		        	}
		        }
		        if(hour.get() == 0)
		        	System.out.printf(" \t Start Time: %d End Time: %d Difference: %dF\n", start, end, max);
		        else
		        	System.out.printf(" \t Start Time: %d End Time: %d Difference: %dF\n", start%60, end%60, max);
				
			}
		} finally {
			lock.unlock();
		}	
	}

	
	public void operation(tModule record, AtomicInteger duration) throws InterruptedException {
		lock.lock();
		
		try {
			temp.set(rand.nextInt(170) - 100);
			start.getAndIncrement();
			end.getAndIncrement();
			record.add(temp.get(), start.get(), end.get());
			
			if(counter.get() % 60 == 0) {

				System.out.println("                Atmospheric Temperature Module       ");
				System.out.println("   ======================================================");
				System.out.println("   -------------------------------------------------------");
				System.out.printf("    Time:  %d hour\n", hour.get());
				System.out.println("   -------------------------------------------------------");
				System.out.printf("    Records:  \n");
				
				if(hour.get() == 0)
					record.display(0, 60);
				else
					record.display(60*hour.get(), 120*hour.get());
				
				System.out.println("   -------------------------------------------------------");
				System.out.println("   Lowest Temperature Record: ");
				record.lowest();
				System.out.println("   -------------------------------------------------------");
				System.out.println("   Highest Temperature Record: ");
				record.highest();
				System.out.println("   -------------------------------------------------------");
				System.out.println("   Largest temperature difference:");
				record.difference();
				head = null;
				hour.getAndIncrement();
			}
								
			if(hour.get() == duration.get()) 
                done.set(true);
		}
		finally {
			lock.unlock();
		}
	}
}