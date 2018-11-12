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
 * Fragment that handles registration via connection with the Heroku Webserver.
 * Also validates user's input so the user can have correct information registered.
 * @author Emmett Kang
 * @version 4 November 2018
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    /** User Credentials to send to the server.*/
    private Credentials mCredentials;

    /** Interaction listener to interact with the activity.*/
    private OnRegisterFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * As soon as the fragment gets created, inflate the fragment
     * and set the click listener.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Button registerButton = (Button) v.findViewById(R.id.btn_register_register);
        registerButton.setOnClickListener(this); //Set click listener for the button.

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }
    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleRegisterOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }
    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        try {
            Log.d("JSON result register", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            mListener.onWaitFragmentInteractionHide();
            if (success) {
                //Register was successful. Inform the Activity so it can do its thing.
                mListener.onRegisterSuccess(mCredentials);

            } else {
                //Register was unsuccessful. Don’t switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.et_register_email))
                        .setError("Register Unsuccessful");
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.et_register_email))
                    .setError("Register Unsuccessful");
        }
    }

    /**
     * Attempts to register by creating credentials and invoking async tasks.
     * Also validates the input values to enforce the expected values for registration.
     * @author Charles Bryan, Emmett Kang
     * @version 4 November 2018
     */
    private void attemptRegister() {
        //Grab all of the fields that are being entered.
        EditText firstName_field = getActivity().findViewById(R.id.et_register_fname);
        EditText lastName_field = getActivity().findViewById(R.id.et_register_lname);

        EditText email_field = getActivity().findViewById(R.id.et_register_email);
        EditText username_field= getActivity().findViewById(R.id.et_register_username);

        EditText password_field = getActivity().findViewById(R.id.et_register_password);
        EditText passConfirm_field = getActivity().findViewById(R.id.et_register_passmatch);

        boolean hasError = false; //Indicator for any of the errors in the EditTexts.
        if (firstName_field.getText().toString().isEmpty()) {
            hasError = true;
            firstName_field.setError("Your first name is empty!");
        } else if (lastName_field.getText().toString().isEmpty()) {
            hasError = true;
            lastName_field.setError("Your last name is empty!");
        } else if (username_field.getText().toString().isEmpty()) {
            hasError = true;
            username_field.setError("Your username is empty!");
        } else if (email_field.getText().toString().isEmpty()) {
            hasError = true;
            email_field.setError("Your username is empty!");
        } else if (password_field.getText().toString().isEmpty()) {
            hasError = true;
            password_field.setError("Your password is empty!");
        } else if (passConfirm_field.getText().toString().isEmpty()) {
            hasError = true;
            passConfirm_field.setError("You haven't entered your password confirmation!");
        } else if (password_field.length() < 6) {
            hasError = true;
            password_field.setError("Your password can't be less than 6 characters!");
        } else if (password_field.getText().toString().compareToIgnoreCase(passConfirm_field.getText().toString()) != 0) {
            hasError = true;
            passConfirm_field.setError("Your password does not match!");
        } else if (!email_field.getText().toString().contains("@")) {
            hasError = true;
            email_field.setError("Your email must be a valid email.");
        }
        if (!hasError) {
            //Build the credentials
            Credentials credentials = new Credentials.Builder(email_field.getText().toString(),
                    password_field.getText().toString())
                    .addUsername(username_field.getText().toString())
                    .addFirstName(firstName_field.getText().toString())
                    .addLastName(lastName_field.getText().toString())
                    .build();


            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.base_url))
                    .appendPath(getString(R.string.ep_register))
                    .build();
            Log.w("URL for Register", uri.toString());
            //build the JSONObject based on credentials
            JSONObject msg = credentials.asJSONObject();

            mCredentials = credentials;

            //instantiate and execute the AsyncTask.
            //Feel free to add a handler for onPreExecution so that a progress bar
            //is displayed or maybe disable buttons.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleRegisterOnPre)
                    .onPostExecute(this::handleRegisterOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.btn_register_register:
                    //if button is a register button from register fragment,
                    attemptRegister(); //Try to register
                    break;
            }
        }

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
    public interface OnRegisterFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener{
        void onRegisterSuccess(Credentials c); //Invoked when register was successful.

    }

}
