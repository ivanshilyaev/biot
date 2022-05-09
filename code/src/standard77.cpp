#include <iostream>
#include <cmath>
#include <string>
#include <sstream>
#include <iomanip>
#include <algorithm>
extern "C" {
    #include "library.h"
}
using namespace std;

size_t decode(uint8_t dest[], size_t count, const char *src) {
    char buf[3];
    size_t i;
    for (i = 0; i < count && *src; i++) {
        buf[0] = *src++;
        buf[1] = '\0';
        if (*src) {
            buf[1] = *src++;
            buf[2] = '\0';
        }
        if (sscanf(buf, "%hhx", &dest[i]) != 1)
            break;
    }
    return i;
}

void reverseAndDecode(uint8_t array[], string s) {
    string temp;
    for (int i = (int) (s.length() - 1); i >= 0; i -= 2) {
        temp += s[i-1];
        temp += s[i];
    }
    decode(array, temp.length(), temp.c_str());
    // reverse
    for (int i = 0; i < s.length() / 4; ++i) {
        uint8_t temp = array[i];
        array[i] = array[s.length() / 2 - 1 - i];
        array[s.length() / 2 - 1 - i] = temp;
    }
}

string encode(const uint8_t v[], const size_t s) {
    stringstream ss;

    ss << hex << setfill('0');

    for (int i = 0; i < s; i++) {
    ss << hex << setw(2) << static_cast<int>(v[i]);
    }

    return ss.str();
}

size_t l;
size_t d;
size_t r; // buf_len
size_t pos;
uint8_t S[192];

#define BASH_PRG_NULL       1 // 0x01, 000000 01 */
#define BASH_PRG_KEY        5 // 0x05, 000001 01 */
#define BASH_PRG_DATA       9 // 0x09, 000010 01 */
#define BASH_PRG_TEXT      13 // 0x0D, 000011 01 */
#define BASH_PRG_OUT       17 // 0x11, 000100 01 */

void start(uint8_t A[], size_t A_len, uint8_t K[], size_t K_len) {
    // 1.
    if (K_len != 0) {
        r = 1536 - l - d * l / 2;
    }
    // 2.
    else {
        r = 1536 - 2 * d * l;
    }
    // 3.
    pos = 8 * (1 + A_len + K_len);
    // 4.
    S[0] = (8 * A_len / 2 + 8 * K_len / 32) % (size_t)(pow(2, 8));
    for (int i = 0; i < A_len; ++i) {
        S[1 + i] = A[i];
    }
    for (int i = 0; i < K_len; ++i) {
        S[1 + A_len + i] = K[i];
    }
    // 5.
    for (int i = (int) pos / 8; i < 1472 / 8; ++i) {
        S[i] = 0;
    }
    // 6.
    S[1472 / 8] = (l / 4 + d) % (size_t)(pow(2, 8));
    for (int i = 1472 / 8 + 1; i < 192; ++i) {
        S[i] = 0;
    }
}

void commit(uint8_t type) {
    // 1.
    S[pos / 8] = S[pos / 8] ^ type;
    // 2.
    S[r / 8] = S[r / 8] ^ 128; // 1000 0000
    // 3.
    bash_f(S);
    // 4.
    pos = 0;
}

void absorb(uint8_t X[], size_t X_len) {
    // 1.
    commit(BASH_PRG_DATA);
    // 2-3.
    int tempPos = 0;
    while (8 * tempPos + r < 8 * X_len) {
        // 3.1
        pos = r;
        // 3.2
        for (int i = 0; i < pos / 8; ++i) {
            S[i] = S[i] ^ X[tempPos + i];
        }
        // 3.3
        bash_f(S);
        pos = 0;
        
        tempPos += r / 8;
    }
    // finish steps 2 & 3
    pos = 8 * (X_len - tempPos);
    for (int i = 0; i < pos  / 8; ++i) {
        S[i] = S[i] ^ X[tempPos + i];
    }
}

void squeeze(uint8_t Y[], size_t n) {
    // 1.
    commit(BASH_PRG_OUT);
    // 2-3.
    int tempPos = 0;
    while (8 * tempPos + r <= n) {
        // 3.1
        for (int i = 0; i < r / 8; ++i) {
            Y[tempPos + i] = S[i];
        }
        tempPos += r / 8;
        // 3.2
        bash_f(S);
    }
    // 4.
    pos = n - 8 * tempPos;
    // 5.
    for (int i = 0; i < pos / 8; ++i) {
        Y[tempPos + i] = S[i];
    }
}

void encrypt(uint8_t X[], size_t X_len, uint8_t Y[]) {
    // 1.
    commit(BASH_PRG_TEXT);
    // 2-4.
    int tempPos = 0;
    while (8 * tempPos + r < 8 * X_len) {
        // 4.1
        pos = r;
        // 4.2
        for (int i = 0; i < pos / 8; ++i) {
            S[i] = S[i] ^ X[tempPos + i];
        }
        // 4.3
        for (int i = 0; i < pos / 8; ++i) {
            Y[tempPos + i] = S[i];
        }
        // 4.4
        bash_f(S);
        pos = 0;
        
        tempPos += r / 8;
    }
    // finish steps 2 & 4
    pos = 8 * (X_len - tempPos);
    for (int i = 0; i < pos / 8; ++i) {
        S[i] = S[i] ^ X[tempPos + i];
    }
    for (int i = 0; i < pos / 8; ++i) {
        Y[tempPos + i] = S[i];
    }
}

void decrypt(uint8_t Y[], size_t Y_len, uint8_t X[]) {
    // 1.
    commit(BASH_PRG_TEXT);
    // 2-4.
    int tempPos = 0;
    while (8 * tempPos + r < 8 * Y_len) {
        // 4.1
        pos = r;
        // 4.2
        for (int i = 0; i < pos / 8; ++i) {
            X[tempPos + i] = S[i] ^ Y[tempPos + i];
        }
        // 4.3
        for (int i = 0; i < pos / 8; ++i) {
            S[i] = Y[tempPos + i];
        }
        // 4.4
        bash_f(S);
        pos = 0;
        
        tempPos += r / 8;
    }
    // finish steps 2 & 4
    pos = 8 * (Y_len - tempPos);
    for (int i = 0; i < pos / 8; ++i) {
        X[tempPos + i] = S[i] ^ Y[tempPos + i];
    }
    for (int i = 0; i < pos / 8; ++i) {
        S[i] = Y[tempPos + i];
    }
}

void authEncrypt(uint8_t varL, uint8_t varD,
                 uint8_t A[], size_t A_len,
                 uint8_t K[], size_t K_len,
                 uint8_t I[], size_t I_len,
                 uint8_t X[], size_t X_len,
                 uint8_t Y[], uint8_t T[]) {
    l = varL;
    d = varD;
    // 1.
    start(A, A_len, K, K_len);
    // 2.1
    absorb(I, I_len);
    // 2.2
    encrypt(X, X_len, Y);
    // 2.3
    squeeze(T, l);
}

void authDecrypt(uint8_t varL, uint8_t varD,
                 uint8_t A[], size_t A_len,
                 uint8_t K[], size_t K_len,
                 uint8_t I[], size_t I_len,
                 uint8_t Y[], size_t Y_len,
                 uint8_t X[], uint8_t T[],
                 bool error) {
    l = varL;
    d = varD;
    // 1.
    start(A, A_len, K, K_len);
    // 2.1
    absorb(I, I_len);
    // 2.2
    decrypt(Y, Y_len, X);
    // 2.3
    uint8_t tempT[l];
    squeeze(tempT, l);
    if (!equal(T, T + l / 8, tempT)) {
        error = true;
    }
}
