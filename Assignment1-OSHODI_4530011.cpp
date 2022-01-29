//Chelsea Oshodi (4530011)
//COP4520 SPRING2022
//Assignment 1 

#include <iostream>
#include <fstream>
#include <math.h>
#include <thread>
#include <vector>
using namespace std;
#define MAX 100000000

void normal(bool *p, int x) {

  for(int i = 2; i * i <= x; i++)
    if(p[i])
      for(int j = i * 2; j * j <= x; j += i)
        p[j] = false;
}

void segment(bool *p, int min, int max) {
  if(min == 1)
    min++;

  for(int i = 2; i < MAX && i * i <= max; i++) {
    int temp = i * i;

    if(temp < min)
      temp = ((min + i - 1) / i) * i;

    for(int j = temp; j <= max; j += i)
      p[j] = false;
  }
}

int main() {
  int min = 1;
  int max = min * 10;

  bool *p = new bool[MAX + 1];
  vector<thread> tList;
  ofstream f_out;

  // Set the initial values of the primes array
  memset(p, true, MAX + 1);
  p[0] = false;
  p[1] = false;

  f_out.open("primes.txt");

  long sum = 0;
  int num = 0;
  long *top = new long[10];

  int start = clock();

  for(int i = 0; i < 8 && max <= MAX; i++){
    tList.push_back(thread(segment, p, min, max));
    min = max + 1;
    max *= 10;
  }

  for_each(tList.begin(), tList.end(), mem_fn(&thread::join));

  int stop = clock();

  int current = 10 - 1;

  for(int i = MAX; i >= 0; i--)
    if(p[i]){
      sum += i;
      num++;

      if(current >= 0 && current < 10)
        top[current--] = i;
    }

  f_out << (double)(stop - start) / CLOCKS_PER_SEC << "s " << num << " " << sum << "\n";

  for(int i = 0; i < 10; i++) {
    f_out << top[i];

    if(i == 10 - 1)
      f_out << "\n";
    else
      f_out << ", ";
  }

  f_out.close();

  return 0;
}