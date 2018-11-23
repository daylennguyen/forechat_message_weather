package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import thedankdevs.tcss450.uw.edu.tddevschat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateNewChatFragment extends Fragment {


    public CreateNewChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_chat, container, false);
    }

}
