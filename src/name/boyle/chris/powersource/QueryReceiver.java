package name.boyle.chris.powersource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * This is the "query" {@code BroadcastReceiver} for a <i>Locale</i> plug-in condition.
 */
public final class QueryReceiver extends BroadcastReceiver
{

	/**
	 * @param context {@inheritDoc}.
	 * @param intent the incoming {@code Intent}. This should always contain the store-and-forward {@code Bundle} that was saved
	 *            by {@link EditActivity} and later broadcast by <i>Locale</i>.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		/*
		 * Always be sure to be strict on your input parameters! A malicious third-party app could always send your plug-in an
		 * empty or otherwise malformed Intent. And since Locale applies settings in the background, you don't want your plug-in
		 * to crash.
		 */
		if (!com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction()))
		{
			Log.w(Constants.LOG_TAG, "Received unexpected Intent action"); //$NON-NLS-1$
			return;
		}

		final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		if (bundle == null)
		{
			Log.e(Constants.LOG_TAG, "Received null BUNDLE"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		if (!bundle.containsKey(Constants.BUNDLE_EXTRA_POWER_SOURCE))
		{
			Log.e(Constants.LOG_TAG, "Missing power source param in Bundle"); //$NON-NLS-1$
			return;
		}
		boolean invert = bundle.getBoolean(Constants.BUNDLE_EXTRA_NOT, false);

		if (!BackgroundService.isRunning())
		{
			/*
			 * To detect screen changes as they happen, a service must be running because the SCREEN_ON/OFF Intents are
			 * REGISTERED_RECEIVER_ONLY.
			 */
			final Intent serviceIntent = new Intent(context, BackgroundService.class);
			serviceIntent.putExtras(intent);
			context.startService(serviceIntent);
		}

		final int state = BackgroundService.getPowerSource(context);
		if (state < 0)
			setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_UNKNOWN);
		else if (!invert && state == bundle.getInt(Constants.BUNDLE_EXTRA_POWER_SOURCE))
			setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
		else if (invert && state != bundle.getInt(Constants.BUNDLE_EXTRA_POWER_SOURCE))
			setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
		else
			setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_UNSATISFIED);
	}
}