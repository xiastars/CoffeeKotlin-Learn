//
// Created by admin on 2019/6/18.
//

#ifndef COFFEEANDROID_LEARN_DZJNICALL_H
#define COFFEEANDROID_LEARN_DZJNICALL_H

#include <jni.h>

class DZJNICall{
public:
    jobject jAudioTrackObj;
    jmethodId jAudioTrackWriteMid;
    JavaVM *javaVM;
    JNIEnv *jniEnv;
    jmethodID jPlayerErrorMid;
    jobject jPlayerObj;
public:
    DZJNICall(JavaVM *javaVM,JNIEnv *jniEnv,jobject jPlayerObj);
    ~DZJNICall();
private:
    void initCreateAudioTrack();
public:
    void callAudioTrackWrite(jbyteArray audioData,int offsetInBytes,int sizeInBytes);
    void callPlayerError(int code,char *msg);
};

#endif //COFFEEANDROID_LEARN_DZJNICALL_H
