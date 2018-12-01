package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests.content;

import java.io.Serializable;

/**
 * Class to encapsulate a Connection Request. Building an Object requires a username
 * and a boolean value representing if the user received this request (true) or sent it (false).
 *
 * Optional fields include nothing for now.
 *
 *
 * @author Michelle Brown
 * @version 24 November 2018
 */
public class Request implements Serializable {

    private final String mUsername;
    private boolean mIReceived;


    /**
     * Helper class for building a Request.
     *
     * @author Michelle Brown
     */
    public static class Builder {

        private final String mUsername;
        private boolean mIReceived = false;


        /**
             * Constructs a new Builder.
             *
             * @param username
             */
        public Builder(String username, boolean iReceived) {
                this.mUsername = username;
                this.mIReceived = iReceived;
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

        public Request build() {
            return new Request(this);
        }

    }

    private Request(final Builder builder) {
        this.mUsername = builder.mUsername;
        this.mIReceived = builder.mIReceived;
    }

    public String getUsername() {
        return mUsername;
    }

    public boolean getIReceived() {
        return mIReceived;
    }

    @Override
    public String toString() {
        String formatted = String.format("Username: %s" + mUsername);
        return formatted;
    }
}
