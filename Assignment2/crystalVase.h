#ifndef CRYSTALVASE_H
#define CRYSTALVASE_H

#include <vector>
#include <mutex>
#include <random>
#include <iostream>
#include <thread>
#include <chrono>

class vase 
{
    private:
        // A vector where each index represents the ith guest and guests[i] represents the number of time that guest has seen the vase
        std::vector<int> guests;
        // Whether or not someone is in the vase room
        bool busy;
        // A mutex object
        std::mutex tex;

    public:
        vase(int nGuests);
        // One tick simulates what happens when a guest is in the vase room
        void ran(int nSeen);
        // add 1 to the number of times the ith guest has seen the vase
        void update(int x);
        int callGuest(int x);
        bool use();
};
#endif
