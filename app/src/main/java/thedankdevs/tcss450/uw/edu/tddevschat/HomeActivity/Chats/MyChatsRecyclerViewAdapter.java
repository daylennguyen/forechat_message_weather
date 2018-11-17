package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;


import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatsFragment.OnChatsListFragmentInteractionListener;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Chat} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class MyChatsRecyclerViewAdapter extends RecyclerView.Adapter<MyChatsRecyclerViewAdapter.ViewHolder> {

    private final List<Chat> mValues;
    private final OnChatsListFragmentInteractionListener mListener;

    public MyChatsRecyclerViewAdapter(List<Chat> items, ChatsFragment.OnChatsListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mChatName.setText(mValues.get(position).getChatName());
        holder.mReceiver.setText(mValues.get(position).getMemberEmails().toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onChatsListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mChatName;
        public final TextView mReceiver;
        public Chat mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mChatName = view.findViewById(R.id.tv_chatslist_chatName);
            mReceiver = view.findViewById(R.id.tv_chatslist_chatReceiver);

        }
    }
}
