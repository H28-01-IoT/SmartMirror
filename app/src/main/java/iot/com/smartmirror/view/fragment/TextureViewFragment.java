package iot.com.smartmirror.view.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;

import iot.com.smartmirror.R;
import iot.com.smartmirror.camera.CameraProvider;

import static android.util.Log.d;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TextureViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TextureViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextureViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CameraProvider camera;
    private static TextureView preview;

    private static final String TAG = TextureViewFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public TextureViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextureViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TextureViewFragment newInstance() {
        TextureViewFragment fragment = new TextureViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d(TAG, "fragment oncreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        d(TAG, "view creation");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_texture_view, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        d(TAG, "onAttach has called");
        super.onAttach(context);
/*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
*/
    }

    @Override
    public void onDetach() {
        d(TAG, "onDetach has called");
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        d(TAG, "onViewCreated has called.");
        preview = getActivity().findViewById(R.id.preview);
        preview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                d(TAG, "preview became available now");
                camera.initialize(getContext());
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
        });
        camera = new CameraProvider();
        if (OpenCVLoader.initDebug()) {
            d(TAG, "OpenCV run succeeded!");
        } else {
            d(TAG, "OpenCV run failure");
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // A problem occurred auto-granting the permission
            d(TAG, "No permission");
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.CAMERA
            }, 1);
        } else {
            d(TAG, "permission granted");
        }
        camera = new CameraProvider();
        d(TAG, String.valueOf(preview.isAttachedToWindow()));
        if (preview.isAvailable()) {
            camera.initialize(getActivity());
            d(TAG, "preview is available");
        } else {
            d(TAG, "preview is not available");
        }
/*
        try {
            d(TAG, "GPIO button initialize start");
            Button button = new Button(BUTTON_GPIO_PIN, Button.LogicState.PRESSED_WHEN_LOW);
            button.setOnButtonEventListener(mButtonCallback);
        } catch (IOException e) {
            e(TAG, "button driver error", e);
        }
*/
        camera.initialize(getContext());
        super.onViewCreated(view,savedInstanceState);
    }

    public static TextureView getPreview() {
        return preview;
    }

    public void enterPressed() {
        camera.take(getContext());
        d(TAG, "camera has started up by enter ss event.");
        if(camera.photoExists()) {
            d(TAG, "MainActivity receives photo from camera.");
            ImageView photoView = new ImageView(getActivity());
            photoView.setImageBitmap(camera.getPhoto());
            getActivity().setContentView(photoView);
            d(TAG, "photo display completed");
        }
    }

    public void spacePressed() {
        d(TAG, String.valueOf(preview.isAvailable()));
        camera.initialize(getContext());
    }

    public void shutdown() {
        camera.shutdown();
        d(TAG, "camera shutdown completed");
    }
}
