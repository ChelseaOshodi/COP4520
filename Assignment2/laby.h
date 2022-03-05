#ifndef LABY_H
#define LABY_H
#include <mutex>
#include <thread>
#include <vector>
#include <random>

class laby 
{
     private:
        
        std::vector<bool> guests; //Vector that keeps track of guests and if they've eatenn a cupcake
        bool cupcake; //Boolean for cupcake availability

        int count; //cupcake refill counter
        int counter; //guest counter
        bool done; //bool for if all guest have eaten a cupcake
        std::mutex m; //mutex object

        public:
       
            laby(int n); //initializer
            void entered(int gid); //Guest entering labyritnth simulator
            bool status();
            int gCount();
            std::vector<bool> callGuests();
};
#endif