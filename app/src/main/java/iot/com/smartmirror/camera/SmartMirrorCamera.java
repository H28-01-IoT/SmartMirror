package iot.com.smartmirror.camera;

/**
 * Created by watanabe on 2017/06/09.
 */

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;

import java.util.Collections;

import static android.content.Context.CAMERA_SERVICE;

public class SmartMirrorCamera {
    private static final String TAG = SmartMirrorCamera.class.getSimpleName();

    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;
    private static final int MAX_IMAGES = 1;
    private ImageReader imageReader;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private final CameraCaptureSession.CaptureCallback captureCallback =
            new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureProgressed(CameraCaptureSession session,
                                                CaptureRequest request,
                                                CaptureResult partialResult) {
                    Log.d(TAG, "Partial result");
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                    if (session != null) {
                        session.close();
                        captureSession = null;
                        Log.d(TAG, "CaptureSession closed");
                    }
                }
            };

    private CameraCaptureSession.StateCallback mSessionCallback =
            new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    // The camera is already closed
                    if (cameraDevice == null) {
                        return;
                    }

                    // When the session is ready, we start capture.
                    captureSession = cameraCaptureSession;
                    triggerImageCapture();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Log.w(TAG, "Failed to configure camera");
                }
            };


    private SmartMirrorCamera() {}
    private static SmartMirrorCamera self;
    public static SmartMirrorCamera getInstance() {
        if(self == null) {
            self = new SmartMirrorCamera();
        }
        return self;
    }

    public void initCamera(Context c, Handler h, ImageReader.OnImageAvailableListener l) {
        CameraManager manager = (CameraManager) c.getSystemService(CAMERA_SERVICE);
        String[] camIds = {};
        try {
            camIds = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            Log.d(TAG, "Cam access exception getting IDs", e);
        }
        if (camIds.length < 1) {
            Log.d(TAG, "No cameras found");
            return;
        }
        String id = camIds[0];
        Log.d(TAG, "Using camera id " + id);
        imageReader = ImageReader.newInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
                ImageFormat.JPEG, MAX_IMAGES);
        imageReader.setOnImageAvailableListener(l, h);

    }

    public void takePicture() {
        if (cameraDevice == null) {
            Log.w(TAG, "Cannot capture image. Camera not initialized.");
            return;
        }

        // Here, we create a CameraCaptureSession for capturing still images.
        try {
            cameraDevice.createCaptureSession(
                    Collections.singletonList(imageReader.getSurface()),
                    mSessionCallback,
                    null);
        } catch (CameraAccessException cae) {
            Log.d(TAG, "access exception while preparing pic", cae);
        }
    }
    private void triggerImageCapture() {
        try {
            final CaptureRequest.Builder captureBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            Log.d(TAG, "Session initialized.");
            captureSession.capture(captureBuilder.build(), captureCallback, null);
        } catch (CameraAccessException cae) {
            Log.d(TAG, "camera capture exception");
        }
    }
}
