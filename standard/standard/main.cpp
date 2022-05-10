#include <iostream>
#include "standard77.hpp"
using namespace std;

void authEncryptAndDecryptDemo() {
    size_t l = 256;
    size_t d = 1;
    
    uint8_t A[16];
    reverseAndDecode(A, "B194BAC80A08F53B366D008E584A5DE4");
    uint8_t K[32];
    reverseAndDecode(K, "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
    uint8_t I[49];
    reverseAndDecode(I, "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4FF33C657B637C306ADD4EA7799EB23D313E");
    uint8_t X[192] = {0};
    
    uint8_t Y[192];
    uint8_t T[l / 8];
    
    authEncrypt(l, d, A, 16, K, 32, I, 49, X, 192, Y, T);

    string encrypted = encode(Y, 192);
    cout << encrypted << endl;
    string mac = encode(T, 32);
    cout << mac << endl;
    
    bool error = false;
    authDecrypt(l, d, A, 16, K, 32, I, 49, Y, 192, X, T, error);
    if (!error) {
        string decrypted = encode(X, 192);
        cout << decrypted << endl;
    }
}

int main() {
    size_t l = 256;
    size_t d = 1;
    
    uint8_t A[16];
    reverseAndDecode(A, "B194BAC80A08F53B366D008E584A5DE4");
    uint8_t K[32];
    reverseAndDecode(K, "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
    uint8_t I[49];
    reverseAndDecode(I, "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4FF33C657B637C306ADD4EA7799EB23D313E");
    
    string hexY = "066015";
    string hexT = "B0FBE2437A715029B42D7103899CF7404B548D8C3A9F2DA368CCDC94F106EFEA";
    
    size_t Y_len = hexY.length() / 2;
      uint8_t Y[Y_len];
      decode(Y, Y_len, hexY.c_str());

      uint8_t X[Y_len];

      size_t T_len = hexT.length() / 2;
      uint8_t T[T_len];
      decode(T, T_len, hexT.c_str());

      bool error = false;
      authDecrypt(l, d, A, 16, K, 32, I, 49, Y, Y_len, X, T, error);
    
    if (error == false) {
        string decrypted = encode(X, Y_len);
        cout << decrypted << endl;
    }
    else {
        cout << "error" << endl;
    }

    return 0;
}
