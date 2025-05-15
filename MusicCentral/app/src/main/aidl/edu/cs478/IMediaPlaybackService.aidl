// IMediaPlaybackService.aidl
package edu.cs478;


interface IMediaPlaybackService {
    void openFile(String path);
    void playClip(int clipNumber);
    void pauseClip();
    void resumeClip();
    void stopClip();
    boolean isPlaying();
    int getClipDuration();
    int getCurrentPosition();
    void seekTo(int position);
}