package edu.cs478.musiccentral;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.cs478.musiccentral.IMediaPlaybackService;

/* This is our bound service app which deals with storing and playing music
 * This exposes the api to our client allowing them to interact with it in MusicClient
 */
public class MainActivity extends AppCompatActivity {
    private IMediaPlaybackService musicService;
    private boolean isBound = false;
    private SeekBar seekBar;

    private Button play;
    private Button pause;
    private Button resume;


    /* manages the connection between music central and music client
     * provides a binder object used to interact with the service
     * bound service runs even when app is minimized allowing us to play music in the background
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = IMediaPlaybackService.Stub.asInterface(service);
            isBound = true;
            try {
                seekBar.setMax(musicService.getClipDuration());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // bind interface elements
        play = findViewById(R.id.playBtn);
        pause = findViewById(R.id.pauseBtn);
        resume = findViewById(R.id.resumeBtn);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = new Intent(this, MusicService.class);
        // binds MusicService to our mainactivity
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


        // set listeners for buttons to call respective functions from the music service class
        //

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    try {
                        musicService.playClip(1);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    try {
                        musicService.pauseClip();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound){
                    try {
                        musicService.resumeClip();
                    } catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}