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

    private final List<Request>                     mRequests;
    private final OnListFragmentInteractionListener mListener;

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

    @Override
    public void onBindViewHolder( final ViewHolder holder, int position ) {
        holder.mItem = mRequests.get( position );
        holder.mUsernameView.setText( mRequests.get( position ).getUsername() );
        try {
            if ( !( holder.mItem.getIReceived() ) ) {
                holder.mAcceptButton.setEnabled( false );
                holder.mAcceptButton.setBackgroundColor( Color.WHITE );
                holder.mAcceptButton.setText( "Pending" );
                holder.mUsernameView.setTextColor( Color.GRAY );
            }
        } catch ( Exception e ) {
            Log.e( "REQUEST VIEW HOLDER", "my iReceived value is weird!! " + e );
        }
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

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

        @Override
        public void onClick( View v ) {
            mAcceptButton.setEnabled( false );
            mAcceptButton.setText( "Accepted!" );
            //mAcceptButton.setBackgroundColor(mMaster.getResources().getColor(R.color.colorBluePurple));
            mAcceptButton.setTextColor( Color.BLUE );
            mListener.onRequestListFragmentInteraction( mItem.getUsername() );
        }
    }
}
