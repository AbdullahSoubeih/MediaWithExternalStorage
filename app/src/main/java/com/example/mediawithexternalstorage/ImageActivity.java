package com.example.mediawithexternalstorage;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Random;

public class ImageActivity extends AppCompatActivity {

    //Copy Image from Device to new folder
    final int SELECTED_IMAGE_CODE = 1515;
    private Uri ImageUri;
    String pathSave;
    ////////////////////////////////////////

    private static final String TAG = "ImageActivity";

    EditText PictureNameSave,PictureNameGet,SelectedImageNewName;
    ImageView imageView;
    Button btnCamera,btnSave,btnGet,btnSelectImage,btnSaveSelectedImage;

    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        //Copy Image from Device to new folder
        SelectedImageNewName = findViewById(R.id.SelectedImageNewName);

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { SelectImageFromDevice();
            }
        });

        btnSaveSelectedImage = findViewById(R.id.btnSaveSelectedImage);
        btnSaveSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { SaveSelectedImage();
            }
        });
        /////////////////////////////////////////////////////////////////

        PictureNameSave = findViewById(R.id.PictureNameSave);
        PictureNameGet = findViewById(R.id.PictureNameGet);
        imageView = findViewById(R.id.imageView);
        btnCamera = findViewById(R.id.btnCamera);
        btnSave = findViewById(R.id.btnSave);
        btnGet = findViewById(R.id.btnGet);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePictureByCamera();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SavePictureOnExternalStorage();
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PictureNameGett = PictureNameGet.getText().toString();
                GetPictureFromExternalStorage(PictureNameGett);
            }
        });
    }


    private void TakePictureByCamera(){
        Intent intent_Camera_Picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent_Camera_Picture,0);
    }



    private void SavePictureOnExternalStorage(){

        String PictureNameSavee = PictureNameSave.getText().toString();
        //create path [path in External Storage] to save audio in it

      //  root = Environment.getExternalStorageDirectory().toString();

        File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyTestMedia");   //file have images is ["saved_Picture"]

        if (!myDir.exists()){
            myDir.mkdirs();
        }


       // Random generator = new Random();
       // int n = 1000;
      //  n = generator.nextInt(n);
       // String fname = "Image-" + PictureName + n + ".jpg";   //name of image

        String fname = "Image-" + PictureNameSavee + ".jpg";   //name of image


        File file = new File(myDir,fname);  // Image [fname] inside File/Folder [myDir]

        Log.i(TAG,""+file);

        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
            out.flush();
            out.close();

            //to see saved Picture in Gallery
            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+Environment.getExternalStorageState())));

            Toast.makeText(getApplicationContext(),"Saved Successfully !",Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();

            Toast.makeText(getApplicationContext(),"Saved Error !",Toast.LENGTH_SHORT).show();
        }

    }

    private void GetPictureFromExternalStorage(String PictureNameGett){


        String fname = "/Image-" + PictureNameGett + ".jpg";   //name of image

        String PicturePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyTestMedia"+fname;


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
       // options.inSampleSize = 8;

        final Bitmap bitmap1 = BitmapFactory.decodeFile(PicturePath,options);
        imageView.setImageBitmap(bitmap1);


    }



    ///////////////////////////////Copy Image from Device to new folder////////////////////

    private void SelectImageFromDevice(){
        Intent intent_selectImage = new Intent(Intent.ACTION_GET_CONTENT);
        intent_selectImage.setType("image/*");
        intent_selectImage = Intent.createChooser(intent_selectImage, "Select Your Image");
        startActivityForResult(intent_selectImage, SELECTED_IMAGE_CODE);
    }

    private void SaveSelectedImage(){
        String ImageNewName = SelectedImageNewName.getText().toString();

        pathSave = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MyTestMedia/"
                // + UUID.randomUUID().toString() + "_audio_record_3gp";
                +"Image-"+ ImageNewName + ".jpg";

        //old Path
        String selectedImagePath = getPathOfSelectedImage(ImageUri);  //get path of selected Image in Mobile :)
        File SourceImage_LastPath = new File(selectedImagePath);  //old file

        //new Path
        File Image_NewPath = new File(pathSave);  //new file
        CopyImage(SourceImage_LastPath, Image_NewPath);

        GetPictureFromExternalStorage(ImageNewName); //show Image in imageView

    }

    //to copy Image file from [LastPath] to [NewPath of my App.]
    private void CopyImage(File SourceImage_LastPath, File Image_NewPath) {
        try {

            FileChannel in = new FileInputStream(SourceImage_LastPath).getChannel();  //to get Image from last path
            FileChannel out = new FileOutputStream(Image_NewPath).getChannel();        //to put Image in new path
            try {
                in.transferTo(0, in.size(), out); //copying file  :)
                Toast.makeText(this,"Successfully Copied Image",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this,"Error with Copying Image",Toast.LENGTH_SHORT).show();
            } finally {
                if (in != null) in.close();
                if (out != null) out.close();
            }
            Toast.makeText(this,"Successfully All Process",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this,"Error All Process",Toast.LENGTH_SHORT).show();
        }


    }


    //to get last Path of Image
    private String getPathOfSelectedImage(Uri uri){

        String ImagePath = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri,projection,null,null,null);

        if (cursor == null){
            ImagePath = uri.getPath();
        }else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);

            ImagePath = cursor.getString(column_index);
            cursor.close();
        }

        return ((ImagePath == null || ImagePath.isEmpty()) ? (uri.getPath()) : ImagePath);

    }
/////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK){
            bitmap = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            // Uri uri = Uri.parse(bitmap.toString());

        }

        if (requestCode == SELECTED_IMAGE_CODE){

            if (resultCode == RESULT_OK){
                ImageUri = data.getData();

                Toast.makeText(getApplicationContext(),"Image Selected Successfully !",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Image Selected Failed !",Toast.LENGTH_SHORT).show();
            }

        }

    }
}
