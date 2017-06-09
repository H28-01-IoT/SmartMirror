package iot.com.smartmirror;

import android.media.Image;
import android.media.ImageReader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.content.pm.PackageManager;
import android.Manifest;
import android.util.Log;
import android.os.Handler;
import android.os.HandlerThread;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.things.contrib.driver.button.Button;

import java.nio.ByteBuffer;
import java.io.IOException;

import iot.com.smartmirror.camera.SmartMirrorCamera;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler cameraHandler;
    private HandlerThread cameraHandlerThread;
    private Button button;
    private final String BUTTON_GPIO_PIN = "BCM21";
    private SmartMirrorCamera camera = SmartMirrorCamera.getInstance();
    private Bitmap photo;
    private ImageView showPictView;

    private Button.OnButtonEventListener mButtonCallback = new Button.OnButtonEventListener() {
        @Override

        public void onButtonEvent(Button button, boolean pressed) {
            if (pressed) {
                // Doorbell rang!
                Log.d(TAG, "button pressed");
                camera.takePicture();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // A problem occurred auto-granting the permission
            Log.d(TAG, "No permission");
            return;
        }

        //TODO take photo
        cameraHandlerThread = new HandlerThread("CameraBackground");
        cameraHandlerThread.start();
        cameraHandler = new Handler(cameraHandlerThread.getLooper());
        camera.initCamera(this, cameraHandler, new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();
                // get image bytes
                ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                final byte[] imageBytes = new byte[imageBuf.remaining()];
                imageBuf.get(imageBytes);
                image.close();
                photo = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, null);
                showPictView.setImageBitmap(photo);
                setContentView(showPictView);
            }

        });
        try {
            button = new Button(BUTTON_GPIO_PIN, Button.LogicState.PRESSED_WHEN_LOW);
            button.setOnButtonEventListener(mButtonCallback);
            showPictView = new ImageView(this);
        } catch (IOException e) {
            Log.e(TAG, "button driver error", e);
        }
    }
}
