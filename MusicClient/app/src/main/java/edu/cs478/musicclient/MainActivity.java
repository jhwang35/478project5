package edu.cs478.musicclient;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.cs478.IMediaPlaybackService;

/**
 * This app is used to bind to the MusicCentral app and access the service API
 * The service API will allow us to control the media player
 */
public class MainActivity extends AppCompatActivity {

    private boolean isBound = false;
    private Button play;
    private Button pause;
    private Button resume;
    private Button bind;
    private Button unbind;
    private IMediaPlaybackService musicService;
    private ListView songList;

    String[] songs = {
            "Country",
            "Groovy",
            "Lofi",
            "Rock"
    };
    private void updateButtons() {
        play.setEnabled(isBound);
        pause.setEnabled(isBound);
        resume.setEnabled(isBound);
        bind.setEnabled(!isBound);
        unbind.setEnabled(isBound);
    }


    /* manages the connection between music central and music client
     * provides a binder object used to interact with the service
     * bound service runs even when app is minimized allowing us to play music in the background
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = IMediaPlaybackService.Stub.asInterface(service);
            isBound = true;
            updateButtons();

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isBound = false;
            updateButtons();
        }
    };

    @SuppressLint("WrongViewCast")
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
        bind = findViewById(R.id.bindBtn);
        unbind = findViewById(R.id.unbindBtn);
        songList = findViewById(R.id.songList);

        Intent intent = new Intent("edu.cs478.IMediaPlaybackService");
        intent.setPackage("edu.cs478.musiccentral");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                songs
        );

        songList.setAdapter(adapter);


        // set listeners for buttons to call respective functions
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    try {
                        Log.d("MainActivity", "Calling playClip(0)");
                        musicService.playClip(0);
                    } catch (RemoteException e) {
                        Log.e("MainActivity", "RemoteException in playClip", e);                    }
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
                if (isBound) {
                    try {
                        musicService.resumeClip();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBound) {
                    Intent intent = new Intent("edu.cs478.IMediaPlaybackService");
                    intent.setPackage("edu.cs478.musiccentral");
                    bindService(intent, connection, Context.BIND_AUTO_CREATE);
                }
                else {
                    bind.setEnabled(false);
                    unbind.setEnabled(true);
                }
            }
        });

        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    unbindService(connection);
                    unbind.setEnabled(false);
                    updateButtons();
                }
            }
        });

        songList.setOnItemClickListener((parent, view, position, id) -> {
            if (isBound && musicService != null) {
                try {
                    musicService.playClip(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    } // end onCreate

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}