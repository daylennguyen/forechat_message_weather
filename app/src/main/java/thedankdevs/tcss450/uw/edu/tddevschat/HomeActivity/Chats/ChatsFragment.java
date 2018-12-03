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
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class ChatsFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_CHATS_LIST = "chats list";
    MyChatsRecyclerViewAdapter adapter;
    private ArrayList<Chat>                        mChats;
    private int mColumnCount = 1;
    private OnChatsListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatsFragment() {
    }


    public static ChatsFragment newInstance( int columnCount ) { //TODO: fix
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
        Button b = view.findViewById( R.id.btn_newChat_chats );
        b.setOnClickListener( this );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull( getActivity() ).setTitle( "Chat" );


        adapter.notifyDataSetChanged();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.btn_newChat_chats:
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
