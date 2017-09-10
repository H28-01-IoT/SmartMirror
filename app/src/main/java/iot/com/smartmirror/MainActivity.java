package iot.com.smartmirror;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import iot.com.smartmirror.view.fragment.TextureViewFragment;

import static android.util.Log.d;

/**
 * Main activity.
 * Control key event and life cycle of camera.
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextureViewFragment cameraFlagment;
    // FIXME to collect the GPIO PIN definition.
    private final static String BUTTON_GPIO_PIN = "BCM21";

/*
    private Button.OnButtonEventListener mButtonCallback = (button, pressed) -> {
        if (pressed) {
            // Note the take photo when enter pressed or GPIO PIN signal receive.
            camera.take();
            d(TAG, "camera has start by GPIO button press event");
        }
    };
*/

    @Override
    /**
     * @Author Ryo Watanabe
     * This method based on Android Things sample library Code.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraFlagment = TextureViewFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, cameraFlagment)
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int pressed = event.getKeyCode();
        d(TAG, "this key is pressed -> " + pressed);
        if (KeyEvent.KEYCODE_ENTER == pressed) {
            cameraFlagment.enterPressed();
        }
        if (KeyEvent.KEYCODE_ESCAPE == pressed) {
            cameraFlagment.shutdown();
        }
        if (KeyEvent.KEYCODE_SPACE == pressed) {
            cameraFlagment.spacePressed();
        }
        return super.onKeyDown(keyCode, event);
    }
}
