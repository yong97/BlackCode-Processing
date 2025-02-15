/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_sourceforge_jartoolkit_core_JARToolKit */

#ifndef _Included_net_sourceforge_jartoolkit_core_JARToolKit
#define _Included_net_sourceforge_jartoolkit_core_JARToolKit
#ifdef __cplusplus
extern "C" {
#endif
	/* Inaccessible static: m_JARToolKit */
	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    utilSleep
	* Signature: (I)V
	*/
	JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_utilSleep
		(JNIEnv *, jclass, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    utilTimer
	* Signature: ()D
	*/
	JNIEXPORT jdouble JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_utilTimer
		(JNIEnv *, jclass);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    utilTimerReset
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_utilTimerReset
		(JNIEnv *, jclass);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    activatePattern
	* Signature: (I)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_activatePattern
		(JNIEnv *, jobject, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    arMultiDeactivate
	* Signature: (I)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_arMultiDeactivate
		(JNIEnv *, jobject, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    arMultiFreeConfig
	* Signature: (I)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_arMultiFreeConfig
		(JNIEnv *, jobject, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    deactivatePattern
	* Signature: (I)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_deactivatePattern
		(JNIEnv *, jobject, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    detectMarker
	* Signature: ([II)[I
	*/
	JNIEXPORT jintArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_detectMarker___3II
		(JNIEnv *, jobject, jintArray, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    detectMarker
	* Signature: (JI)[I
	*/
	JNIEXPORT jintArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_detectMarker__JI
		(JNIEnv *, jobject, jlong, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    detectMarkerLite
	* Signature: ([II)[I
	*/
	JNIEXPORT jintArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_detectMarkerLite___3II
		(JNIEnv *, jobject, jintArray, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    detectMarkerLite
	* Signature: (JI)[I
	*/
	JNIEXPORT jintArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_detectMarkerLite__JI
		(JNIEnv *, jobject, jlong, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    freePattern
	* Signature: (I)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_freePattern
		(JNIEnv *, jobject, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getCamTransMatrix
	* Signature: ()[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrix__
		(JNIEnv *, jobject);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getCamTransMatrix
	* Signature: ([D)V
	*/
	JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrix___3D
		(JNIEnv *, jobject, jdoubleArray);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getCamTransMatrixJava3D
	* Signature: ()[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrixJava3D__
		(JNIEnv *, jobject);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getCamTransMatrixJava3D
	* Signature: (DD)[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrixJava3D__DD
		(JNIEnv *, jobject, jdouble, jdouble);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getCamTransMatrixJava3D
	* Signature: ([D)V
	*/
	JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrixJava3D___3D
		(JNIEnv *, jobject, jdoubleArray);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getCamTransMatrixJava3D
	* Signature: ([DDD)V
	*/
	JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getCamTransMatrixJava3D___3DDD
		(JNIEnv *, jobject, jdoubleArray, jdouble, jdouble);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getTransMatrix
	* Signature: ([DIIFF)Z
	*/
	JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrix___3DIIFF
		(JNIEnv *, jobject, jdoubleArray, jint, jint, jfloat, jfloat);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getTransMatrix
	* Signature: (IIFF)[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrix__IIFF
		(JNIEnv *, jobject, jint, jint, jfloat, jfloat);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getTransMatrixCont
	* Signature: ([DIIFF[D)Z
	*/
	JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixCont___3DIIFF_3D
		(JNIEnv *, jobject, jdoubleArray, jint, jint, jfloat, jfloat, jdoubleArray);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getTransMatrixCont
	* Signature: (IIFF[D)[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixCont__IIFF_3D
		(JNIEnv *, jobject, jint, jint, jfloat, jfloat, jdoubleArray);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getTransMatrixContJava3D
	* Signature: ([DIIFF[D)Z
	*/
	JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixContJava3D___3DIIFF_3D
		(JNIEnv *, jobject, jdoubleArray, jint, jint, jfloat, jfloat, jdoubleArray);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getTransMatrixContJava3D
	* Signature: (IIFF[D)[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixContJava3D__IIFF_3D
		(JNIEnv *, jobject, jint, jint, jfloat, jfloat, jdoubleArray);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getTransMatrixJava3D
	* Signature: ([DIIFF)Z
	*/
	JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixJava3D___3DIIFF
		(JNIEnv *, jobject, jdoubleArray, jint, jint, jfloat, jfloat);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    getTransMatrixJava3D
	* Signature: (IIFF)[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_getTransMatrixJava3D__IIFF
		(JNIEnv *, jobject, jint, jint, jfloat, jfloat);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    initCparam
	* Signature: ()I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_initCparam
		(JNIEnv *, jobject);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    loadPattern
	* Signature: (Ljava/lang/String;)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_loadPattern
		(JNIEnv *, jobject, jstring);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    multiGetTransMat
	* Signature: ([DII)Z
	*/
	JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiGetTransMat___3DII
		(JNIEnv *, jobject, jdoubleArray, jint, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    multiGetTransMat
	* Signature: (II)[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiGetTransMat__II
		(JNIEnv *, jobject, jint, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    multiGetTransMatJava3D
	* Signature: ([DII)Z
	*/
	JNIEXPORT jboolean JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiGetTransMatJava3D___3DII
		(JNIEnv *, jobject, jdoubleArray, jint, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    multiGetTransMatJava3D
	* Signature: (II)[D
	*/
	JNIEXPORT jdoubleArray JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiGetTransMatJava3D__II
		(JNIEnv *, jobject, jint, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    multiPatternActivate
	* Signature: (I)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiPatternActivate
		(JNIEnv *, jobject, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    multiReadConfigFile
	* Signature: (Ljava/lang/String;)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_multiReadConfigFile
		(JNIEnv *, jobject, jstring);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    paramChangeSize
	* Signature: (II)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_paramChangeSize
		(JNIEnv *, jobject, jint, jint);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    paramDisplay
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_paramDisplay
		(JNIEnv *, jobject);

	/*
	* Class:     net_sourceforge_jartoolkit_core_JARToolKit
	* Method:    paramLoad
	* Signature: (Ljava/lang/String;)I
	*/
	JNIEXPORT jint JNICALL Java_net_sourceforge_jartoolkit_core_JARToolKit_paramLoad
		(JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif
