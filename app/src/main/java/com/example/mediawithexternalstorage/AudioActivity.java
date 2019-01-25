package com.example.mediawithexternalstorage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class AudioActivity extends AppCompatActivity {

    Button btnStartRecord, btnStopRecord, btnPlay, btnStop, btnChooseAudio, btnSaveSelectedAudio;
    EditText audioNameTxt;
    TextView audioNameView;
    String pathSave;
    Uri uri_Selected_Audio_From_Device;
    MediaRecorder mediaRecorder; //to record audio
    MediaPlayer mediaPlayer;     //to play audio

    final int REQUEST_PERMISSION_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);


        //Request Runtime Permission
        if (!checkPermissionFromDevice())
            requestPermission();


        //initialize of Recording Part
        btnStartRecord = findViewById(R.id.btnStartRecord);
        btnStopRecord = findViewById(R.id.btnStopRecord);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        btnChooseAudio = findViewById(R.id.btnChooseAudio);
        btnSaveSelectedAudio = findViewById(R.id.btnSaveAudio);
        audioNameTxt = findViewById(R.id.audioNameTxt);
        audioNameView = findViewById(R.id.audioNameView);


        //to choose (Selecting) Audio from device and save it
        btnChooseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAudio();
            }
        });

        btnSaveSelectedAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSelectedAudio();
            }
        });

        //////////////////////////to record audio and save it///////////////////////////////
        btnStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/*Request Permission*/
                if (checkPermissionFromDevice()) {

                    String audioName = audioNameTxt.getText().toString();
                    //create path [path in External Storage] to save audio in it

                    pathSave = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/MyTestMedia/"
                            // + UUID.randomUUID().toString() + "_audio_record_3gp";
                            +"Audio-"+ audioName + ".mp3";

                  //  pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyAppFiles/" + audioName;


                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);

                    Toast.makeText(AudioActivity.this, "Recording....", Toast.LENGTH_SHORT).show();

                } else {
                    requestPermission();
                }
            }

        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecordAudio();
            }
        });

        ////////////////////to Play & Stop Audio ///////////////////////
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAudio();

            }
        });


    }

    private void setupMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);  //put a pathSave of recorded audio

    }

    private void requestPermission() { //to request needed permissions
        ActivityCompat.requestPermissions(this, new String[]{

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO

        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && read_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    public void stopRecordAudio() {
        mediaRecorder.stop();
        Toast.makeText(AudioActivity.this, "Stop Recording", Toast.LENGTH_SHORT).show();

        btnStopRecord.setEnabled(false);  //after stop recording
        btnPlay.setEnabled(true);
        btnStartRecord.setEnabled(true);
        btnStop.setEnabled(false);
    }

    public void playAudio() {
        String audioName = audioNameTxt.getText().toString();

        //create path [path in External Storage] to save audio in it
        /*
        pathSave = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/"
                // + UUID.randomUUID().toString() + "_audio_record_3gp";
                + audioName + "_audio_record_mp3";
*/
        // String pathSave = "/storage/emulated/0/Download/Sounds/cat.mp3";

        pathSave = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MyTestMedia/"
                // + UUID.randomUUID().toString() + "_audio_record_3gp";
                +"Audio-"+ audioName + ".mp3";

       // pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyAppFiles/" + audioName;


        btnStop.setEnabled(true);
        btnStopRecord.setEnabled(false);
        btnStartRecord.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        try {  //get audio from external storage by path [ pathSave ] to play it
            mediaPlayer.setDataSource(pathSave);
            mediaPlayer.prepare();
            audioNameView.setText(audioName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();
        Toast.makeText(AudioActivity.this, "Playing....", Toast.LENGTH_SHORT).show();

    }

    public void stopAudio() {
        btnStopRecord.setEnabled(false);
        btnStartRecord.setEnabled(true);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(true);

        if (mediaPlayer != null) { //to clean the mediaPlayer
            mediaPlayer.stop();
            mediaPlayer.release();
            setupMediaRecorder(); //to setup the Recorder new time
        }
    }


    /*
    //get Audio from SD Card and play it (//play Audio from Device)
    public void playAudioFromStorage(){
        mediaPlayer = new MediaPlayer();
        String media_path = "/storage/emulated/0/Download/Sounds/cat.mp3";
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Uri uri = Uri.parse(media_path);
        try {
            mediaPlayer.setDataSource(getApplicationContext(),uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(),"playback started",Toast.LENGTH_SHORT).show();
            playMusicBtn.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"playback error",Toast.LENGTH_SHORT).show();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playMusicBtn.setEnabled(true);
                mediaPlayer.release();
                mediaPlayer = null;
                Toast.makeText(getApplicationContext(),"playback finished",Toast.LENGTH_SHORT).show();

            }
        });
    }
*/


    public void selectAudio() {
        Intent intent_selectAudio = new Intent(Intent.ACTION_GET_CONTENT);
        intent_selectAudio.setType("audio/*");
        intent_selectAudio = Intent.createChooser(intent_selectAudio, "Select Your Audio");
        startActivityForResult(intent_selectAudio, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 200 && resultCode == RESULT_OK) {
            uri_Selected_Audio_From_Device = data.getData();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void SaveSelectedAudio() {


        String audioName = audioNameTxt.getText().toString();
       // pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyAppFiles/" + audioName;

        pathSave = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MyTestMedia/"
                // + UUID.randomUUID().toString() + "_audio_record_3gp";
                +"Audio-"+ audioName + ".mp3";

      //  String selectedAudioPath = uri_Selected_Audio_From_Device.getPath();  //get path of selected Audio in Mobile :)

        String selectedAudioPath = getPathOfAudio(uri_Selected_Audio_From_Device);  //get path of selected Audio in Mobile :)


        File SourceAudio_LastPath = new File(selectedAudioPath);
        //String fileName = uri_Selected_Audio_From_Device.getLastPathSegment(); //to get last name of Audio
        File Audio_NewPath = new File(pathSave);

        CopyAudio(SourceAudio_LastPath, Audio_NewPath);

    }

    //to copy Audio file from [LastPath] to [NewPath of my App.]
    private void CopyAudio(File SourceAudio_LastPath, File Audio_NewPath) {
        try {

            FileChannel in = new FileInputStream(SourceAudio_LastPath).getChannel();  //to get audio from last path
            FileChannel out = new FileOutputStream(Audio_NewPath).getChannel();        //to put audio in new path
            try {
                in.transferTo(0, in.size(), out); //copying file  :)
                Toast.makeText(this,"Successfully Copied Audio",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this,"Error with Copying Audio",Toast.LENGTH_SHORT).show();
            } finally {
                if (in != null) in.close();
                if (out != null) out.close();
            }
            Toast.makeText(this,"Successfully All Process",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this,"Error All Process",Toast.LENGTH_SHORT).show();
        }


    }

    //to get last Path of Audio
    private String getPathOfAudio(Uri uri){

        String AudioPath = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri,projection,null,null,null);

        if (cursor == null){
            AudioPath = uri.getPath();
        }else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);

            AudioPath = cursor.getString(column_index);
            cursor.close();
        }

        return ((AudioPath == null || AudioPath.isEmpty()) ? (uri.getPath()) : AudioPath);

    }

}