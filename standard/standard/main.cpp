#include <iostream>
#include <cmath>
#include <string>
#include <sstream>
#include <iomanip>
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
    for (int i = 0; i < X_len - tempPos; ++i) {
        S[i] = S[i] ^ X[tempPos + i];
    }
}

void squeeze(uint8_t Y[], int n) {
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

void encrypt() {
    
}

int main() {
//    string input = "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4FF33C657B637C306ADD4EA7799EB23D313E98B56E27D3BCCF591E181F4C5AB793E9DEE72C8F0C0FA62DDB49F46F73964706075316ED247A3739CBA38303A98BF692BD9B1CE5D141015445FBC95E4D0EF2682080AA227D642F2687F93490405511";
//    string expectedOutput = "8FE727775EA7F140B95BB6A200CBB28C7F0809C0C0BC68B7DC5AEDC841BD94E403630C301FC255DF5B67DB53EF65E376E8A4D797A6172F2271BA48093173D329C3502AC946767326A2891971392D3F7089959F5D61621238655975E00E2132A0D5018CEEDB17731CCD88FC50151D37C0D4A3359506AEDC2E6109511E7703AFBB014642348D8568AA1A5D9868C4C7E6DFA756B1690C7C2608A2DC136F5997AB8FBB3F4D9F033C87CA6070E117F099C4094972ACD9D976214B7CED8E3F8B6E058E";
//    // decode
//    size_t length = input.length() / 2;
//    uint8_t array[length];
//    decode(array, input.length(), input.c_str());
//    // bash_f
//    bash_f(array);
//    // encode
//    string output = encode(array, length);
//    cout << output << endl;
    
    uint8_t A[16];
    reverseAndDecode(A, "B194BAC80A08F53B366D008E584A5DE4");
    uint8_t K[32];
    reverseAndDecode(K, "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
    uint8_t I[49];
    reverseAndDecode(I, "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4FF33C657B637C306ADD4EA7799EB23D313E");
    
    l = 256;
    d = 1;
    start(A, 16, K, 32);
    absorb(I, 49);
    
    for (uint8_t i = 0; i < 192; ++i) {
        cout << unsigned(S[i]) << endl;
    }

    return 0;
}
