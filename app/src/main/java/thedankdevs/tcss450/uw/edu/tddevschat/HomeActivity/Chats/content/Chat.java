package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to encapsulate a Connection. Building an Object requires a first name, last name and email.
 *
 * Optional fields include nothing for now.
 *
 *
 * @author Michelle Brown
 * @version 11 November 2018
 */
public class Chat implements Serializable {

    private final String mName;
    private final int mChatID;
    private ArrayList<String> mMemberEmails;

    private Chat(String name, int chatID, String firstMemberEmail) {
        this.mName = name;
        this.mChatID = chatID;
        this.mMemberEmails.add(firstMemberEmail);
    }

    public String getName() {
        return mName;
    }

    public int getChatID() {
        return mChatID;
    }

    public ArrayList<String> getMemberEmails() {
        return mMemberEmails;
    }

    public void addMember(String newMemberEmail) {
        mMemberEmails.add(newMemberEmail);
    }
}
