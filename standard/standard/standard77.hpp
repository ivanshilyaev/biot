#ifndef standard77_hpp
#define standard77_hpp

#include <stdio.h>
#include <cmath>
#include <string>
#include <sstream>
#include <iomanip>
#include <algorithm>
extern "C" {
    #include "library.h"
}
using namespace std;

size_t decode(uint8_t dest[], size_t count, const char *src);

void reverseAndDecode(uint8_t array[], string s);

string encode(const uint8_t v[], const size_t s);

void authEncrypt(uint8_t varL, uint8_t varD,
                 uint8_t A[], size_t A_len,
                 uint8_t K[], size_t K_len,
                 uint8_t I[], size_t I_len,
                 uint8_t X[], size_t X_len,
                 uint8_t Y[], uint8_t T[]);

void authDecrypt(uint8_t varL, uint8_t varD,
                 uint8_t A[], size_t A_len,
                 uint8_t K[], size_t K_len,
                 uint8_t I[], size_t I_len,
                 uint8_t Y[], size_t Y_len,
                 uint8_t X[], uint8_t T[],
                 bool error);

#endif /* standard77_hpp */
