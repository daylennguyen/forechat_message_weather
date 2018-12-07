package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests.RequestFragment.OnListFragmentInteractionListener;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests.content.Request;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Request} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestRecyclerViewAdapter.ViewHolder> {

    /**
     * The list of connection requests (both send and received)*/
    private final List<Request>                     mRequests;

    private final OnListFragmentInteractionListener mListener;

    /**
     * The constructor
     *
     * @param items the list of requests
     * @param listener the class that will respond when something happens here
     */
    public MyRequestRecyclerViewAdapter( List<Request> items, OnListFragmentInteractionListener listener ) {
        mRequests = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.fragment_request, parent, false );
        return new ViewHolder( view );
    }

    /**
     * Called to display data at the specified position.
     *
     * @param holder represents the contents of the item at the given position in the data set
     * @param position position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder( final ViewHolder holder, int position ) {
        holder.mItem = mRequests.get( position );
        holder.mUsernameView.setText( mRequests.get( position ).getUsername() );
        try {
            if ( !( holder.mItem.getIReceived() ) ) {
                holder.mAcceptButton.setEnabled( false );
                holder.mAcceptButton.setBackgroundColor( Color.WHITE );
                holder.mAcceptButton.setText( R.string.connection_pendingrequest );
                holder.mUsernameView.setTextColor( Color.GRAY );
            }
        } catch ( Exception e ) {
            Log.e( "REQUEST VIEW HOLDER", "my iReceived value is weird!! " + e );
        }
    }

    /**
     * @return the number of requests in the list
     */
    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    /**
     * Describes a Request item view and data about its place in the RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View     mView;
        public final TextView mUsernameView;
        public final Button   mAcceptButton;
        public       Request  mItem;

        public ViewHolder( View view ) {
            super( view );
            mView = view;
            mUsernameView = view.findViewById( R.id.tv_conectionRequest_username );
            mAcceptButton = view.findViewById( R.id.btn_connectionRequest_accept );
            //if (mItem == null) Log.e("REQUEST VIEWHOLDER - ERROR MICHELLE", "mItem should not be null!");
            //if (mItem != null && mItem.getIReceived()) {
            mAcceptButton.setOnClickListener( this );
            //}
            //} else {
            //mAcceptButton.setEnabled(false);
            //mAcceptButton.setText("Pending");
            // mAcceptButton.setTextColor(Color.BLUE);
            //}
        }

        /**
         * When the user clicks the button, we make the listener respond to the user accepting the connection
         * @param v
         */
        @Override
        public void onClick( View v ) {
            mAcceptButton.setEnabled( false );
            mAcceptButton.setText( R.string.connection_acceptedrequest );
            //mAcceptButton.setBackgroundColor(mMaster.getResources().getColor(R.color.colorBluePurple));
            mAcceptButton.setTextColor( Color.BLUE );
            mListener.onRequestListFragmentInteraction( mItem.getUsername() );
        }
    }
}
