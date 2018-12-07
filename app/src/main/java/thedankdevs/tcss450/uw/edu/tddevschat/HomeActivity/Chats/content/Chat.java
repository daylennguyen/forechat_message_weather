package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content;

import java.io.Serializable;

/**
 * Class to encapsulate a Connection. Building an Object requires a first name, last name and email.
 * <p>
 * Optional fields include nothing for now.
 *
 * @author Michelle Brown
 * @version 11 November 2018
 */
public class Chat implements Serializable {

    private final int    mChatID;
    private       String mChatName;
    private       String mChatMembers;

    private Chat( String name, int chatID, String memberUsernames ) {

        this.mChatName = name;
        this.mChatID = chatID;
        this.mChatMembers = memberUsernames;
    }

    private Chat( final Chat.Builder builder ) {
        this.mChatMembers = builder.mMemberusernames;
        this.mChatID = builder.mChatID;
        this.mChatName = builder.mChatName;
    }

    /**
     * Gets the name of the chat
     *
     * @return the name of the chat
     */
    public String getChatName() {
        return mChatName;
    }

    /**
     * Gets the int ID of the chat
     * @return the ID of the chat
     */
    public int getChatID() {
        return mChatID;
    }

    /**
     * Gets the names of the members of this chat
     * @return the names of the members of this chat
     */
    public String getChatMembers() {
        return mChatMembers;
    }

    /**
     * Gets the name of the chat plus a marker to notify the user of an event in the chat.
     */
    public void notifiedChat() {
        this.mChatName = "*" + this.getChatName();
    }

    /**
     * Helper class for building ConnectionListFragment.
     *
     * @author Michelle Brown
     */
    public static class Builder {

        private final int    mChatID;
        private       String mChatName;
        private       String mMemberusernames;


        /**
         * Constructs a new Builder.
         *
         * @param chatName
         * @param userNames email
         * @param chatID
         */
        public Builder( String chatName, String userNames, int chatID ) {

            this.mChatName = chatName;
            this.mMemberusernames = userNames;
            this.mChatID = chatID;
        }

        public Chat build() {
            return new Chat( this );
        }

    }

}
