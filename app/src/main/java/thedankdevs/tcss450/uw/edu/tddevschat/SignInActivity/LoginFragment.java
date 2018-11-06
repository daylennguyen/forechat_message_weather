package thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity;

import android.content.Context;
import android.content.SharedPreferences;
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
 *
 *
 * @author Michelle Brown
 * @version 26 October 2018
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters
    private static final String TAG = LoginFragment.class.getSimpleName();
    private Credentials mCredentials;
    private OnFragmentInteractionListener mListener;
    private View mView;

    public LoginFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param credentials contains the email and password.
//     * @return A new instance of fragment LoginFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static LoginFragment newInstance(Credentials credentials) { //TODO: should we include this method?
//        LoginFragment fragment = new LoginFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_CREDENTIALS, credentials);
//        fragment.setArguments(args);
//        return fragment;
//    }
//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_login, container, false);

        Button b = (Button) mView.findViewById(R.id.btn_login_login);
        b.setOnClickListener(this);
        b = (Button) mView.findViewById(R.id.btn_login_register);
        b.setOnClickListener(this);
        return mView;
    }



    @Override
    public void onClick(View viewClicked) {

        switch (viewClicked.getId()) {
            case R.id.btn_login_register:
                mListener.onRegisterClicked();
                break;
            case R.id.btn_login_login:
                if (!isLoginValid()) {
                    break;
                }
                buildLoginServerCredentials();
                // mListener is attached in handleLoginOnPost();
                break;
            default:
                Log.e(TAG, "Error when button is clicked in Login Fragment");
        }
    }

    private boolean isLoginValid() {

        EditText email_field = getActivity().findViewById(R.id.et_login_email);
        EditText password_field = getActivity().findViewById(R.id.et_login_password);

        String email = email_field.getText().toString();
        String password = password_field.getText().toString();

        // if both fields are empty, display error on both fields
        if (email.length() == 0 && password.length() == 0) {
            email_field.setError("Please enter your email");
            password_field.setError("Please enter your password");
            return false;
        }

        // set errors individually on fields that are invalid or empty
        if (email.length() == 0) {
            email_field.setError("Please enter your email");
            return false;
        }

        if (password.length() == 0) {
            password_field.setError("Please enter your password");
            return false;
        }

        if (!isEmailValid(email)) {
            email_field.setError("Email must contain a single '@' symbol");
            return false;
        }

        return true;
    }

    private boolean isEmailValid(String email) {
        int count = 0; // character '@' count;
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                count++;
            }
        }

        if (count == 1) return true;
        return false;
    }

    private boolean buildLoginServerCredentials() {
        EditText email_field = getActivity().findViewById(R.id.et_login_email);
        EditText password_field = getActivity().findViewById(R.id.et_login_password);

        String email = email_field.getText().toString();
        String password = password_field.getText().toString();

        //build the web service URL
        mCredentials = new Credentials.Builder(email, password).build();
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString(R.string.ep_login))
                .build();

        //build the JSONObject
        JSONObject msg = mCredentials.asJSONObject();

        //instantiate and execute the AsyncTask
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleLoginOnPre)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build()
                .execute();

        return true;


    }



    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {
            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.et_login_email);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.et_login_password);
            passwordEdit.setText(password);
            buildLoginServerCredentials();
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
                saveCredentials(mCredentials);
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



    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
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
