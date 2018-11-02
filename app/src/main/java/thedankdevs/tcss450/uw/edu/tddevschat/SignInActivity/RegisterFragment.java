package thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import thedankdevs.tcss450.uw.edu.tddevschat.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Button b = (Button) v.findViewById(R.id.btn_register_register);
        b.setOnClickListener(this);
        return v;
    }

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

    private void attemptRegister() {
        //Grab all of the fields that are being entered.
        EditText firstName_field = getActivity().findViewById(R.id.et_register_fname);
        EditText lastName_field = getActivity().findViewById(R.id.et_register_lname);

        EditText email_field = getActivity().findViewById(R.id.et_register_email);
        EditText nickname_field= getActivity().findViewById(R.id.et_register_nickname);

        EditText password_field = getActivity().findViewById(R.id.et_register_password);
        EditText passConfirm_field = getActivity().findViewById(R.id.et_register_passmatch);


        boolean hasError = false; //Indicator for any of the erros in the EditTexts.
        if (firstName_field.getText().toString().isEmpty()) {
            hasError = true;
            firstName_field.setError("Your first name is empty!");
        } else if (lastName_field.getText().toString().isEmpty()) {
            hasError = true;
            lastName_field.setError("Your last name is empty!");
        } else if (nickname_field.getText().toString().isEmpty()) {
            hasError = true;
            nickname_field.setError("Your username is empty!");
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
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        //Yet to have a webserver, so pending. For now, it does simple validation.
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.btn_register_register:
                    attemptRegister();
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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
