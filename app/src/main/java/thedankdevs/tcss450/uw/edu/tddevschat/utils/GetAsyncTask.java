package thedankdevs.tcss450.uw.edu.tddevschat.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Implemented AsyncTask that makes a Get call to a web service.  Builds the Task
 * requiring a fully formed URL.
 *
 * Optional parameters include actions for onPreExecute, onProgressUpdate, onPostExecute, and
 * onCancelled.
 *
 * An action for onProgressUpdate is included but a call to publishProgress is never made in
 * doInBackground rendering onProgressUpdate unused.
 *
 * The method cancel() is called in doInBackGround during exception handling. Use the action
 * onCnCancelled to respond to exceptional situations resulting from doInBackground execution.
 * Note that external cancellation will cause the same action to execute.
 *
 * Created by Charles Bryan on 3/22/2018.
 *
 * @author Charles Bryan
 * @version 1 OCT 2018
 */
public class GetAsyncTask extends AsyncTask<Void, String, String> {

    private final String mUrl;

    private Runnable mOnPre;
    private Consumer<String[]> mOnProgress;
    private Consumer<String> mOnPost;
    private Consumer<String> mOnCancel;

    /**
     * Helper class for building PostAsyncTasks.
     *
     * @author Charles Bryan
     */
    public static class Builder {

        //Required Parameters
        private final String mUrl;

        //Optional Parameters
        private Runnable onPre = () -> {};
        private Consumer<String[]> onProg = X -> {};
        private Consumer<String> onPost = x -> {};
        private Consumer<String> onCancel = x -> {};

        /**
         * Constructs a new Builder.
         *
         * @param url the fully-formed url of the web service this task will connect to
         */
        public Builder(final String url) {
            mUrl = url;
        }

        /**
         * Set the action to perform during AsyncTask onPreExecute.
         *
         * @param val a action to perform during AsyncTask onPreExecute
         * @return
         */
        public Builder onPreExecute(final Runnable val) {
            onPre = val;
            return this;
        }

        /**
         * Set the action to perform during AsyncTask onProgressUpdate. An action for
         * onProgressUpdate is included but a call to publishProgress is never made in
         * doInBackground rendering onProgressUpdate unused.
         *
         * @param val a action to perform during AsyncTask onProgressUpdate
         * @return
         */
        public Builder onProgressUpdate(final Consumer<String[]> val) {
            onProg = val;
            return this;
        }

        /**
         * Set the action to perform during AsyncTask onPostExecute.
         *
         * @param val a action to perform during AsyncTask onPostExecute
         * @return
         */
        public Builder onPostExecute(final Consumer<String> val) {
            onPost = val;
            return this;
        }

        /**
         * Set the action to perform during AsyncTask onCancelled. The AsyncTask method cancel() is
         * called in doInBackGround during exception handling. Use this action to respond to
         * exceptional situations resulting from doInBackground execution. Note that external
         * cancellation will cause this action to execute.
         *
         * @param val a action to perform during AsyncTask onCancelled
         * @return
         */
        public Builder onCancelled(final Consumer<String> val) {
            onCancel = val;
            return this;
        }

        /**
         * Constructs a SendPostAsyncTask with the current attributes.
         *
         * @return a SendPostAsyncTask with the current attributes
         */
        public GetAsyncTask build() {
            return new GetAsyncTask(this);
        }

    }

    /**
     * Construct a SendPostAsyncTask internally from a builder.
     *
     * @param builder the builder used to construct this object
     */
    private GetAsyncTask(final Builder builder) {
        mUrl = builder.mUrl;

        mOnPre = builder.onPre;
        mOnProgress = builder.onProg;
        mOnPost = builder.onPost;
        mOnCancel = builder.onCancel;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mOnPre.run();
    }

    @Override
    protected String doInBackground(Void... voids) {

        StringBuilder response = new StringBuilder();
        HttpURLConnection urlConnection = null;

        try {
            URL urlObject = new URL(mUrl);
            urlConnection = (HttpURLConnection) urlObject.openConnection();

            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while((s = buffer.readLine()) != null) {
                response.append(s);
            }
            publishProgress();
        } catch (Exception e) {
            response = new StringBuilder("Unable to connect, Reason: "
                    + e.getMessage());
            cancel(true);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response.toString();
    }

    @Override
    protected void onCancelled(String result) {
        super.onCancelled(result);
        mOnCancel.accept(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mOnProgress.accept(values);

    }

    @Override
    protected void onPostExecute(String result) {
        mOnPost.accept(result);
    }
}

