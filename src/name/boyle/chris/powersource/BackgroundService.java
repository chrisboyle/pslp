package name.boyle.chris.powersource;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

/**
 * {@code Service} for monitoring the {@code REGISTERED_RECEIVER_ONLY} {@code Intent}s {@link Intent#ACTION_SCREEN_ON} and
 * {@link Intent#ACTION_SCREEN_OFF}.
 */
public final class BackgroundService extends Service
{

	/**
	 * REPRESENTATION INVARIANTS:
	 * <ol>
	 * <li>The {@link #REQUEST_REQUERY} {@code Intent} should not be modified after its static initialization completes</li>
	 * <li>{@link #isRunning} returns true only while the service is running</li>
	 * <li>{@link #mReceiver} is registered only while the service is running</li>
	 * <li>{@link #displayState} must be one of: -1 for unknown, 0 for off, or 1 for on</li>
	 * </ol>
	 */

	/**
	 * {@code Intent} to ask <i>Locale</i> to re-query our conditions. Cached here so that we only have to create this object
	 * once.
	 */
	private static final Intent REQUEST_REQUERY = new Intent(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY);

	static
	{
		/*
		 * The Activity name must be present as an extra in this Intent, so that Locale will know who needs updating. This intent
		 * will be ignored unless the extra is present.
		 */
		REQUEST_REQUERY.putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY, EditActivity.class.getName());
	}

	/**
	 * Indicates whether the {@code Service} is running or not. Since a {@code Service} is a singleton object by definition in
	 * Android, we can make this a static global.
	 */
	private static boolean isRunning = false;

	/**
	 * Global state indicating the power source.
	 * <p>
	 * One of:
	 * <ol>
	 * <li>-1 for unknown</li>
	 * <li>0 for unplugged</li>
	 * <li>BATTERY_PLUGGED_AC (1)</li>
	 * <li>BATTERY_PLUGGED_USB (2)</li>
	 * </ol>
	 */
	private static int powerSource = -1;

	/**
	 * A {@code BroadcastReceiver} to monitor {@link Intent#ACTION_BATTERY_CHANGED}.
	 */
	private BroadcastReceiver mReceiver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();

		/*
		 * Listen continuously for screen Intents. Note that this receiver will also receive a sticky broadcast.
		 */
		mReceiver = new BroadcastReceiver()
		{

			@SuppressWarnings("synthetic-access")
			@Override
			public void onReceive(final Context context, final Intent intent)
			{
				final String action = intent.getAction();

				Log.v(Constants.LOG_TAG, String.format("Received Intent action %s", action)); //$NON-NLS-1$ //$NON-NLS-2$
				if (! Intent.ACTION_BATTERY_CHANGED.equals(action)) return;
				
				powerSource = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
				if (powerSource < 0) return;

				/*
				 * Ask Locale to re-query our condition instances. Note: this plug-in does not keep track of what types of
				 * conditions have been set up. While executing this code, we have no idea whether there are even any PowerSource
				 * conditions within Locale, or whether those conditions are checking for screen on/screen off. This is an
				 * intentional design decision to eliminate all sorts of complex synchronization problems.
				 */
				sendBroadcast(REQUEST_REQUERY);
			}
		};

		/*
		 * This a RECEIVER_REGISTERED_ONLY Intent
		 */
		registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		isRunning = true;
	}

	/**
	 * Determines whether the service is actually running or not.
	 *
	 * @return true if the service is running. False if it is not.
	 */
	public static boolean isRunning()
	{
		return isRunning;
	}

	/**
	 * Gets the current screen state
	 *
	 * @param c {@code Context}
	 * @return 0 for off, 1 for on, -1 for unknown
	 */
	public static int getPowerSource(final Context c)
	{
		return powerSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBinder onBind(final Intent arg0)
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();

		unregisterReceiver(mReceiver);
		isRunning = false;
	}

}