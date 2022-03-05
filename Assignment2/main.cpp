//Chelsea Oshodi (4530011)
//COP4520 SPRING2022
//Assignment 2 

#include <fstream>
#include <queue>
#include <algorithm>
#include "laby.h"
#include "crystalVase.h"
using namespace std;


const int nTHREADS = thread::hardware_concurrency(); //Gets threads based on hardware
default_random_engine gen;

//Threadpooling
template<typename work>
void startT(vector<thread>& threads, work&& w)
{
    for(auto&& m: threads)     //Gets empty thread
    {
        if(m.joinable()) 
            continue;
        m = thread(w);
        return;
    }

    for(auto&& m: threads)     //Available thread queue
    {
        if(!m.joinable()) 
            continue;
        m.join();
        m = thread(w);
        return;
    }
}


void p1() //Problem 1
{
    vector<thread> pool(nTHREADS); //Threadpooling
    int nGuests = 0;

    //Gets number of guests
    do
    {
        cout << "Enter the number of guests, between 2 and 300, to enter the labyrinth" << endl;
        cin >> nGuests;
    } while (nGuests < 2 or nGuests > 300);

    laby z(nGuests); //Initializes the labyrinth
    uniform_int_distribution<int> distribution(0, nGuests - 1);

    auto start = chrono::high_resolution_clock::now();    //Records the time

    int gId = -1;

    while (!z.status()) //Starts the puzzle
    {
        gId = distribution(gen);
        startT(pool, [&]{z.entered(gId);});
    }

    for (auto&& m : pool) //Clears threads
        if (m.joinable())
            m.join();

    //Records time
    auto end = chrono::high_resolution_clock::now(); 
    auto length = chrono::duration_cast<chrono::milliseconds>(end - start);
    
    int eaten = z.gCount();

    //Saves results
    ofstream opf;
    opf.open("results-problem1.txt");
    opf << "RESULTS:\n\nNumber of guests who had a cupcake: " << eaten << "\nNumber of guests who have haven't had a cupcake: " << nGuests - eaten << endl;
    opf << "Execution Time(MS): " << length.count() << endl;
    opf << "-----------------------------------" << endl;
    opf << "GUEST#\tHAS HAD CAKE?" << endl;
    vector<bool> guests = z.callGuests();
    for (int i = 0; i < guests.size(); i++)
        opf << i << "\t" << guests[i] << endl;
    opf.close();
    cout << "Results saved to \"p1_resultsproblem1.txt\"" << endl;
}

// Code that executes the vase problem
void p2()
{
    //Gets number of ticks
    int y;
    int n;

    cout << "Enter number of ticks to run" << endl;
    cin >> y;

    //Gets number of guests
    do
    {
        cout << "Enter the number of guests, between 2 and 300." << endl;
        cin >> n;
    } while (n < 2 or n > 300);

    cout << "Starting with" << n << "guests and " << y << " ticks.\n" << endl;

    vase vs(n); //Initializes the vase
    vector<int> guests;
    queue<int> vq;

    //Randomizes guests
    for (int i = 0; i < n; i++) guests.push_back(i);
    shuffle(begin(guests), end(guests), gen);
    vq.push(guests.at(0));

    guests.erase(guests.begin()); //Removes first guest to avoid repetition 
    uniform_int_distribution<int> distribution(0, 2000);
    for (int i = 1; i <= y; i++)
    {
        cout << "TICK " << i << "\n--------------" << endl;
        int p = vq.front();
        vq.pop();
        thread r(&vase::ran, ref(vs), p);
        this_thread::sleep_for(chrono::milliseconds(200));
        
        while (vs.use() && !guests.empty()) //Queues guests
        {
            vq.push(guests.at(0));
            cout << "Guest #" << guests.at(0) << " queues to see the vase" << endl;
            guests.erase(guests.begin());
            this_thread::sleep_for(chrono::milliseconds(distribution(gen)));
        }
     
        r.join(); //thread finishes before continuing
        guests.push_back(p); //Guest added back to queue
        shuffle(begin(guests), end(guests), gen);
    }

    //Saves results
    ofstream opf;
    opf.open("p2_results.txt");
    opf << "Ending Guest Queue:" << endl;
    while(!vq.empty())
    {
        opf << vq.front() << " ";
        vq.pop();
    }
    opf << "\n\n";
    opf << "GUEST #\t| # OF TIMES SEEING VASE\n-------------------\n" << endl;
    for (int i = 0; i < n; i++)
        opf << i << "\t" << vs.callGuest(i) << endl;
    opf.close();
    cout << "Final states written to \"results-problem2.txt\"" << endl;
}

// Simple menu driven program
int main()
{
    int choice = 0;
    do 
    {
        cout << "For the Minotaur's Birthday Party Problem press 1. For the  Minotaur's Crystal Vase problem press 2. Press 0 to exit" << endl;
        cin >> choice;
        
        switch (choice)
        {
            case 0:
                break;
            case 1:
                p1();
                choice = 0;
                break;
            case 2:
                choice = 0;
                p2();
                break;
            default:
                cout << "Invalid input, please enter a valid choice." << endl;
                break;
        }
    } while (choice != 0);
    return 0;
}
