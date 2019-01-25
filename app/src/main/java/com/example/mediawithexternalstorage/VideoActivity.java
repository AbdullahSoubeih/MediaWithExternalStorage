package com.example.mediawithexternalstorage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class VideoActivity extends AppCompatActivity {

    final int VIDEO_REQUEST_CODE = 500;
    final int SELECTED_VIDEO_CODE = 600;

    EditText VideoNameToSave,VideoNameFromDevice,SelectedVideoNewName;
    Button btnCaptureVideo, btnPlayVideo,btnGetVideo,btnSelectVideoFromDevice,btnSaveSelectedAudio;

    private Uri videoUri;
    String pathSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        ///////////////To Copy Video from Device to App Folder//////////////////////
        SelectedVideoNewName = findViewById(R.id.SelectedVideoNewName);
        btnSelectVideoFromDevice = findViewById(R.id.btnSelectVideoFromDevice);
        btnSelectVideoFromDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { SelectVideoFromDevice();
            }
        });

        btnSaveSelectedAudio = findViewById(R.id.btnSaveSelectedVideo);
        btnSaveSelectedAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSelectedVideo();
            }
        });
        ////////////////////////////////////////////////////////////////////////////


        btnCaptureVideo = findViewById(R.id.btnCaptureVideo);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnGetVideo = findViewById(R.id.btnGetVideo);

        VideoNameToSave = findViewById(R.id.VideoNameToSave);
        VideoNameFromDevice = findViewById(R.id.VideoNameFromDevice);


        btnGetVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { GetVideoFrom_ExternalStorage_ByName(VideoNameFromDevice.getText().toString()); }
        });


        btnCaptureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { CaptureVideo();

            }
        });

        btnPlayVideo.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { startActivity(new Intent(VideoActivity.this,VideoPlayActivity.class).putExtra("videoUri_KEY",videoUri.toString())); }});




    }



    //create file to save video in external storage
    public File getFilepath(String VideoNamee){

        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/MyTestMedia");
        if (!folder.exists()){ //if folder is not found [exists] in device --> then create it
            folder.mkdir();    //create a folder [sdcard/video_app]
        }


        File video_file = new File(folder,"Video-"+VideoNamee+".mp4");   //put {video ["video_"+videoName+"mp4"] } inside folder[sdcard/video_app]

        return video_file;
    }


private void CaptureVideo(){
    String videoName = VideoNameToSave.getText().toString();

    Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

    //////////To save Captured Video in External Storage/////////
    try {

        File video_File = getFilepath(videoName);
        Uri video_uri = Uri.fromFile(video_File);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);

        //Toast.makeText(getApplicationContext(),"Video Successfully Saved !",Toast.LENGTH_SHORT).show();
    }catch (Exception e){
       // Toast.makeText(getApplicationContext(),"Video Failed Saved !",Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////////////////

    startActivityForResult(camera_intent,VIDEO_REQUEST_CODE);
}

    private void GetVideoFrom_ExternalStorage_ByName(String VideoName){

        try {
            String Vname = "/Video-" + VideoName + ".mp4";   //name of Video
            videoUri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/MyTestMedia"+Vname);

            Toast.makeText(getApplicationContext(),"Get Video From External Storage Successfully !",Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Get Video From External Storage Failed !",Toast.LENGTH_SHORT).show();
        }


    }



    //////////////////////////Copy Video from Device to new folder//////////////////////

    private void SelectVideoFromDevice(){
        Intent intent_selectVideo = new Intent(Intent.ACTION_GET_CONTENT);
        intent_selectVideo.setType("video/*");
        intent_selectVideo = Intent.createChooser(intent_selectVideo, "Select Your Video");
        startActivityForResult(intent_selectVideo, SELECTED_VIDEO_CODE);
    }

    private void SaveSelectedVideo(){
        String VideoNewName = SelectedVideoNewName.getText().toString();

        pathSave = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MyTestMedia/"
                // + UUID.randomUUID().toString() + "_audio_record_3gp";
                +"Video-"+ VideoNewName + ".mp4";

        //old Path
        String selectedVideoPath = getPathOfSelectedVideo(videoUri);  //get path of selected Video in Mobile :)
        File SourceVideo_LastPath = new File(selectedVideoPath);  //old file

        //new Path
        File Video_NewPath = new File(pathSave);  //new file
        CopyVideo(SourceVideo_LastPath, Video_NewPath);

    }

    //to copy Video file from [LastPath] to [NewPath of my App.]
    private void CopyVideo(File SourceVideo_LastPath, File Video_NewPath) {
        try {

            FileChannel in = new FileInputStream(SourceVideo_LastPath).getChannel();  //to get video from last path
            FileChannel out = new FileOutputStream(Video_NewPath).getChannel();        //to put video in new path
            try {
                in.transferTo(0, in.size(), out); //copying file  :)
                Toast.makeText(this,"Successfully Copied Video",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this,"Error with Copying Video",Toast.LENGTH_SHORT).show();
            } finally {
                if (in != null) in.close();
                if (out != null) out.close();
            }
            Toast.makeText(this,"Successfully All Process",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this,"Error All Process",Toast.LENGTH_SHORT).show();
        }


    }

    //to get last Path of Video
    private String getPathOfSelectedVideo(Uri uri){

        String VideoPath = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri,projection,null,null,null);

        if (cursor == null){
            VideoPath = uri.getPath();
        }else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);

            VideoPath = cursor.getString(column_index);
            cursor.close();
        }

        return ((VideoPath == null || VideoPath.isEmpty()) ? (uri.getPath()) : VideoPath);

    }

    ///////////////////////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == VIDEO_REQUEST_CODE){

            if (resultCode == RESULT_OK){
                videoUri = data.getData();

                Toast.makeText(getApplicationContext(),"Video Successfully Recorded !",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Video Capture Failed !",Toast.LENGTH_SHORT).show();
            }

        }


        if (requestCode == SELECTED_VIDEO_CODE){

            if (resultCode == RESULT_OK){
                videoUri = data.getData();

                Toast.makeText(getApplicationContext(),"Video Selected Successfully !",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Video Selected Failed !",Toast.LENGTH_SHORT).show();
            }

        }

    }



}
