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
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.d(TAG, "Opened camera.");
            cameraDevice = camera;
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.d(TAG, "Camera disconnected, closing.");
            camera.close();
        }

        @Override
        public void onError(CameraDevice camera, int i) {
            Log.d(TAG, "Camera device error, closing.");
            camera.close();
        }

        @Override
        public void onClosed(CameraDevice camera) {
            Log.d(TAG, "Closed camera, releasing");
            cameraDevice = null;
        }
    };
    private CameraCaptureSession captureSession;
    private final CameraCaptureSession.CaptureCallback captureCallback =
            new CameraCaptureSession.CaptureCallback() {

                // This method is called when progressing capture.
                @Override
                public void onCaptureProgressed(CameraCaptureSession session,
                                                CaptureRequest request,
                                                CaptureResult partialResult) {
                    Log.d(TAG, "Partial result of capture progress");
                }

                // This method is called when completed capturing.
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
                /* This method is called when the camera device has finished configuring itself,
                   and the session can start processing capture requests.*/
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    // The camera is already closed
                    if (cameraDevice == null) {
                        Log.w(TAG, "camera device is null");
                        return;
                    }
                    // When the session is ready, we start capture.
                    captureSession = cameraCaptureSession;
                    triggerImageCapture();
                }

                // this method is called when the camera device configuration fails.
                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Log.w(TAG, "Failed to configure camera");
                }
            };

    private SmartMirrorCamera() {
    }

    private static SmartMirrorCamera self;

    public static SmartMirrorCamera getInstance() {
        if (self == null) {
            self = new SmartMirrorCamera();
            Log.d(TAG, "The first camera instantiate");
        }
        Log.d(TAG, "camera instantiate");
        return self;
    }

    /**
     *
     * @param c context which get camera service from system service.
     * @param h Listener handler what passes for image available listener.
     * @param l It is Listener when runs image available.
     */
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
        try {
            manager.openCamera(id, stateCallback, h);
        } catch (CameraAccessException e) {
            Log.e(TAG,"Exception in camera access", e);
        }
    }

    /**
     * @Author Ryo Watanabe
     * take picture.
     * this method processes only create session.
     */
    public void takePicture() {
        if (cameraDevice == null) {
            Log.w(TAG, "Cannot capture image. Camera not initialized.");
            return;
        }

        /* Here, we create a CameraCaptureSession for capturing still images.
           The session is the execution unit of request for camera.
           */
        try {
            cameraDevice.createCaptureSession(
                    Collections.singletonList(imageReader.getSurface()),
                    mSessionCallback,
                    null);
        } catch (CameraAccessException cae) {
            Log.e(TAG, "access exception while preparing pic" , cae);
        }
    }

    /**
     * @Author Ryo Watanabe
     * This method summarize the process regarding camera.
     */
    private void triggerImageCapture() {
        try {
            final CaptureRequest.Builder captureBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            Log.d(TAG, "Session initialized.");
            captureSession.capture(captureBuilder.build(), captureCallback, null);
        } catch (CameraAccessException cae) {
            Log.e(TAG, "camera access exception : " , cae);
        }
    }
}
