package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 *
 * @author Michelle Brown, Bryan Santos
 * @version 11/17/2018
 */
public class ConnectionListFragment extends Fragment implements SearchView.OnQueryTextListener {

    /**
     * List of existing connections used by mLocalAdapter
     */
    private ArrayList<Connection> mConnections;
    public static final String ARG_CONNECTIONS_LIST = "connections list";

    /** Adapter for existing connections */
    private ConnectionListRecyclerViewAdapter mLocalAdapter;

    /** Adapter used for sending post requests to server to retrieve global connections
     * when user is searching for users that they are not currently connected with*/
    private ConnectionListRecyclerViewAdapter mGlobalAdapter;

    /** Used as a field to change adapters between local and global */
    private RecyclerView mRecyclerView;

    private int mColumnCount = 1;

    private OnListFragmentInteractionListener mListener;

    boolean mExistsLocally;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionListFragment() {
    }

    public static ConnectionListFragment newInstance(int connectionsList) { //TODO: fix
        ConnectionListFragment fragment = new ConnectionListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CONNECTIONS_LIST, connectionsList);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mConnections = (ArrayList) getArguments().getSerializable(ARG_CONNECTIONS_LIST);

        }

    }

    /**
     * Uses menu of parent. Sets search view visible to user. Also sets
     * QueryTextListener to this class.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem search = menu.findItem(R.id.action_search_contacts);
        search.setVisible(true);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connections_list, container, false);
        Log.d(getClass().getSimpleName(), "I am on create view");

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            /*
                set local adapter to be the primary list used so that this list
                is displayed first and so that search is looked through this list first
             */

            mLocalAdapter = new ConnectionListRecyclerViewAdapter(mConnections, mListener);
            mGlobalAdapter = null;
            mRecyclerView.setAdapter(mLocalAdapter);


        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        text = text.toLowerCase();
        mExistsLocally = mLocalAdapter.filter(text);
        if (!mExistsLocally) {

            /*
                since global adapter was previously initialized and that list
                contains the text query, just set adapter to this
             */
            if (mGlobalAdapter != null && mGlobalAdapter.filter(text)) {
                Log.d(getClass().getSimpleName(), "I am setting mGlobalAdapter to be my adapter");
                mRecyclerView.setAdapter(mGlobalAdapter);
            }
            /*
                otherwise, make post request to search through database
                that current user is not connected with
             */
            else {

                Log.d(getClass().getSimpleName(), "I'm sending a post request");
                requestForAllContacts(text);
            }

            /*
                if local adapter contains the textQuery and globalAdapter was previously set,
                reset the adapter to local
             */

        } else if (mExistsLocally && !mRecyclerView.getAdapter().equals(mLocalAdapter)) {

            mRecyclerView.setAdapter(mLocalAdapter);
        }

        Log.d(getClass().getSimpleName(), "current Adapter: " + mRecyclerView.getAdapter());
        Log.d(getClass().getSimpleName(), "is my equals method working:" +
                " check with LocalAdapter: " + mRecyclerView.getAdapter().equals(mLocalAdapter));
        return false;
    }

    /**
     * Helper method to send post request to web service to retrieve all connections
     * from the database based on the text query.
     * @param text search query
     */
    private void requestForAllContacts(String text) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath(getString(R.string.ep_search))
                .appendPath(getString(R.string.ep_contacts))
                .build();

        JSONObject searchAllContacts = new JSONObject();
        try {
            searchAllContacts.put("values", text);
            Log.d(getClass().getSimpleName(), "JSON request: " + searchAllContacts.toString());
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "JSON object creation failed: " + e);
        }

        new SendPostAsyncTask.Builder(uri.toString(), searchAllContacts)
                .onCancelled(this::handleErrorsInTask)
                .onPostExecute(this::handleOnSearchPost)
                .build()
                .execute();
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }


    private void handleOnSearchPost(final String result) {
        Log.d(getClass().getSimpleName(), "I am handling Post Response");
        try {
            JSONObject jsonResults = new JSONObject(result);
            boolean success = jsonResults.getBoolean("success");
            List<Connection> globalConnections;
            JSONArray membersArray;
            if (success) {
                globalConnections = new ArrayList<>();
                membersArray = jsonResults.getJSONArray("members");
                for (int i = 0; i < membersArray.length(); i++) {
                    JSONObject member = membersArray.getJSONObject(i);
                    String firstName = member.getString("firstname");
                    String lastName = member.getString("lastname");
                    String username = member.getString("username");
                    String email = member.getString("email");

                    Connection c;
                    if (mExistsLocally) {
                        c = new Connection.Builder(email, username)
                                .addFirstName(firstName)
                                .addLastName(lastName)
                                .isMine()
                                .build();
                    } else {
                        c = new Connection.Builder(email, username)
                                .addFirstName(firstName)
                                .addLastName(lastName)
                                .build();
                    }
                    globalConnections.add(c);

                }

                mGlobalAdapter = new ConnectionListRecyclerViewAdapter(globalConnections, mListener);
                mRecyclerView.setAdapter(mGlobalAdapter);


                Log.d(getClass().getSimpleName(), "Successfully parsed JSON: " + globalConnections);
            } else {
                Log.d(getClass().getSimpleName(), "Not successful!!!! Fault coming from programmer");
            }
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "Post Response failed to be parsed: " + e);
        }

        Log.d(getClass().getSimpleName(), "I'm done handling post response");
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Connection item);
    }
}
