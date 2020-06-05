//
// Created by admin on 2019/6/18.
//
#include "DZJNICall.h"
#include "DZConstDefine.h"

DZJNICall::DZJNICall(JavaVM *javaVM,JNIEnv *jniEnv,jobject jPlayerObj){
    this->javaVM = javaVM;
    this->jniEnv = jniEnv;
    this->jPlayerObj = jPlayerObj;
    initCreateAudioTrack();
    jclass jPlayerClass = jniEnv->GetObjectClass(jPlayerObj);
    jPlayerErrorMid = jniEnv ->GetMethodID(JPlayerClass,"onError","(ILjava/lang/String;)V");
}

void DZJNICall::initCreateAudioTrack(){
    jclass jAudioTrackClass = jniEnv ->FindClass("android/media/AudioTrack");
    jmethodID jAudioTrackMid = jniEnv ->GetMethodID(jAudioTrackClass,"<init>","(IIIIII)V");

    int streamType = 3;
    int sampleRateInHz = AUDIO_SAMPLE_RATE;
    int channelConfig = (0x4 | 0x8);
    int audioFormat = 2;
    int mode = 1;

    jmethodID getMinBufferSizeMid = jniEvn->GetStaticMethodID(jAudioTrackClass,"getMinBufferSize","(III)I)";
    int bufferSizeInBytes = jniEnv->CallStaticIntMethod(jAudioTrackClass,getMinBufferSizeMid,sampleRateInHz,channelConfig,audioFormat);
    jAudioTrackObj = jniEnv->NewObject(jAudioTrackClass,jAudioTrackMid,streamType,sampleRateInHz,channelConfig,audioFormat,bufferSizeInBytes,mode);
    jMethodId playMid = jniEnv->GetMethodID(jAudioTrackClass,"play","()V");
    jniEnv ->CallVoidMethod(jAudioTrackObj,playMid);

}