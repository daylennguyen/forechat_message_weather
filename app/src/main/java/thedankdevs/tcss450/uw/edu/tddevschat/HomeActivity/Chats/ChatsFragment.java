package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A fragment representing a list of chats.
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 *
 * @author Emmett Kang
 */
public class ChatsFragment extends Fragment implements View.OnClickListener {
    /**
     * Key to find the array list
     */
    public static final String ARG_CHATS_LIST = "chats list";
    /**
     * Adapter that sets the conent
     */
    MyChatsRecyclerViewAdapter adapter;
    /**
     * Array list that contains all chats
     */
    private ArrayList<Chat>                        mChats;
    private int                                    mColumnCount = 1;
    private OnChatsListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatsFragment() {
    }

    /**
     * new instance of the chats fragment.
     *
     * @param columnCount setting arguments with column count
     * @return chats list fragment
     */
    public static ChatsFragment newInstance( int columnCount ) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle        args     = new Bundle();
        args.putInt( ARG_CHATS_LIST, columnCount );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnChatsListFragmentInteractionListener ) {
            mListener = ( OnChatsListFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnListFragmentInteractionListener" );
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments() != null ) {
            mChats = ( ArrayList ) getArguments().getSerializable( ARG_CHATS_LIST );
        }


    }

    /**
     * Create the view of the fragment itself.
     *
     * @param inflater           view to be inflated
     * @param container          content container
     * @param savedInstanceState any saved information.
     * @return inflated view
     */
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        adapter = new MyChatsRecyclerViewAdapter( mChats, mListener );
        View view = inflater.inflate( R.layout.fragment_chats_list, container, false );
        // Set the adapter
        if ( view instanceof LinearLayout ) {
            Context      context      = view.getContext();
            RecyclerView recyclerView = view.findViewById( R.id.list );
            if ( mColumnCount <= 1 ) {
                recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
            } else {
                recyclerView.setLayoutManager( new GridLayoutManager( context, mColumnCount ) );
            }
            recyclerView.setAdapter( adapter );
        }
        //set on click listener.
        Button b = view.findViewById( R.id.btn_newChat_chats );
        b.setOnClickListener( this );
        return view;
    }

    /**
     * When this fragment resumes, set the title of the activity to Chat.
     */
    @Override
    public void onResume() {
        super.onResume();
        //Set the title to chat.
        Objects.requireNonNull( getActivity() ).setTitle( "Chat" );


        adapter.notifyDataSetChanged();


    }

    /**
     * Detach the fragment from activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * onclick listener
     *
     * @param v button to be set
     */
    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.btn_newChat_chats: //new chat button
                mListener.onCreateNewChatButtonPressed();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnChatsListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChatsListFragmentInteraction( Chat item );

        void onCreateNewChatButtonPressed();

        void onChatsListFragmentLongInteraction( Chat item );
    }
}
