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

    private final String mChatName;
    private final int mChatID;
    private ArrayList<String> mMemberEmails;

    private Chat(String name, int chatID, String firstMemberEmail) {
        mMemberEmails = new ArrayList<>();
        this.mChatName = name;
        this.mChatID = chatID;
        this.mMemberEmails.add(firstMemberEmail);
    }

    /**
     * Helper class for building ConnectionsFragment.
     *
     * @author Michelle Brown
     */
    public static class Builder {

        private final String mChatName;
        private final int mChatID;
        private ArrayList<String> mMemberEmails;


        /**
         * Constructs a new Builder.
         *
         * @param chatName
         * @param firstMemberEmail email
         * @param chatID
         */
        public Builder(String chatName, String firstMemberEmail, int chatID) {
            mMemberEmails = new ArrayList<>();
            this.mChatName = chatName;
            this.mMemberEmails.add(firstMemberEmail);
            this.mChatID = chatID;
        }



        /*
         * Add an optional something to the connection.
         * @param val an optional something for the connection
         * @return the Builder of this Connection
         */
/*        public Builder addSomething(final String val) {
            mSomething = val;
            return this;
        }*/

        public Chat build() {
            return new Chat(this);
        }

    }

    private Chat(final Chat.Builder builder) {
        this.mMemberEmails = builder.mMemberEmails;
        this.mChatID = builder.mChatID;
        this.mChatName = builder.mChatName;
    }


    public String getChatName() {
        return mChatName;
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
