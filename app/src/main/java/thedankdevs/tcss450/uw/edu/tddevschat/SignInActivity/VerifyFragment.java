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
 */
public class VerifyFragment extends Fragment implements View.OnClickListener{


    /** Interaction listener to interact with the activity.*/
    private OnVerifyFragmentInteractionListener mListener;

    public VerifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_verify, container, false);
        Button registerButton = (Button) v.findViewById(R.id.btn_verify_verify);
        registerButton.setOnClickListener(this); //Set click listener for the button.

       return v;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.btn_verify_verify:
                    attemptVerify(); //Try to verify
                    break;
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterFragment.OnRegisterFragmentInteractionListener) {
            mListener = (VerifyFragment.OnVerifyFragmentInteractionListener) context;
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
    private void handleVerifyOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }
    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleVerifyOnPost(String result) {
        try {

            Log.d("JSON result verify: ", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            mListener.onWaitFragmentInteractionHide();
            if (success) {
                //Register was successful. Inform the Activity so it can do its thing.
                  mListener.onVerificationSuccess();
            } else {
                //Register was unsuccessful. Don’t switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.et_verify_code))
                        .setError("Verification Unsuccessful");
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.et_verify_code))
                    .setError("Verification Unsuccessful");
        }
    }


    private void attemptVerify() {
        EditText verificationCode_field = getActivity().findViewById(R.id.et_verify_code);
        boolean hasError = false; //Indicator for any of the errors in the EditTexts.
        if (verificationCode_field.getText().toString().isEmpty()) {
            hasError = true;
            verificationCode_field.setError("Verification code is empty!");
        }
        if (!hasError) {
            JSONObject msg = new JSONObject();
            try {
                msg.put("vericode", verificationCode_field.getText().toString());
            } catch (JSONException e) {
                Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
            }
            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.base_url))
                    .appendPath(getString((R.string.ep_verify)))
                    .build();

            Log.w("URL for Verify", uri.toString());

            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleVerifyOnPre)
                    .onPostExecute(this::handleVerifyOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

    public interface OnVerifyFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener{
        void onVerificationSuccess();
    }


}
