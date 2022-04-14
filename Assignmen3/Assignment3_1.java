import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.io.*;


class lockFree
{
    private Node head;

    lockFree()
    {
        this.head = new Node(Integer.MIN_VALUE);
        this.head.next = new AtomicMarkableReference<Node>(new Node(Integer.MAX_VALUE), false);
    }

    public Window find(Node head, int key)
    {
        Node pred = null, curr = null, succ = null;
        boolean[] mark = { false };
        boolean bool;
        retry: while (true) {
            pred = head;
            curr = pred.next.getReference();
            while (true) {
                succ = curr.next.get(mark);
                while (mark[0]) {
                    bool = pred.next.compareAndSet(curr, succ, false, false);
                    if (!bool)
                        continue retry;

                    curr = succ;
                    succ = curr.next.get(mark);
                }
                if (curr.key >= key)
                    return new Window(pred, curr);

                pred = curr;
                curr = succ;
            }
        }
    }

    public boolean add(int key)
    {
        while (true)
        {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if (curr.key == key) {
                return false;
            } else {
                Node node = new Node(key);
                node.next = new AtomicMarkableReference<Node>(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) {
                    return true;
                }
            }
        }
    }

    public boolean remove(int key)
    {
        boolean bool;
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if (curr.key != key) {
                return false;
            } else {
                Node succ = curr.next.getReference();
                bool = curr.next.compareAndSet(succ, succ, false, true);
                if (!bool)
                    continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public boolean contains(int key) 
    {
        boolean[] mark = { false };
        Node curr = head;
        while (curr.key < key)
        {
            curr = curr.next.getReference();
            Node succ = curr.next.get(mark);
        }
        return (curr.key == key && !mark[0]);
    }
}

class Node {
    int key;
    AtomicMarkableReference<Node> next;

    Node(int key)
    {
        this.key = key;
        this.next = new AtomicMarkableReference<Node>(null, false);
    }
}

class Window {
    public Node pred, curr;

    Window(Node myPred, Node myCurr) {
        pred = myPred;
        curr = myCurr;
    }
}

public class Assignment3_1 extends Thread
{
    public static ArrayList<servant> threads = new ArrayList<>();
    public AtomicInteger cards= new AtomicInteger();
    public lockFree chain = new lockFree();
    public Stack<Integer> gifts = new Stack<>();
    public ArrayList<Integer> giftList = new ArrayList<>();
    
    ReentrantLock lock = new ReentrantLock();
    int presents;
    int numOfThreads;

    Assignment3_1()
    {
        this.presents = 500_000;
        this.numOfThreads = 4;
    }

    void run(Assignment3_1 mainThread) throws InterruptedException
    {
        //Creating threads
        for (int i = 1; i <= this.numOfThreads; i++)
            threads.add(new servant(i, mainThread));

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).start();

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).join();

        System.out.println("All " + cards + " Thank you notes were written!");

    }

    public static void main(String args[]) throws InterruptedException
    {
        //Main thread instance
        Assignment3_1 mainThread = new Assignment3_1();

        for (int i = 1; i <= mainThread.presents; i++)
            mainThread.gifts.push(i);

        Collections.shuffle(mainThread.gifts);

        final long startTime = System.currentTimeMillis();

        mainThread.run(mainThread);

        final long endTime = System.currentTimeMillis();
        final long runTime = endTime - startTime;
        System.out.println("Execution time: " + runTime + " ms");
    }
}

class servant extends Thread
{
    int threadNum;
    Assignment3_1 mainThread;

    servant(int threadNum, Assignment3_1 mainThread)
    {
        this.threadNum = threadNum;
        this.mainThread = mainThread;
    }

    boolean emptyBag()
    {
        return this.mainThread.gifts.empty();
    }

    boolean emptyList()
    {
        return this.mainThread.giftList.size() == 0 ? true : false;
    }

    @Override
    public void run()
    {
        while (mainThread.cards.get() < mainThread.presents)
        {
            int task = (int) (Math.random() * 3 + 1);

            int gift;

            switch (task)
            {
                case 1:
                    //Remove gift from bag and add to list
                    mainThread.lock.lock();
                    try {
                        if (emptyBag())
                            break;
                        gift = mainThread.gifts.pop();
                        mainThread.giftList.add(gift);
                    } finally {
                        mainThread.lock.unlock();
                    }
                    mainThread.chain.add(gift);
                    break;
                case 2:
                    //Remove from list and send Thank you notes
                    mainThread.lock.lock();
                    try {
                        if (emptyList())
                            break;
                        int randIndex = (int)(Math.random() * mainThread.giftList.size());
                        gift = mainThread.giftList.get(randIndex);
                        mainThread.giftList.remove(randIndex);
                    } finally {
                        mainThread.lock.unlock();
                    }
                    mainThread.chain.remove(gift);
                    mainThread.cards.getAndIncrement();
                    break;
                case 3:
                    //Checks if gift is added to list
                    int random = (int)(Math.random() * mainThread.presents + 1);

                    mainThread.chain.contains(random);
            }
        }
    }
}