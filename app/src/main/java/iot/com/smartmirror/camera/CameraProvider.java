package iot.com.smartmirror.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


import iot.com.smartmirror.fragment.view.PreviewFragment;

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
    private CaptureRequest.Builder previewBuilder;
    private CaptureRequest previewRequest;
    private ImageReader imageReader = ImageReader.newInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
            ImageFormat.JPEG, MAX_IMAGES);
    private boolean isCameraAvailable = false;

    /**
     * @Author Ryo Watanabe
     * initialize camera.
     * create preview session and preview start.
     */
    public void initialize(Context c) {
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
            manager.openCamera(id,
                    new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(CameraDevice camera) {
                            d(TAG, "Opened camera.");
                            cameraDevice = camera;
                            isCameraAvailable = true;
                            try {
                                previewBuilder
                                        = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                // set the preview surface of texture has calling class.
                                // preview setting start.
                                SurfaceTexture texture = PreviewFragment.getPreview().getSurfaceTexture();
                                List<Surface> surfaceList = new ArrayList<>();
                                if(PreviewFragment.getPreview().isAvailable()) {
                                    Surface surface = new Surface(texture);
                                    surfaceList.add(surface);
                                    previewBuilder.addTarget(surface);
                                } else {
                                    previewBuilder.addTarget(imageReader.getSurface());
                                }
                                surfaceList.add(imageReader.getSurface());
                                cameraDevice.createCaptureSession(surfaceList,
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
                                                    previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                                                    previewRequest = previewBuilder.build();
                                                    takePhotoSession.setRepeatingRequest(
                                                            previewRequest,
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
                                                                }
                                                            },
                                                            null);
                                                    d(TAG, "Session initialized.");
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
                                        },
                                        null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

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
                    },
                    new Handler(Looper.getMainLooper()));
            // create request builder for preview.
        } catch (CameraAccessException e) {
            e(TAG, "Exception in camera access", e);
        }
        d(TAG, "camera initialize end");
    }

    /**
     * @Author Ryo Watanabe
     * take photo.
     */
    public void take(Context c) {
        d(TAG, "take photo start");
        if (cameraDevice == null) {
            w(TAG, "Cannot capture image. Camera not initialized.");
            return;
        }
        try {
            takePhotoSession.stopRepeating();
            CaptureRequest.Builder captureBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            takePhotoSession.capture(
                    previewBuilder.build(),
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
                    },
                    null);
        } catch (CameraAccessException cae) {
            e(TAG, "access exception while preparing pic", cae);
        }
    }

    public void shutdown() {
        if (takePhotoSession != null) {
            takePhotoSession.close();
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
