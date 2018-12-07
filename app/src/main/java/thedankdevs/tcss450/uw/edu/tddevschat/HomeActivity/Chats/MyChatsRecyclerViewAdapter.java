package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatsFragment.OnChatsListFragmentInteractionListener;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import java.util.List;

/**
 * This recyclerview adapter sets the content of the chats list fragment.
 *
 * @author Emmett Kang
 */
public class MyChatsRecyclerViewAdapter extends RecyclerView.Adapter<MyChatsRecyclerViewAdapter.ViewHolder> {

    private final List<Chat>                             mValues;
    private final OnChatsListFragmentInteractionListener mListener;

    /**
     * constructor for the view adapter.
     *
     * @param items    chats list.
     * @param listener interaction listener between the activity.
     */
    MyChatsRecyclerViewAdapter( List<Chat> items, ChatsFragment.OnChatsListFragmentInteractionListener listener ) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.fragment_chats, parent, false );
        return new ViewHolder( view );
    }

    /**
     * biding the view holder, setting the contents accordingly with the layout
     *
     * @param holder   content holder
     * @param position where the content will be placed.
     */
    @Override
    public void onBindViewHolder( @NonNull final ViewHolder holder, int position ) {
        holder.mItem = mValues.get( position );
        holder.mChatName.setText( mValues.get( position ).getChatName() );
        holder.mReceiver.setText( mValues.get( position ).getChatMembers() );

        holder.mView.setOnClickListener( v -> {
            if ( null != mListener ) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onChatsListFragmentInteraction( holder.mItem );
            }
        } );
        holder.mView.setOnLongClickListener( v -> {
            if ( null != mListener ) {
                mListener.onChatsListFragmentLongInteraction( holder.mItem );
            }
            return true;
        } );
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Describes a Chat item view and data about its place in the RecyclerView
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        final View     mView;
        final TextView mChatName;
        final TextView mReceiver;
        Chat mItem;


        ViewHolder( View view ) {
            super( view );
            mView = view;
            mChatName = view.findViewById( R.id.tv_chatslist_chatName );
            mReceiver = view.findViewById( R.id.tv_chatslist_chatReceiver );

        }
    }
}
