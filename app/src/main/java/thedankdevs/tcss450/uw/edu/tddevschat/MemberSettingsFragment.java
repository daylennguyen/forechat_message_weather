package thedankdevs.tcss450.uw.edu.tddevschat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemberSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemberSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEMBER_SETTINGS = "member_settings";
    private static final String[] KEYS = {"firstname", "lastname", "username", "email", "password"};
    private static final String CHANGE_BTN_TEXT = "Change";
    private static final String UNDO_BTN_TEXT = "Undo";
    private final int DISABLED_TEXT_COLOR = Color.WHITE;
    private int ENABLED_TEXT_COLOR;
    private Drawable ENABLED_ET_COLOR;
    private Drawable DISABLED_ET_COLOR;
    private final String TAG = getClass().getSimpleName();
    private Credentials mCredentials;
    private OnFragmentInteractionListener mListener;
    private Map<Integer, UpdateValue> mCredentialsMap;
    private GridLayout mGridLayout;
    private int mEnabledChangeButtons;
    private Button mApplyButton;
    private EditText mPassword_et;
    private EditText mConfirmPassword_et;
    private TextView mConfirmPassword_tv;

    private View mView;

    public MemberSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MemberSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberSettingsFragment newInstance(Credentials credentials) {
        MemberSettingsFragment fragment = new MemberSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable( ARG_MEMBER_SETTINGS, credentials );
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCredentialsMap = new HashMap<>();
        if (getArguments() != null) {
            mCredentials = (Credentials) getArguments().getSerializable(getString(R.string.nav_membersettings));
            UpdateValue firstname = new UpdateValue(KEYS[0], mCredentials.getFirstName());
            UpdateValue lastname = new UpdateValue(KEYS[1], mCredentials.getLastName());
            UpdateValue username = new UpdateValue(KEYS[2], mCredentials.getUsername());
            UpdateValue email = new UpdateValue(KEYS[3], mCredentials.getEmail());
            UpdateValue password = new UpdateValue(KEYS[4], mCredentials.getPassword());

            mCredentialsMap.put(R.id.et_member_settings_firstname, firstname);
            mCredentialsMap.put(R.id.et_member_settings_lastname, lastname);
            mCredentialsMap.put(R.id.et_member_settings_username, username);
            mCredentialsMap.put(R.id.et_member_settings_email, email);
            mCredentialsMap.put(R.id.et_member_settings_password, password);

            Log.d(TAG, "Finished initializing mCredentialsMap: " + mCredentialsMap);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        // create ContextThemeWrapper from the original Activity Context with the custom theme
//        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Material);
//
//        // clone the inflater using the ContextThemeWrapper
//        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);



        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_member_settings, container, false);

        mGridLayout = mView.findViewById(R.id.gl_member_settings);
        mPassword_et = mView.findViewById(R.id.et_member_settings_password);
        mConfirmPassword_et = mView.findViewById(R.id.et_member_settings_confirm_password);
        mConfirmPassword_tv = mView.findViewById(R.id.tv_member_settings_confirmpass);
        mApplyButton = mView.findViewById(R.id.btn_member_settings_apply);

        ENABLED_TEXT_COLOR = getContext().getColor(R.color.colorPrimary);
        ENABLED_ET_COLOR = getContext().getDrawable(R.drawable.member_settings_btn_enabled);
        DISABLED_ET_COLOR = getContext().getDrawable(R.drawable.member_settings_btn_disabled);
        Log.d("BRYAN", "my tag: "+ getTag());
        setDefault();

        return mView;
    }


    // helper methods to setting the default values and state of the layout
    private void setDefault() {
        Context context = getContext();
        mEnabledChangeButtons = 0;
//        displayETIds();
        for (int i = 0; i < mGridLayout.getChildCount(); i++) {
            View childView = mGridLayout.getChildAt(i);
            int id = childView.getId();
            if (childView instanceof EditText) {
                EditText et = (EditText) childView;
                if (id == R.id.et_member_settings_confirm_password) {
//                    et.setBackground(ENABLED_ET_COLOR);
//                    et.setTextColor(ENABLED_TEXT_COLOR);
                    et.setVisibility(View.GONE);
                    mConfirmPassword_tv.setVisibility(View.GONE);
                    continue;
                }

                UpdateValue updateValue = mCredentialsMap.get(id);
                String value = updateValue.getValue();
                et.setText(value);
//                et.setBackground(DISABLED_ET_COLOR);
//                et.setTextColor(DISABLED_TEXT_COLOR);
                et.setEnabled(false);

            } else if(childView instanceof Button) {
                Button btn = (Button) childView;
                if (id == R.id.btn_member_settings_cancel) {
                    btn.setOnClickListener(view -> onCancelButtonClick(childView));
                } else if (id == R.id.btn_member_settings_apply) {
                    btn.setOnClickListener(view -> onApplyButtonClick(childView));
                    btn.setEnabled(false);
                } else {
                    btn.setOnClickListener(view -> onChangeButtonClick(childView));
                    btn.setText(CHANGE_BTN_TEXT);
                    setButtonTags(btn);
                }

            }

        }
        mPassword_et.setTransformationMethod(PasswordTransformationMethod.getInstance());

    }
    private void setButtonTags(Button btn) {

        EditText et;
        int id = btn.getId();
        switch (id) {
            case R.id.btn_member_settings_firstname:
                et = mView.findViewById(R.id.et_member_settings_firstname);
                break;
            case R.id.btn_member_settings_lastname:
                et = mView.findViewById(R.id.et_member_settings_lastname);
                break;
            case R.id.btn_member_settings_username:
                et = mView.findViewById(R.id.et_member_settings_username);
                break;
            // remove email actions until email endpoint is implemented
//            case R.id.btn_member_settings_email:
//                et = mView.findViewById(R.id.et_member_settings_email);
//                break;
            case R.id.btn_member_settings_password:
                Log.d(TAG, "I am password button");
                et = mView.findViewById(R.id.et_member_settings_password);
                break;
            default:
                Log.e(TAG, "Unknown button clicked");
                throw new IllegalArgumentException();
        }

        btn.setTag(et);
    }

    // onClick Event handlers
    public void onCancelButtonClick(View v) {
        if (mEnabledChangeButtons == 0) {
            return;
        } else if (mEnabledChangeButtons < 0) {
            Log.d(TAG, "mEnabledChangeButtons < 0? = " + mEnabledChangeButtons);
        }

        Context context = getContext();
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Cancel Update")
                .setMessage("Are you sure you want to undo all your changes?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setDefault();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_cancel_black_24dp)
                .show();

    }
    public void onChangeButtonClick(View v) {
        Button btnClicked = (Button) v;
        EditText et;
        if (v.getTag() instanceof EditText) {
            et = (EditText) v.getTag();
        } else {
            Log.e(TAG, "unknown tag");
            throw new IllegalArgumentException();
        }
        int id = et.getId();
        if (id != R.id.et_member_settings_password) {
            flipTextFields(et, btnClicked);
            Log.d(TAG, "I flipped non-password fields");
        } else {
            flipPasswordFields(mPassword_et, mConfirmPassword_et, btnClicked);
            Log.d(TAG, "I flipped password fields");
        }

        // check apply button state
        if (mEnabledChangeButtons > 0 && !mApplyButton.isEnabled()) {
            mApplyButton.setEnabled(true);
        } else if (mEnabledChangeButtons == 0 && mApplyButton.isEnabled()) {
            mApplyButton.setEnabled(false);
        }


    }
    public void onApplyButtonClick(View v) {

        if (!areFieldsValid()) {
            return;
        }

        // check if password fields are enabled and if they're matching
        if (mPassword_et.isEnabled()) {
            String pass = mPassword_et.getText().toString();
            String confirm_pass = mConfirmPassword_et.getText().toString();
            if (!arePasswordsValid(pass, confirm_pass)) {
                // do nothing
                return;
            }
        }

        Context context = getContext();
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Update Information")
                .setMessage("Are you sure you want to update the fields selected?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        attachListener();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_settings_thumb_up_black_24dp)
                .show();

    }

    // helper methods for when the change button is clicked
    private void flipTextFields(EditText et, Button btn) {

        int key = et.getId();
        if (!et.isEnabled()) {
            et.setEnabled(true);
//            et.setBackground(ENABLED_ET_COLOR);
//            et.setTextColor(ENABLED_TEXT_COLOR);
            btn.setText(UNDO_BTN_TEXT);
            mEnabledChangeButtons += 1;
        } else {
            et.setEnabled(false);
            et.setText(mCredentialsMap.get(key).getValue());
//            et.setBackground(DISABLED_ET_COLOR);
//            et.setTextColor(DISABLED_TEXT_COLOR);
            btn.setText(CHANGE_BTN_TEXT);
            mEnabledChangeButtons -= 1;
        }

        et.setError(null);
    }
    private void flipPasswordFields(EditText pass_et, EditText confirmpass_et, Button btn) {
        int key = pass_et.getId();

        if (!pass_et.isEnabled()) {
            Log.d(TAG, "I am enabling et");
            pass_et.setEnabled(true);
//            pass_et.setBackground(ENABLED_ET_COLOR);
//            pass_et.setTextColor(ENABLED_TEXT_COLOR);
            confirmpass_et.setVisibility(View.VISIBLE);
            mConfirmPassword_tv.setVisibility(View.VISIBLE);
            btn.setText(UNDO_BTN_TEXT);

            pass_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirmpass_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEnabledChangeButtons += 1;
        } else {
            Log.e(TAG, "I am disabling et");
            pass_et.setEnabled(false);
            pass_et.setText(mCredentialsMap.get(key).getValue());
//            pass_et.setBackground(DISABLED_ET_COLOR);
//            pass_et.setTextColor(DISABLED_TEXT_COLOR);
            confirmpass_et.setVisibility(View.GONE);
            mConfirmPassword_tv.setVisibility(View.GONE);
            btn.setText(CHANGE_BTN_TEXT);
            pass_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEnabledChangeButtons -= 1;
        }

        // clear error
        pass_et.setError(null);

    }

    // helper methods to check the validity of the enabled fields before making post request
    private boolean areFieldsValid() {
        boolean valid = true;

        for (int i = 0; i < mGridLayout.getChildCount(); i++) {
            View childView = mGridLayout.getChildAt(i);

            if (childView instanceof EditText) {
                EditText et = (EditText) childView;
                int type = et.getInputType();
                int id = et.getId();
                if (et.isEnabled() && id != R.id.et_member_settings_confirm_password) {
                    String newValue = et.getText().toString();
                    String oldValue = mCredentialsMap.get(id).getValue();
                    if (newValue.length() < 3) {
                        et.setError("Field must contain at least 3 characters");
                        valid = false;
                    }

                    else if(oldValue.equalsIgnoreCase(newValue)) {
                        et.setError("The new value cannot match the existing value");
                        valid = false;
                    }
                }
            }
        }



        return valid;
    }
    private boolean arePasswordsValid(String pass, String confirm_pass) {
        boolean valid = true;

        // check if any of the password fields are less than 6 characters
        EditText[] et_arr = {mPassword_et, mConfirmPassword_et};
        for (EditText et : et_arr) {
            String et_string = et.getText().toString();
            if (et_string.length() < 6) {
                et.setError("Passwords must be at least 6 characters long");
                valid = false;
            }
        }

        // check for equality (case sensitive)
        if (valid && !pass.equals(confirm_pass)) {
            mConfirmPassword_et.setError("Passwords must match");
            valid = false;
        }

        return valid;
    }
    private void attachListener() {

        Map<String, String> updateMap = new HashMap<>();
        for (int i = 0; i < mGridLayout.getChildCount(); i++) {
            View childView = mGridLayout.getChildAt(i);
            if (childView instanceof EditText) {
                EditText et = (EditText) childView;
                int id = et.getId();
                if (et.isEnabled() && id != R.id.et_member_settings_confirm_password) {
                    UpdateValue obj = mCredentialsMap.get(id);
                    updateMap.put(obj.getKey(), et.getText().toString());
                    Log.d(TAG, "Added object to updateMap: " + obj.getKey() + "=" + et.getText().toString());

                }


            }
        }

        if (updateMap.isEmpty()) {
            Log.d(TAG, "Why is my update map empty?");
        }
        Log.d(TAG, "I'm sending this map: " + updateMap);
        mListener.onChangeMemberInfo(updateMap);
        Log.d(TAG, "mListener attached");
    }

    // dialog boxes for when the update is successful and unsuccessful
    public void successfulUpdateDialog() {
        Context context = getContext();
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        builder.setTitle("Succesful Update")
                .setMessage("Information has been successfully updated")
                .setNeutralButton("OK",
                        (DialogInterface dialog, int which) -> { dialog.dismiss(); })
                .setIcon(R.drawable.ic_check_black_24dp)
                .show();


    }
    public void unSuccessfulUpdateDialog() {
        Context context = getContext();
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        builder.setTitle("Unsuccesful Update")
                .setMessage("Information update failed")
                .setNeutralButton("OK",
                        (DialogInterface dialog, int which) -> { dialog.dismiss(); })
                .setIcon(R.drawable.ic_cancel_black_24dp)
                .show();

        setDefault();
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

        void onChangeMemberInfo(Map<String, String> info);
    }

    // helper class for storing the default values
    // of each EditText field (excluding confirm_password)
    private class UpdateValue {

        private String mKey;
        private String mValue;

        public UpdateValue(String key, String value) {
            mKey = key;
            mValue = value;
            if (key == null) {
                mKey = "";
            }

            if (value == null) {
                mValue = "";
            }

        }

        public String getKey() { return mKey; }
        public String getValue() { return mValue; }

        @Override
        public String toString() {
            String formatted = String.format("{%s=%s}", mKey, mValue);
            return formatted;
        }


    }

}
