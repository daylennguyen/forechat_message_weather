package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests.content;

import java.io.Serializable;

/**
 * Class to encapsulate a Connection Request. Building an Object requires a username
 * and a boolean value representing if the user received this request (true) or sent it (false).
 * <p>
 * Optional fields include nothing for now.
 *
 * @author Michelle Brown
 * @version 24 November 2018
 */
public class Request implements Serializable {

    private final String  mUsername;
    private       boolean mIReceived;


    /**
     * Constructs a Request internally from a builder.
     *
     * @param builder the builder used to construct this object
     */
    private Request( final Builder builder ) {
        this.mUsername = builder.mUsername;
        this.mIReceived = builder.mIReceived;
    }

    /**
     * Get the Username.
     *
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Get the boolean value representing if the user received this request (true) or sent it (false).
     *
     * @return mIReceived
     */
    public boolean getIReceived() {
        return mIReceived;
    }

    @Override
    public String toString() {
        String formatted = String.format( "Username: %s" + mUsername );
        return formatted;
    }

    /**
     * Helper class for building a Request.
     *
     * @author Michelle Brown
     */
    public static class Builder {

        private final String  mUsername;
        private       boolean mIReceived = false;


        /**
         * Constructs a new Builder.
         *
         * @param username
         */
        public Builder( String username, boolean iReceived ) {
            this.mUsername = username;
            this.mIReceived = iReceived;
        }

        public Request build() {
            return new Request( this );
        }

    }
}
