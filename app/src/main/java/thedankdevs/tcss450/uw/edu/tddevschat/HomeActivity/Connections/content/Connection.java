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

    private final String  mEmail;
    private final String  mUsername;
    private final String  mFirstName;
    private final String  mLastName;
    private       int     mChatID;
    private       boolean mIsMine;
    private       boolean mIsEmpty;


    private Connection( final Builder builder ) {
        this.mEmail = builder.mEmail;
        this.mUsername = builder.mUsername;
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;
        this.mChatID = builder.mChatID;
        this.mIsMine = builder.mIsMine;
        this.mIsEmpty = builder.mIsEmpty;
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

    public void setChatID( int chatID ) {
        mChatID = chatID;
    }

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
            return new Connection( this );
        }

    }
}
