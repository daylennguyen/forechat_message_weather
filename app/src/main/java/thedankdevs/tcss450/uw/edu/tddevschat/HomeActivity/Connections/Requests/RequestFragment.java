package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests;

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
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import java.util.ArrayList;

/**
 * A fragment representing a list of Requests.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RequestFragment extends Fragment {
    /**
     * Key to find the array list
     */
    public static final String    ARG_REQUESTS_LIST = "chats list";
    /**
     * Array list that contains all connections
     */
    private             ArrayList mRequests;
    private             int       mColumnCount      = 1;

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestFragment() {
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnListFragmentInteractionListener ) {
            mListener = ( OnListFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnListFragmentInteractionListener" );
        }
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments() != null ) {
            mRequests = ( ArrayList ) getArguments().getSerializable( ARG_REQUESTS_LIST );
        }
    }

    /**
     * The fragment instantiates its user interface view
     *
     * @param inflater           view to be inflated
     * @param container          content container
     * @param savedInstanceState any saved information.
     * @return the inflated view
     */
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_request_list, container, false );
        // Set the adapter
        if ( view instanceof RecyclerView ) {
            Context      context      = view.getContext();
            RecyclerView recyclerView = ( RecyclerView ) view;
            if ( mColumnCount <= 1 ) {
                recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
            } else {
                recyclerView.setLayoutManager( new GridLayoutManager( context, mColumnCount ) );
            }
            recyclerView.setAdapter( new MyRequestRecyclerViewAdapter( mRequests, mListener ) );
        }
        return view;
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
    public interface OnListFragmentInteractionListener {
        void onRequestListFragmentInteraction( String theirUsername );
    }
}
