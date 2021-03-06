package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionListFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import java.util.ArrayList;

/**
 * Fragment that gives the users to select from their connections and
 * create a new chat. The connection list is loaded from homeactivity.
 *
 * @Author Emmett Kang
 * @Version 27 November 2018
 */
public class CreateNewChatFragment extends Fragment implements View.OnClickListener {
    /**
     * Checkbox list for connection's usernames
     */
    ArrayList<CheckBox>   checkBoxList;
    /**
     * List of connections
     */
    ArrayList<Connection> connectionList;
    private OnCreateNewChatButtonListener mListener;
    /**
     * Chat room title
     */
    private EditText                      mChatTitleEditText;

    public CreateNewChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick( View v ) {
        if ( mListener != null ) {
            switch ( v.getId() ) {
                case R.id.btn_create_new_chat: //When button create new chat is clicked
                    String ChatTitle = mChatTitleEditText.getText().toString();
                    mListener.CreateNewChatInteraction( checkBoxList, connectionList, ChatTitle );
                    break;
            }
        }
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof ConnectionFragment.OnConnectionFragmentInteractionListener ) {
            mListener = ( CreateNewChatFragment.OnCreateNewChatButtonListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    /**
     * Set the view of the fragment.
     *
     * @param inflater           view to be inflated
     * @param container          container of the contents
     * @param savedInstanceState any saved information
     * @return inflated view.
     */
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View v = inflater.inflate( R.layout.fragment_create_new_chat, container, false );

        if ( getArguments() != null ) {

            LinearLayout cbContainter = v.findViewById( R.id.CheckBoxContainer );
            checkBoxList = new ArrayList<>();
            connectionList = ( ArrayList<Connection> ) getArguments().getSerializable( ConnectionListFragment.ARG_CONNECTIONS_LIST );

            //For each connections the user have, make a checkbox for each connection.
            for ( int i = 0; i < connectionList.size(); i++ ) {
                CheckBox checkBox = new CheckBox( getContext() );
                checkBox.setTextSize( 20 );
                checkBox.setText( connectionList.get( i ).getUsername() );
                checkBox.setFontFeatureSettings( String.valueOf( R.font.roboto ) );
                cbContainter.addView( checkBox );
                checkBoxList.add( checkBox );
            }

        }


        mChatTitleEditText = v.findViewById( R.id.et_chatroom_title );
        Button createNewChatButton = v.findViewById( R.id.btn_create_new_chat );
        createNewChatButton.setOnClickListener( this );
        return v;
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
     */
    public interface OnCreateNewChatButtonListener {
        void CreateNewChatInteraction( ArrayList<CheckBox> cbList, ArrayList<Connection> connectionList, String ChatTitle );
    }
}
