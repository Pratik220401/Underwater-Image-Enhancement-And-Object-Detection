package com.example.underwater;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.underwater.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ObjectDetection extends AppCompatActivity {
    TextView result, confidence;
    ImageView imageView;
    Button picture,gal;
    int imageSize = 224;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);


        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        gal=(Button)findViewById(R.id.buttontt);

        picture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, 1);
                    } else {

                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                    }
                }
            }
        });

        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 3);
            }
        });
    }
    public void classifyImage(Bitmap image) {
        try{
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize *3);
            byteBuffer. order(ByteOrder.nativeOrder());

            int[] intValue = new int[imageSize * imageSize];
            image.getPixels(intValue, 0, image.getWidth(), 0,0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for(int i =0 ; i <imageSize; i++){
                for (int j =0 ; j < imageSize; j++) {
                    int val = intValue[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val  & 0xFF) * (1.f / 255.f));

                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            Model.Outputs outputs= model.process(inputFeature0);
            TensorBuffer outputFeatures0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] yolo = outputFeatures0.getFloatArray();

            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < yolo.length; i++){
                if(yolo[i] > maxConfidence){
                    maxConfidence = yolo[i];
                    maxPos = i;

                }
            }
            String[] classes = {"Seashell","Fish","Human","Coral","Not Classified"};
            result.setText(classes[maxPos]);
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/search?q="+result.getText())));
                }
            });
            model.close();

        }catch (IOException e){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

                imageView.setImageBitmap(image);
               // demoTxt.setVisibility(View.GONE);
                //clickHere.setVisibility(View.VISIBLE);
                confidence.setVisibility(View.VISIBLE);
                result.setVisibility(View.VISIBLE);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);

            } else {
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
/*Under Water Code
    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer=ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
byteBuffer.order(ByteOrder.nativeOrder());
int[] intValues=new int[imageSize*imageSize];
image.getPixels(intValues,0, image.getWidth(), 0, 0,image.getWidth(), image.getHeight());
int pixel=0;
for(int i=0;i<imageSize;i++){
    for(int j=0;j<imageSize;j++){
        int val=intValues[pixel++];
        byteBuffer.putFloat(((val>>16)&0xFF)*(1.f/255.f));
        byteBuffer.putFloat(((val>>8)&0xFF)*(1.f/255.f));
        byteBuffer.putFloat((val & 0xFF)*(1.f/255.f));
    }
}
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidencee=outputFeature0.getFloatArray();
            int maxPos=0;
            float maxCofidance=0;
            for(int i=0;i<confidencee.length;i++){
                if(confidencee[i]>maxCofidance){
                    maxCofidance=confidencee[i];
                    maxPos=i;
                }
            }

            String [] classes ={"Fish","Fishtree","Tree"};
            result.setText(classes[maxPos]);

            String s="";
            for(int i=0;i<classes.length;i++){
                s +=String.format("%s: %.1f%%\n", classes[i], confidencee[i] * 100);
            }
            confidence.setText(s);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image=(Bitmap) data.getExtras().get("data");
            int dimension=Math.min(image.getWidth(),image.getHeight());
            image= ThumbnailUtils.extractThumbnail(image,dimension,dimension);
            imageView.setImageBitmap(image);

            image=Bitmap.createScaledBitmap(image,imageSize,imageSize ,false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }*/
