// IMediaPlayBackService.aidl
package edu.uic.cs478.musicclient;

// Declare any non-default types here with import statements


interface IMediaPlayBackService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     void openFile(String path);
     void playClip(int clipNumber);
     void pauseClip();
     void resumeClip();
     void stopClip();
     boolean isPlaying();
     int getClipDuration();
     int getCurrentPosition();
     void seekTo(int position);

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}