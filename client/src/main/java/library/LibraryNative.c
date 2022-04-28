#include <jni.h>
#include <inttypes.h>
#include <string.h>
#include "LibraryNative.h"

// CLEAN MEMORY

typedef void *(*memset_t)(void *, int, size_t);

static volatile memset_t memset_func = memset;

void memory_clean(void *ptr, size_t len)
{
    memset_func(ptr, 0, len);
}

// END CLEAN MEMORY

// BASH

#define rol64(n, c) (((n) << (c)) | ((n) >> (64 - (c))))

#define bash_s(w0, w1, w2, m1, n1, m2, n2) \
  do { \
    register uint64_t t0, t1, t2; \
    t0 = rol64(w0, m1); \
    w0 ^= w1 ^ w2; \
    t1 = w1 ^ rol64(w0, n1); \
    w1 = t0 ^ t1; \
    w2 ^= rol64(w2, m2) ^ rol64(t1, n2); \
    t1 = w0 | w2; \
    t2 = w0 & w1; \
    t0 = ~w2; \
    t0 |= w1; \
    w1 ^= t1; \
    w2 ^= t2; \
    w0 ^= t0; \
  } while (0)

static const uint64_t __c1  = 0x3bf5080ac8ba94b1;
static const uint64_t __c2  = 0xc1d1659c1bbd92f6;
static const uint64_t __c3  = 0x60e8b2ce0ddec97b;
static const uint64_t __c4  = 0xec5fb8fe790fbc13;
static const uint64_t __c5  = 0xaa043de6436706a7;
static const uint64_t __c6  = 0x8929ff6a5e535bfd;
static const uint64_t __c7  = 0x98bf1e2c50c97550;
static const uint64_t __c8  = 0x4c5f8f162864baa8;
static const uint64_t __c9  = 0x262fc78b14325d54;
static const uint64_t __c10 = 0x1317e3c58a192eaa;
static const uint64_t __c11 = 0x098bf1e2c50c9755;
static const uint64_t __c12 = 0xd8ee19681d669304;
static const uint64_t __c13 = 0x6c770cb40eb34982;
static const uint64_t __c14 = 0x363b865a0759a4c1;
static const uint64_t __c15 = 0xc73622b47c4c0ace;
static const uint64_t __c16 = 0x639b115a3e260567;
static const uint64_t __c17 = 0xede6693460f3da1d;
static const uint64_t __c18 = 0xaad8d5034f9935a0;
static const uint64_t __c19 = 0x556c6a81a7cc9ad0;
static const uint64_t __c20 = 0x2ab63540d3e64d68;
static const uint64_t __c21 = 0x155b1aa069f326b4;
static const uint64_t __c22 = 0x0aad8d5034f9935a;
static const uint64_t __c23 = 0x0556c6a81a7cc9ad;
static const uint64_t __c24 = 0xde8082cd72debc78;

#define P0(x) x
#define P1(x) (((x) < 8) ? 8 + (((x) + 2 * ((x) & 1) + 7) % 8) : \
         (((x) < 16) ? 8 + ((x) ^ 1) : (5 * (x) + 6) % 8))
#define P2(x) P1(P1(x))
#define P3(x) (8 * ((x) / 8) + (((x) % 8) + 4) % 8)
#define P4(x) P1(P3(x))
#define P5(x) P2(P3(x))

#define bash_r(s, p, p_next, i) \
  do { \
    bash_s(s[p( 0)], s[p( 8)], s[p(16)],  8, 53, 14,  1); \
    bash_s(s[p( 1)], s[p( 9)], s[p(17)], 56, 51, 34,  7); \
    bash_s(s[p( 2)], s[p(10)], s[p(18)],  8, 37, 46, 49); \
    bash_s(s[p( 3)], s[p(11)], s[p(19)], 56,  3,  2, 23); \
    bash_s(s[p( 4)], s[p(12)], s[p(20)],  8, 21, 14, 33); \
    bash_s(s[p( 5)], s[p(13)], s[p(21)], 56, 19, 34, 39); \
    bash_s(s[p( 6)], s[p(14)], s[p(22)],  8,  5, 46, 17); \
    bash_s(s[p( 7)], s[p(15)], s[p(23)], 56, 35,  2, 55); \
    s[p_next(23)] ^= __c##i; \
  } while (0)

JNIEXPORT jbyteArray JNICALL Java_LibraryNative_bash_1f(JNIEnv *env, jclass thisClass, jbyteArray inJNIArray) {
    // Step 1: Convert the incoming JNI jbyteArray to C's jbyte[]
    jbyte *inCArray = (*env)->GetByteArrayElements(env, inJNIArray, NULL);
    if (NULL == inCArray) return NULL;
    uint8_t arr[192];
    for (int i = 0; i < 192; i++)
        arr[i] = (uint8_t)inCArray[i];

    // Step 2: Perform its intended operations
    uint64_t s[24];
    memcpy(s, arr, 192);
    bash_r(s, P0, P1,  1);
    bash_r(s, P1, P2,  2);
    bash_r(s, P2, P3,  3);
    bash_r(s, P3, P4,  4);
    bash_r(s, P4, P5,  5);
    bash_r(s, P5, P0,  6);
    bash_r(s, P0, P1,  7);
    bash_r(s, P1, P2,  8);
    bash_r(s, P2, P3,  9);
    bash_r(s, P3, P4, 10);
    bash_r(s, P4, P5, 11);
    bash_r(s, P5, P0, 12);
    bash_r(s, P0, P1, 13);
    bash_r(s, P1, P2, 14);
    bash_r(s, P2, P3, 15);
    bash_r(s, P3, P4, 16);
    bash_r(s, P4, P5, 17);
    bash_r(s, P5, P0, 18);
    bash_r(s, P0, P1, 19);
    bash_r(s, P1, P2, 20);
    bash_r(s, P2, P3, 21);
    bash_r(s, P3, P4, 22);
    bash_r(s, P4, P5, 23);
    bash_r(s, P5, P0, 24);
    memcpy(arr, s, 192);
    memory_clean(s, 192);

    jbyte outCArray[192];
    for (int i = 0; i < 192; i++)
        outCArray[i] = (jbyte)arr[i];

    (*env)->ReleaseByteArrayElements(env, inJNIArray, inCArray, 0); // release resources

    // Step 3: Convert the C's Native jbyte[] to JNI jbyteArray, and return
    jbyteArray outJNIArray = (*env)->NewByteArray(env, 192);  // allocate
    if (NULL == outJNIArray) return NULL;
    (*env)->SetByteArrayRegion(env, outJNIArray, 0, 192, outCArray);  // copy
    return outJNIArray;
}
