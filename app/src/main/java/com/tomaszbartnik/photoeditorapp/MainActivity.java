package com.tomaszbartnik.photoeditorapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements View.OnClickListener {

    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]    permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // Image loading result to pass to startActivityForResult method.
    private static int LOAD_IMAGE_RESULTS = 1;

    private Button loadButton;
    private Button previewButton;
    private Button saveButton;
    private ImageView image;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find references to the GUI objects
        loadButton = (Button) findViewById(R.id.button);
        previewButton = (Button) findViewById(R.id.button2);
        saveButton = (Button) findViewById(R.id.button3);
        image = (ImageView) findViewById(R.id.imageView2);
        radioGroup = (RadioGroup) findViewById(R.id.RGroup);

        loadButton.setOnClickListener(this);
        previewButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.filter1) {
                    Toast.makeText(MainActivity.this, "choice: A",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.filter2) {
                    Toast.makeText(MainActivity.this, "choice: B",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.filter3){
                    Toast.makeText(MainActivity.this, "choice: C",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.filter4){
                    Toast.makeText(MainActivity.this, "choice: D",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.filter5){
                    Toast.makeText(MainActivity.this, "choice: E",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            // Now we need to set the GUI ImageView data with data read from the picked file.
            image.setImageBitmap(BitmapFactory.decodeFile(imagePath));

            Toast.makeText(MainActivity.this, "Picture loaded successfully", Toast.LENGTH_SHORT).show();
            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button:
                EasyPermissions.requestPermissions(this, "Access for storage",101, galleryPermissions);
                // Create the Intent for Image Gallery.
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
                startActivityForResult(i, LOAD_IMAGE_RESULTS);
                break;
            case R.id.button2:
                // do stuff;
                break;
            case R.id.button3:
                EasyPermissions.requestPermissions(this, "Access for storage",101, galleryPermissions);
                BitmapDrawable draw = (BitmapDrawable) image.getDrawable();
                Bitmap bitmap = draw.getBitmap();

                try {
                    FileOutputStream outStream = null;
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/PhotoEditorApp");
                    dir.mkdirs();
                    String fileName = String.format("%d.jpg", System.currentTimeMillis());
                    File outFile = new File(dir, fileName);
                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                break;
        }

    }


}
