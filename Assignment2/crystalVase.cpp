#include "crystalVase.h"

using namespace std;
vase::vase(int nGuests) : guests(nGuests, 0), busy(false){}

// One tick selects a guest at random and tries to see the vase (acquire the lock)
void vase::ran(int nSeen)
{
    lock_guard<mutex> lg(tex);
    // The room is now busy
    busy = true;
    // This guest has now seen the vase +1 times
    vase::update(nSeen);
    cout << "Guest #" << nSeen << " is viewing the vase..." << endl;
    // Simulate the guest viewing the vase
    this_thread::sleep_for(chrono::seconds(2));
    cout << "Guest #" << nSeen << " is finished viewing the vase...\n" << endl;
    busy = false;
    return;
}

void vase::update(int x) {guests.at(x) += 1;}

int vase::callGuest(int x) {return guests.at(x);}

bool vase::use(){return busy;}