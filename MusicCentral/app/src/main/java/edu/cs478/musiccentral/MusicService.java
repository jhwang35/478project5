package edu.cs478.musiccentral;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.cs478.IMediaPlaybackService;

/* The MusicService class is used to provide functionality for a music player.
 * It acts as a bound service allowing for other components to interact with it through the aidl file
 * aidl file declares the Service interface with methods
 * The stub is an abstract implementation of the IMediaPlayBackService interface
 * The binder object is an instance of the stub class which delegates method calls to corresponding service methods
 * Here we also implement the functions declared in the binder
 */

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private int currentClipNumber = 0;
    private String currentFilePath = "";
    private final Map<Integer, Integer> clipResources = new HashMap<Integer, Integer>() {{
        put(1, R.raw.groovy);
        put(2, R.raw.country);
        put(3, R.raw.lofi);
        put(4, R.raw.rock);
    }};

    // instance of our stub(skeleton generated from aidl) as binder obj to define methods
    private final IMediaPlaybackService.Stub binder = new IMediaPlaybackService.Stub() {
        @Override
        public void playClip(int clipNumber) throws RemoteException {
            MusicService.this.playClip(clipNumber);
        }

        @Override
        public void pauseClip() throws RemoteException {
            MusicService.this.pauseClip();
        }

        @Override
        public void resumeClip() throws RemoteException {
            MusicService.this.resumeClip();
        }

        @Override
        public void stopClip() throws RemoteException {
            MusicService.this.stopClip();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return MusicService.this.isPlaying();
        }

        @Override
        public int getClipDuration() throws RemoteException {
            return MusicService.this.getClipDuration();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return MusicService.this.getCurrentPosition();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            MusicService.this.seekTo(position);
        }

        @Override
        public void openFile(String filePath) throws RemoteException {
            MusicService.this.openFile(filePath);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playClip(int clipNumber) {
        // goes through hashmap of songs included in res/raw folder
        if (clipResources.containsKey(clipNumber)) {
            currentClipNumber = clipNumber;
            int resourceId = clipResources.get(clipNumber);
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                try {
                    /* AssetFileDescriptor provides info on an app's assets/raw resources
                     * commonly used to access and play media files
                     * @param File Description: is what the API uses to read the file's data
                     * @param Start Offset: starting portion of where the data begins
                     * @param Length: length of data in bytes, how much of the file should be read
                     */
                    AssetFileDescriptor afd = getResources().openRawResourceFd(resourceId);
                    if (afd != null) {
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                } catch (IOException e) {
                    Log.e("MusicService", "Error playing clip", e);
                }
            }
        }
    }

    private void pauseClip() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void resumeClip() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void stopClip() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private int getClipDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    private int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    private void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    private void openFile(String filePath) {
        currentFilePath = filePath;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                Log.e("MusicService", "Error opening file: " + filePath, e);
            }
        }
    }
}