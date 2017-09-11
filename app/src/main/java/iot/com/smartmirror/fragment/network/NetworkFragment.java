package iot.com.smartmirror.fragment.network;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import iot.com.smartmirror.R;
import iot.com.smartmirror.network.ImageParseAPI;
import iot.com.smartmirror.network.json.cloudvision.CloudVisionResponce;
import iot.com.smartmirror.network.json.cloudvision.Response;
import retrofit.RestAdapter;

import static android.util.Log.d;
import static android.util.Log.e;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NetworkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NetworkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetworkFragment extends Fragment {
    private static final String URL_BASE = "";
    private OnFragmentInteractionListener mListener;

    private static final String TAG = NetworkFragment.class.getSimpleName();
    private static final String id = "";
    private static final String appId = "";
    private static final String restFileName = "";


    public NetworkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NetworkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NetworkFragment newInstance() {
        NetworkFragment fragment = new NetworkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // access web api on other thread.
        new RestAdapter
                .Builder()
                .setEndpoint("http://")
                .build()
                .create(ImageParseAPI.class)
                .getParsedInformation(restFileName, id, appId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responce -> {
                            List<Response> resList = responce
                                    .getResponses()
                                    .stream()
                                    .filter(each -> each != null)
                                    .collect(Collectors.toList());
                            long rescnt = resList.size();
                            d(TAG, "response count is : " + rescnt);
                            if(rescnt < 1) {
                                e(TAG, "no response");
                            } else {
                                // FIXME: 2017/09/11 get the face grid and return square.
                            }
                        },
                        onerr -> {
                            e(TAG, "some network error occur.");
                            e(TAG, onerr.getMessage());
                        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
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


}
