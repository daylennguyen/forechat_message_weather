package thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Michelle Brown
 * @version 26 October 2018
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters
    private static final String ARG_CREDENTIALS = "cred_param";

    private static final String TAG = LoginFragment.class.getSimpleName();

    private String mParam1;
    private String mParam2;
    private Credentials mCredentials;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param credentials contains the email and password.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(Credentials credentials) { //TODO: should we include this method?
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CREDENTIALS, credentials);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_EMAIL); //TODO: should the credentials be gotten here?
            //mParam2 = getArguments().getString(ARG_PASSWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button b = (Button) v.findViewById(R.id.btn_login_login);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.btn_login_register);
        b.setOnClickListener(this);
        return v;
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



    @Override
    public void onClick(View viewClicked) {
        if (mListener != null) {
            EditText email_field = getActivity().findViewById(R.id.et_login_email);
            EditText password_field = getActivity().findViewById(R.id.et_login_password);

            String email = email_field.getText().toString();
            String password = password_field.getText().toString();

            Credentials.Builder credBuilder;
            Credentials credentials;
/*
            switch (viewClicked.getId()) {
                case R.id.btn_login_login:
                    if (!email.equals("") && !password.equals("") && email.contains("@")) {
                        //build credentials
                        credBuilder = new Credentials.Builder(email, password);
                        credentials = credBuilder.build();
                        //build the web service URL
                        Uri uri = new Uri.Builder()
                                .scheme("https")
                                .appendPath(getString(R.string.ep_base_url))
                                .appendPath(getString(R.string.ep_login))
                                .build();
                        //build the JSONObject
                        JSONObject msg = credentials.asJSONObject();
                        mCredentials = credentials;
                        //instantiate and execute the AsyncTask
                        new SendPostAsyncTask.Builder(uri.toString(), msg)
                                .onPreExecute(this::handleLoginOnPre)
                                .onPostExecute(this::handleLoginOnPost)
                                .onCancelled(this::handleErrorsInTask)
                                .build().execute();
//                        mListener.onLoginSuccess(credentials);
                    } else {
                        if (email.equals(""))
                            email_field.setError(getString(R.string.loginRegister_missingField));
                        if (password.equals(""))
                            password_field.setError(getString(R.string.loginRegister_missingField));
                        if (!email.contains("@"))
                            email_field.setError(getString(R.string.loginRegister_invalidEmail));
                        Log.d(TAG, "one of the fields was invalid");
                    }
                    break;
                case R.id.btn_login_register:
                    mListener.onRegisterClicked();
                    break;
                default:
                    Log.wtf(TAG, "was something weird clicked?");
            }*/
        }

    }



    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
        //TODO: disable buttons
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            mListener.onWaitFragmentInteractionHide();
            if (success) {
                //Login was successful. Inform the Activity so it can do its thing.
                mListener.onLoginSuccess(mCredentials);
            } else {
                //Login was unsuccessful. Don’t switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.et_login_email))
                        .setError("Login Unsuccessful");
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.et_login_email))
                    .setError("Login Unsuccessful");
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onLoginSuccess(Credentials credentials);
        void onRegisterClicked();
    }
}
