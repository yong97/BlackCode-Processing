/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class pFaceDetect_PFaceDetect */

#ifndef _Included_pFaceDetect_PFaceDetect
#define _Included_pFaceDetect_PFaceDetect
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     pFaceDetect_PFaceDetect
 * Method:    init
 * Signature: (IILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_pFaceDetect_PFaceDetect_init
  (JNIEnv *, jobject, jint, jint, jstring);

/*
 * Class:     pFaceDetect_PFaceDetect
 * Method:    check
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_pFaceDetect_PFaceDetect_check
  (JNIEnv *, jobject, jintArray);

/*
 * Class:     pFaceDetect_PFaceDetect
 * Method:    getFaces
 * Signature: ()[[I
 */
JNIEXPORT jobjectArray JNICALL Java_pFaceDetect_PFaceDetect_getFaces
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
