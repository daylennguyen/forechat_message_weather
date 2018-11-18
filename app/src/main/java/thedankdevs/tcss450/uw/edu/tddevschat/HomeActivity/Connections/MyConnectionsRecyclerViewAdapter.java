package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionsFragment.OnListFragmentInteractionListener;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Connection} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 *
 * @author Michelle Brown
 */
public class MyConnectionsRecyclerViewAdapter extends RecyclerView.Adapter<MyConnectionsRecyclerViewAdapter.ViewHolder> {

    private List<Connection> mConnections;
    private final List<Connection> mCopyConnections;
    private final OnListFragmentInteractionListener mListener;

    public MyConnectionsRecyclerViewAdapter(List<Connection> items, OnListFragmentInteractionListener listener) {
        mConnections = items;
        mListener = listener;
        mCopyConnections = new ArrayList<>();
        mCopyConnections.addAll(mConnections);

        Log.d("CONNECTION", "mConnections: " + mConnections);
        Log.d("CONNECTION", "mCopyConnections: " + mCopyConnections);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_connections, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = (mConnections).get(position);
        holder.mUsername.setText(mConnections.get(position).getUsername());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mConnections.size();
    }

    public boolean filter(String text) {
        mConnections.clear();
        if (text.isEmpty()) {
            mConnections.addAll(mCopyConnections);
            return true;
        }
        text = text.toLowerCase();
        for (Connection c : mCopyConnections) {
            if (containsText(c, text)) {
                mConnections.add(c);
            }
        }

        notifyDataSetChanged();

        // if this returns false, should make post request to server
        return !mConnections.isEmpty();
    }

    private boolean containsText(Connection c, String text) {
        return c.getFirstName().toLowerCase().contains(text) ||
                c.getLastName().toLowerCase().contains(text) ||
                c.getUsername().toLowerCase().contains(text) ||
                c.getEmail().toLowerCase().contains(text);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mUsername;
        public Connection mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUsername = view.findViewById(R.id.tv_connectionslist_username);
        }

    }
}
