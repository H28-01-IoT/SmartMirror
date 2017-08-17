package iot.com.smartmirror.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.view.TextureView;

import java.nio.ByteBuffer;
import java.util.Collections;

import static android.content.ContentValues.TAG;
import static android.content.Context.CAMERA_SERVICE;
import static android.util.Log.d;
import static android.util.Log.e;
import static android.util.Log.w;

/**
 * Created by Ryo on 2017/08/10.
 * Provides the camera2 api and callback fields.
 */
public class CameraProvider {

    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;
    private static final int MAX_IMAGES = 1;
    private Bitmap photo;
    private CameraDevice cameraDevice;
    private CameraCaptureSession takePhotoSession;
    private ImageReader imageReader = ImageReader.newInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
            ImageFormat.JPEG, MAX_IMAGES);

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            d(TAG, "Opened camera.");
            cameraDevice = camera;
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            d(TAG, "Camera disconnected, closing.");
            camera.close();
        }

        @Override
        public void onError(CameraDevice camera, int i) {
            d(TAG, "Camera device error, closing.");
            camera.close();
        }

        @Override
        public void onClosed(CameraDevice camera) {
            d(TAG, "Closed camera, releasing");
            cameraDevice = null;
        }
    };

    private final CameraCaptureSession.CaptureCallback takePhotoCallback =
            new CameraCaptureSession.CaptureCallback() {

                // This method is called when progressing capture.
                @Override
                public void onCaptureProgressed(CameraCaptureSession session,
                                                CaptureRequest request,
                                                CaptureResult partialResult) {
                    d(TAG, "Partial result of capture progress");
                }

                // This method is called when completed capturing.
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                    // TODO took photo shows mirror.
                    // set content view
                }
            };
    private CameraCaptureSession.StateCallback takePhotoStateCallBack =
            new CameraCaptureSession.StateCallback() {
                /* This method is called when the camera device has finished configuring itself,
                   and the session can start processing capture requests.*/
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    d(TAG, "smart mirror camera configuring start.");
                    // The camera is already closed
                    if (cameraDevice == null) {
                        w(TAG, "camera device is null");
                        return;
                    }
                    // When the session is ready, we start capture.
                    takePhotoSession = cameraCaptureSession;
                    try {
                        final CaptureRequest.Builder takePhotoRequestBuilder =
                                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                        takePhotoRequestBuilder.addTarget(imageReader.getSurface());
                        takePhotoRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                        d(TAG, "Session initialized.");
                        takePhotoSession.capture(takePhotoRequestBuilder.build(), takePhotoCallback, null);
                    } catch (CameraAccessException cae) {
                        e(TAG, "camera access exception : ", cae);
                    }
                    d(TAG, "smart mirror camera configuring end");
                }

                // this method is called when the camera device configuration fails.
                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    w(TAG, "Failed to configure the smart mirror camera");
                }
            };

    /**
     * @param c context which get camera service from system service.
     * @Author Ryo Watanabe
     * initialize camera.
     * create preview session and preview start.
     */
    public void initialize(Context c, TextureView v) {
        d(TAG, "camera initialize start");
        CameraManager manager = (CameraManager) c.getSystemService(CAMERA_SERVICE);
        String[] camIds = {};
        try {
            camIds = manager.getCameraIdList();
            if (camIds.length < 1) {
                d(TAG, "No cameras found");
                return;
            }
            String id = camIds[0];
            d(TAG, "Using camera id " + id);
            // this image reader used when take photo.
            // the handler (which is second )
            imageReader.setOnImageAvailableListener((iReader) -> {
                d(TAG, "photo took");
                Image image = iReader.acquireLatestImage();
                // get image bytes
                ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                final byte[] imageBytes = new byte[imageBuf.remaining()];
                imageBuf.get(imageBytes);
                image.close();
                photo = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, null);
                d(TAG, "photo set on content view");
            }, null);
            if (ActivityCompat.checkSelfPermission(c,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                d(TAG, "camera permission is not granted");
                return;
            }
            // if open camera method receives null handler,
            // callback calls calling thread.
            manager.openCamera(id, stateCallback, null);

            // create request builder for preview.
            d(TAG, "preview setting start.");
            CaptureRequest.Builder previewBuilder
                    = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // set the preview surface of texture has calling class.
            previewBuilder.addTarget(new Surface(v.getSurfaceTexture()));
            previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            takePhotoSession.setRepeatingRequest(previewBuilder.build(), null, null);
            d(TAG, "preview start now");
        } catch (CameraAccessException e) {
            e(TAG, "Exception in camera access", e);
        }
        d(TAG, "camera initialize end");
    }

    /**
     * @Author Ryo Watanabe
     * take photo.
     */
    public void take() {
        d(TAG, "take photo start");
        if (cameraDevice == null) {
            w(TAG, "Cannot capture image. Camera not initialized.");
            return;
        }
        try {
            // Here, we create a CameraCaptureSession for capturing still images.
            // The session is the execution unit of request for camera.
            takePhotoSession.stopRepeating();
            cameraDevice.createCaptureSession(
                    Collections.singletonList(imageReader.getSurface()),
                    takePhotoStateCallBack,
                    null);
        } catch (CameraAccessException cae) {
            e(TAG, "access exception while preparing pic", cae);
        }
    }

    public void shutdown() {
        if (takePhotoSession != null) {
            takePhotoSession.close();
            takePhotoStateCallBack = null;
            d(TAG, "CaptureSession closed");
        }
    }

    public Bitmap getPhoto() {
        return this.photo;
    }
    public boolean photoExists() {
        return this.photo != null ? true : false;
    }
}
