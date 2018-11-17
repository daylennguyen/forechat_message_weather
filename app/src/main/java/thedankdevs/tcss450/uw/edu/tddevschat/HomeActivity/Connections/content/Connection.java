package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content;

import java.io.Serializable;

/**
 * Class to encapsulate a Connection. Building an Object requires a first name, last name and email.
 *
 * Optional fields include nothing for now.
 *
 *
 * @author Michelle Brown
 * @version 11 November 2018
 */
public class Connection implements Serializable {

    private final String mEmail;
    private final String mUsername;
    private final String mFirstName;
    private final String mLastName;
    private final int mChatID;


    /**
     * Helper class for building ConnectionListFragment.
     *
     * @author Michelle Brown
     */
    public static class Builder {

        private final String mEmail;
        private final String mUsername;
        private String mFirstName = "";
        private String mLastName = "";
        private int mChatID = -1;


        /**
         * Constructs a new Builder.
         *
         * @param email
         * @param username
         */
        public Builder(String email, String username) {
            this.mEmail = email;
            this.mUsername = username;
        }

        /**
         * Add an optional last name to the connection.
         * @param val an optional first name for the connection
         * @return the Builder of this Connection
         */
        public Builder addFirstName(final String val) {
            mFirstName = val;
            return this;
        }

        /**
         * Add an optional last name to the connection.
         * @param val an optional last name for the connection
         * @return the Builder of this Connection
         */
        public Builder addLastName(final String val) {
            mLastName = val;
            return this;
        }

        /**
         * Add an optional chatID to the connection.
         * @param val an optional chatID for the connection
         * @return the Builder of this Connection
         */
        public Builder addChatID(final int val) {
            mChatID = val;
            return this;
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

        public Connection build() {
            return new Connection(this);
        }

    }

    private Connection(final Builder builder) {
        this.mEmail = builder.mEmail;
        this.mUsername = builder.mUsername;
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;
        this.mChatID = builder.mChatID;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public int getChatID() {
        return mChatID;
    }

    @Override
    public String toString() {
        String formatted = String.format("First Name: %s | Last Name: %s | Username: %s | " +
                "Email: %s", this.mFirstName, mLastName, mUsername, mEmail);

        return formatted;
    }
}
