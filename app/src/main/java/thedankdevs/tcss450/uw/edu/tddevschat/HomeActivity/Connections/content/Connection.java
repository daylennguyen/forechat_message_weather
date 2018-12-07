package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Class to encapsulate a Connection. Building an Object requires a first name, last name and email.
 * <p>
 * Optional fields include nothing for now.
 *
 * @author Michelle Brown
 * @version 11 November 2018
 */
public class Connection implements Serializable {

    /**
     * Information about the Connection, which is really another user of the app
     */
    private final String  mEmail;
    private final String  mUsername;
    private final String  mFirstName;
    private final String  mLastName;
    /**
     * The ID of the one-on-one chat that the current user, and this connection share.
     * will be -1 if uninitialized
     */
    private       int     mChatID;
    /**
     * Boolean representation of if this user is registered as a connection/contact of the other user
     */
    private       boolean mIsMine;
    /**
     * Will be true if the list of connections is empty
     */
    private       boolean mIsEmpty;

    /**
     * Constructs a Connection internally from a builder.
     *
     * @param builder the builder used to construct this object
     */
    private Connection( final Builder builder ) {
        this.mEmail = builder.mEmail;
        this.mUsername = builder.mUsername;
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;
        this.mChatID = builder.mChatID;
        this.mIsMine = builder.mIsMine;
        this.mIsEmpty = builder.mIsEmpty;
    }

    /**
     * Gets the email
     *
     * @return the email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Gets the username
     *
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Gets the first name
     *
     * @return the first name
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Gets the last name
     *
     * @return the last name
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Get the ID of the individual chat that the two users are in
     *
     * @return the ID of the individual chat that the two users are in
     */
    public int getChatID() {
        return mChatID;
    }

    /**
     * Sets the ID of the individual chat that the two users are in
     *
     * @param chatID
     */
    public void setChatID( int chatID ) {
        mChatID = chatID;
    }

    /**
     * Gets the boolean representation of if this user is registered as a connection/contact of the other user
     *
     * @return
     */
    public boolean getIsMine() {
        return mIsMine;
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }

    @NonNull
    @Override
    public String toString() {

        return String.format( "First Name: %s | Last Name: %s | Username: %s | " +
                "Email: %s", this.mFirstName, mLastName, mUsername, mEmail );
    }

    /**
     * Helper class for building Connection.
     *
     * @author Michelle Brown
     * @author Bryan Santos (added mIsEmptyField)
     * @version 12/05/2018
     */
    public static class Builder {

        private final String  mEmail;
        private final String  mUsername;
        private       String  mFirstName = "";
        private       String  mLastName  = "";
        private       int     mChatID    = -1;
        private       boolean mIsMine    = false;
        private       boolean mIsEmpty   = false;


        /**
         * Constructs a new Builder.
         *
         * @param email
         * @param username
         */
        public Builder( String email, String username ) {
            this.mEmail = email;
            this.mUsername = username;
        }

        /**
         * Add an optional last name to the connection.
         *
         * @param val an optional first name for the connection
         * @return the Builder of this Connection
         */
        public Builder addFirstName( final String val ) {
            mFirstName = val;
            return this;
        }

        /**
         * Add an optional last name to the connection.
         *
         * @param val an optional last name for the connection
         * @return the Builder of this Connection
         */
        public Builder addLastName( final String val ) {
            mLastName = val;
            return this;
        }

        /**
         * Add an optional chatID to the connection.
         *
         * @param val an optional chatID for the connection
         * @return the Builder of this Connection
         */
        public Builder addChatID( final int val ) {
            mChatID = val;
            return this;
        }

        /**
         * Changes the value of mIsMine
         * to signify that this member is a verified Connection of this user;
         *
         * @return the Builder of this Connection
         */
        public Builder isMine() {
            mIsMine = true;
            return this;
        }

        /**
         * Signifies that the current connection is
         * an empty one. Used by the Search Functionality
         * to display "No Connections Found"
         *
         * @return the Builder of this Connection
         */
        public Builder isEmpty() {
            mIsEmpty = true;
            return this;
        }

        public Connection build() {
            return new Connection( this );
        }

    }
}
