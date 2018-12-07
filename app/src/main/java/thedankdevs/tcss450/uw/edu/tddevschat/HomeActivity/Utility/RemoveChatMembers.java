package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import java.util.ArrayList;

/**
 * This fragment shows the members to be deleted and gives the option for
 * the user to submit the chat members to be deleted.
 * @author Emmett Kang
 */
public class RemoveChatMembers extends Fragment implements View.OnClickListener {

    private OnRemoveMemberListener mListener;
    private ArrayList<CheckBox>    checkBoxes;
    private ArrayList<String>      usersToRemove;
    private int                    mChatID;

    public RemoveChatMembers() {
        // Required empty public constructor
    }

    @Override
    public void onClick( View v ) {
        if ( mListener != null ) {
            switch ( v.getId() ) {
                case R.id.btn_remove_chatMem:
                    for ( CheckBox cb : checkBoxes ) {
                        if ( cb.isChecked() ) { // if check box is checked
                            //Add to the list to be removed.
                            usersToRemove.add( cb.getText().toString() );
                        }
                    }
                    mListener.RemoveMemberInteraction( usersToRemove, mChatID );
                    break;
            }
        }
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof RemoveChatMembers.OnRemoveMemberListener ) {
            mListener = ( RemoveChatMembers.OnRemoveMemberListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View v = inflater.inflate( R.layout.fragment_remove_chat_members, container, false );

        if ( getArguments() != null ) {
            //collections of usernames to be removed from the group chat.
            usersToRemove = new ArrayList<>();
            LinearLayout cbContainter = v.findViewById( R.id.remove_cb_container );
            //Grab the arrarylist of checkBoxes
            checkBoxes = ( ArrayList<CheckBox> ) getArguments().getSerializable( "ArrayList" );
            mChatID = ( int ) getArguments().getSerializable( "chatID" );

            //Add the checkboxes of username to the view.
            for ( CheckBox cb : checkBoxes ) {
                cbContainter.addView( cb );
            }
        }
        Button removeMemberButton = v.findViewById( R.id.btn_remove_chatMem );
        removeMemberButton.setOnClickListener( this );


        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnRemoveMemberListener {
        void RemoveMemberInteraction( ArrayList<String> usersToRemove, int mChatID );
    }
}
