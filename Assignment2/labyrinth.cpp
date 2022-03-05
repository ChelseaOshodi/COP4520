#include "laby.h"

using namespace std;

laby::laby(int n) : guests(n, false), cupcake(true), count(0), done(false)
{
    default_random_engine gen;
    uniform_int_distribution<int> distribution(0, n - 1);
    counter = distribution(gen);
}

void laby::entered(int gid)
{
    lock_guard<mutex> lg(m);

    //Checks cupcake status, updates count, and checks guests' eaten status
    if (gid == counter && !cupcake) 
    {
        cupcake = true;
        count++;
        done = count == guests.size() - 1 ? true : false; 
        if (done) guests.at(counter) = true;
    }
    else if (cupcake && !guests.at(gid) && gid != counter)
    {
        cupcake = false;
        guests.at(gid) = true;  
    }
}

bool laby::status(){return done;}
int laby::gCount()
{
    int eaten = 0;
    for (auto x : guests)
        if (x) eaten++;
            
    return eaten;
}  

vector<bool> laby::callGuests(){return guests;}
