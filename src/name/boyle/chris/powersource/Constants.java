package name.boyle.chris.powersource;

/**
 * Class of {@code Intent} constants used by this <i>Locale</i> plug-in.
 */
final class Constants
{
	/**
	 * Private constructor prevents instantiation
	 *
	 * @throws UnsupportedOperationException because this class cannot be instantiated.
	 */
	private Constants()
	{
		throw new UnsupportedOperationException(String.format("%s(): This class is non-instantiable", this.getClass().getSimpleName())); //$NON-NLS-1$
	}

	/**
	 * TYPE: {@code boolean}
	 * <p>
	 * Invert the check
	 */
	protected static final String BUNDLE_EXTRA_NOT = "name.boyle.chris.powersource.extra.NOT"; //$NON-NLS-1$
	
	/**
	 * TYPE: {@code int}
	 * <p>
	 * The power source to check for, as returned in ACTION_BATTERY_CHANGED/EXTRA_PLUGGED
	 */
	protected static final String BUNDLE_EXTRA_POWER_SOURCE = "name.boyle.chris.powersource.extra.POWERSOURCE"; //$NON-NLS-1$

	public static final boolean IS_LOGGABLE = BuildConfig.DEBUG;

	public static final String LOG_TAG = "PowerSource";
}