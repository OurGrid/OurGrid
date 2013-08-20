#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <algorithm>
#include <string>
#include <sstream>
#include <iterator>
#include <vector>
#include <fstream>
#include <iostream>
#include <pthread.h>
#include <signal.h>
#include <time.h>

using namespace std;

vector<string> split(string input) {
  istringstream iss(input);
  vector<string> tokens;
  copy(istream_iterator<string>(iss), istream_iterator<string>(),
      back_inserter<vector<string> >(tokens));
  return tokens;
}

void* get_char_thread(void* argv) {
  char *fullDevFile = (char *)argv;
  FILE * device;
  device = fopen(fullDevFile, "r");
  int cc = fgetc(device);
  fclose(device);
  exit(0);
}

void signal_alarm(int sig) {
  exit(1);
}

int main (int argc, char *argv[]) {

  if (argc < 2) {
    printf("Requires sleep time\n" );
    exit(2);
  }
 
  FILE *fp;
  fp = popen("cat /proc/bus/input/devices", "r");
  if (fp == NULL) {
    printf("Failed to run command\n" );
    exit(2);
  }

  char lineA[1035];
  bool isInput = false;
  
  vector<pthread_t*> threads;

  signal(SIGALRM, signal_alarm);
  alarm(atoi(argv[1]));

  /* Read the output a line at a time - output it. */
  while (fgets(lineA, sizeof(lineA) - 1, fp) != NULL) {
    string line (lineA);
    transform(line.begin(), line.end(), line.begin(), ::tolower);

    if (strcmp(lineA, "\n") == 0) {
      isInput = false;
      continue;
    }

    if (line.find("n: ") == 0 && 
         (line.find("mouse") != string::npos || line.find("keyboard") != string::npos)) {
      isInput = true;
      continue;
    }

    if (line.find("h: ") == 0 && isInput) {
      string handlersS = line.substr(line.find("=") + 1);
      vector<string> handlers = split(handlersS);

      for (int i = 0; i < handlers.size(); i++) {
        if (handlers[i].find("event") != 0) {
          continue;
        }
        string * fullDevFile = new string("/dev/input/" + handlers[i]);
        ifstream devFile((*fullDevFile).c_str());
	if (!devFile.good()) {
          continue;
        }
        pthread_t thread;
        pthread_create(&thread, NULL, get_char_thread, 
            (void *)(*fullDevFile).c_str());
        threads.push_back(&thread);
      }

    }
  }

  pclose(fp);
  pthread_exit(NULL);
}
