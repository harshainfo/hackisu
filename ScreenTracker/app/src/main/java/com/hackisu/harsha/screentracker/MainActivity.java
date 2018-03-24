package com.hackisu.harsha.screentracker;

import android.content.Intent;
import android.graphics.Bitmap;
//import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageSource;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final EditText etSearchTxt = (EditText) findViewById(R.id.txtSearchTxt);
        final EditText etInterval = (EditText) findViewById(R.id.txtInterval);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            String stSearchTxt = etSearchTxt.getText().toString();
            String stInterval = etInterval.getText().toString();

            takeScreenshot();

        }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void takeScreenshot() {
        //Date now = new Date();
        //android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            //String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);




            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

            final byte[] photoData = IOUtils.toByteArray(bs);
            bs.close();

            final Image inputImage = new Image();

            Vision.Builder visionBuilder = new Vision.Builder(
                    new NetHttpTransport(),
                    new AndroidJsonFactory(),
                    null);

            visionBuilder.setVisionRequestInitializer(
                    new VisionRequestInitializer("AIzaSyAsMNc2hrC4daSngrpjdORCe1T1HUE78TM"));

            Vision vision = visionBuilder.build();

            // Create new thread
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    inputImage.encodeContent(photoData);

                    Feature desiredFeature = new Feature();
                    desiredFeature.setType("TEXT_DETECTION");

                    // More code here
                }
            });
    /*
            AnnotateImageRequest request = new AnnotateImageRequest();
            request.setImage(inputImage);
            request.setFeatures(Arrays.asList(desiredFeature));

            BatchAnnotateImagesRequest batchRequest =
                    new BatchAnnotateImagesRequest();

            batchRequest.setRequests(Arrays.asList(request));

            BatchAnnotateImagesResponse batchResponse =
                    vision.images().annotate(batchRequest).execute();

            final TextAnnotation text = batchResponse.getResponses()
                    .get(0).getFullTextAnnotation();

            Toast.makeText(getApplicationContext(),
                    text.getText(), Toast.LENGTH_LONG).show();
            /*try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                List<AnnotateImageResponse> responses = response.getResponsesList();

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        out.printf("Error: %s\n", res.getError().getMessage());
                        return;
                    }

                    // For full list of available annotations, see http://g.co/cloud/vision/docs
                    for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                        out.printf("Text: %s\n", annotation.getDescription());
                        out.printf("Position : %s\n", annotation.getBoundingPoly());
                    }
                }
            }


            /*File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
            */
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
    /*
    public static void detectTextGcs(String gcsPath, PrintStream out) throws Exception, IOException {

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyAsMNc2hrC4daSngrpjdORCe1T1HUE78TM"));

        Vision vision = visionBuilder.build();

        // Create new thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Convert photo to byte array
                InputStream inputStream =
                        getResources().openRawResource(R.raw.photo);
                byte[] photoData = IOUtils.toByteArray(inputStream);
                inputStream.close();

                Image inputImage = new Image();
                inputImage.encodeContent(photoData);

                // More code here
            }
        });

        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    out.printf("Text: %s\n", annotation.getDescription());
                    out.printf("Position : %s\n", annotation.getBoundingPoly());
                }
            }
        }
    }
    */
}
