package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

import thedankdevs.tcss450.uw.edu.tddevschat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemoveChatMembers extends Fragment implements View.OnClickListener {

    private OnRemoveMemberListener mListener;
    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<String> usersToRemove;
    private int mChatID;
    public RemoveChatMembers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remove_chat_members, container, false);

        if (getArguments() != null) {
            Log.wtf("EMMETT", "checkbox list is loaded");
            usersToRemove = new ArrayList<>();
            LinearLayout cbContainter = v.findViewById(R.id.remove_cb_container);
            checkBoxes = (ArrayList<CheckBox>) getArguments().getSerializable("ArrayList");
            mChatID = (int) getArguments().getSerializable("chatID");
            for (CheckBox cb : checkBoxes ) {
                cbContainter.addView(cb);
                Log.w("Running", cb.getText().toString());

            }
        }
        Button removeMemberButton = v.findViewById(R.id.btn_remove_chatMem);
        removeMemberButton.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.btn_remove_chatMem:
                    for (CheckBox cb : checkBoxes ) {
                        if (cb.isChecked()) {
                            Log.wtf("out", cb.getText().toString());
                            usersToRemove.add(cb.getText().toString());
                        }
                    }
                    mListener.RemoveMemberInteraction(usersToRemove, mChatID);
                    break;
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RemoveChatMembers.OnRemoveMemberListener) {
            mListener = (RemoveChatMembers.OnRemoveMemberListener) context;
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

    public interface OnRemoveMemberListener {
        void RemoveMemberInteraction(ArrayList<String> usersToRemove, int mChatID);
    }
}
