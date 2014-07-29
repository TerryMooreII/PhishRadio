package com.terrymoreii.phishradio.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.terrymoreii.phishradio.DAO.PhishInDAO;
import com.terrymoreii.phishradio.ShowsFragment;

/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ShowsService extends IntentService {

    public static final String YEAR = "Year";
    public static final String SHOWS_RESULT = "Shows_Result";


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see android.app.IntentService
     */
    // TODO: Customize helper method
    public static void startAction(Context context, String param1) {
        Intent intent = new Intent(context, ShowsService.class);
        intent.putExtra(YEAR, param1);
        context.startService(intent);
    }


    public ShowsService() {
        super("ShowsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            final String year = intent.getStringExtra(YEAR);
            final String urlStr = "http://phish.in/api/v1/years/" + year + ".json";

            PhishInDAO phishInDao = new PhishInDAO();
            String json = phishInDao.getData(urlStr);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ShowsFragment.ResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(SHOWS_RESULT, json);
            sendBroadcast(broadcastIntent);
        }
    }
}
