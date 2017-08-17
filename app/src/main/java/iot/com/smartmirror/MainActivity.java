package iot.com.smartmirror;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.TextureView;
import android.widget.ImageView;

import com.google.android.things.contrib.driver.button.Button;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;

import iot.com.smartmirror.camera.CameraProvider;

import static android.util.Log.d;
import static android.util.Log.e;

/**
 * Main activity.
 * Control key event and life cycle of camera.
 */
public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    // FIXME to collect the GPIO PIN definition.
    private final static String BUTTON_GPIO_PIN = "BCM21";
    private CameraProvider camera;
    private TextureView preview;

    private Button.OnButtonEventListener mButtonCallback = (button, pressed) -> {
        if (pressed) {
            // Note the take photo when enter pressed or GPIO PIN signal receive.
            camera.take();
            d(TAG, "camera has start by GPIO button press event");
        }
    };

    @Override
    /**
     * @Author Ryo Watanabe
     * This method based on Android Things sample library Code.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (OpenCVLoader.initDebug()) {
            d(TAG, "OpenCV run succeeded!");
        } else {
            d(TAG, "OpenCV run failure");
        }
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // A problem occurred auto-granting the permission
            d(TAG, "No permission");
            return;
        } else {
            d(TAG, "permission granted");
        }
        preview = new TextureView(this);
        if (preview.isAvailable()) {
            d(TAG, "preview is available");
            camera = new CameraProvider();
            camera.initialize(this, preview);
        } else {
            preview.setSurfaceTextureListener(this);
        }
        try {
            d(TAG, "GPIO button initialize start");
            Button button = new Button(BUTTON_GPIO_PIN, Button.LogicState.PRESSED_WHEN_LOW);
            button.setOnButtonEventListener(mButtonCallback);
        } catch (IOException e) {
            e(TAG, "button driver error", e);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        // Note the take photo when enter pressed or GPIO PIN signal receive.
        int pressed = e.getKeyCode();
        d(TAG, "this key is pressed -> " + pressed);
        if (KeyEvent.KEYCODE_ENTER == pressed) {
            camera.take();
            d(TAG, "camera has started up by enter ss event.");
            if(camera.photoExists()) {
                d(TAG, "MainActivity receives photo from camera.");
                ImageView photoView = new ImageView(this);
                photoView.setImageBitmap(camera.getPhoto());
                setContentView(photoView);
                d(TAG, "photo display completed");
            }
        }
        if (KeyEvent.KEYCODE_ESCAPE == pressed) {
            camera.shutdown();
            d(TAG, "camera shutdown completed");
        }
        return super.dispatchKeyEvent(e);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        d(TAG, "preview became available now");
        camera = new CameraProvider();
        camera.initialize(this, preview);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        d(TAG, "preview destroyed.");
        camera = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        d(TAG, "preview updated");
    }

    public TextureView getPreview() {
        return preview;
    }

}
