package noonecares.whatever.nmapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import noonecares.whatever.nmapp.ConnectionDetector;
import noonecares.whatever.nmapp.MainActivity;
import noonecares.whatever.nmapp.NMAPPUtil;
import noonecares.whatever.nmapp.NMAPPconstants;
import noonecares.whatever.nmapp.R;
import noonecares.whatever.nmapp.permissionSupport.PermissionSupport;


public class NoConnectionFragment extends Fragment {


    private View view;
    private Button mButton;
    private TextView mTextview;
    private OnFragmentInteractionListener mListener;

    public NoConnectionFragment() {
        // Required empty public constructor
    }

    public static NoConnectionFragment newInstance() {
        NoConnectionFragment fragment = new NoConnectionFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =inflater.inflate(R.layout.fragment_no_connection, container, false);
        mButton = (Button) view.findViewById(R.id.tryAgainButton);
        mTextview = (TextView) view.findViewById(R.id.tryAgainTextView);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.tryAgainButtonPressed();
//                if(ConnectionDetector.getConnectionDetector().isConnectedToInternet(getActivity().getApplicationContext())){
//                    if(PermissionSupport.checkLocationAndStoragePermission(getActivity(), PermissionSupport.LOCATION_AND_STORAGE_PERMISSION_INITIAL)){
//
//                        NMAPPUtil.checkLocationModeAndInflateMap(getActivity(),getActivity(), (MainActivity) getActivity());
//
//                    }
//                }else {
//                    Toast.makeText(getActivity(), NMAPPconstants.STILL_NO_INTERNET, Toast.LENGTH_SHORT);
//                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onConnectionFragmentInteraction(uri);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onConnectionFragmentInteraction(Uri uri);
        void tryAgainButtonPressed();
    }
}
