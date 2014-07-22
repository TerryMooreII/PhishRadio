package com.terrymoreii.phishradio.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.terrymoreii.phishradio.DAO.PhishInDAO;
import com.terrymoreii.phishradio.ShowDetailsFragment;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ShowDetailsService extends IntentService {

    public static final String SHOW_ID = "ShowId";
    public static final String SHOW_DETAILS_RESULT = "Show_Details_Result";


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startAction(Context context, String param1) {
        Intent intent = new Intent(context, ShowDetailsService.class);
        intent.putExtra(SHOW_ID, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ShowDetailsService.class);
        intent.putExtra(SHOW_ID, param1);
        context.startService(intent);
    }

    public ShowDetailsService() {
        super("ShowDetailsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            final String showId = intent.getStringExtra(SHOW_ID);
            final String urlStr = "http://phish.in/api/v1/shows/" + showId + ".json";

            PhishInDAO phishInDao = new PhishInDAO();
            String json = phishInDao.getData(urlStr);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ShowDetailsFragment.ResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(SHOW_DETAILS_RESULT, json);
            sendBroadcast(broadcastIntent);


        }
    }


}
