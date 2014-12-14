package name.boyle.chris.powersource;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * This is the "Edit" activity for a <i>Locale</i> plug-in.
 */
public final class EditActivity extends AbstractPluginActivity
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		/*
		 * populate the spinner
		 */
		final Spinner spinner = ((Spinner) findViewById(R.id.spinner));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,
				getResources().getStringArray(R.array.sources));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		final CheckBox notCB = ((CheckBox) findViewById(R.id.not));

		/*
		 * if savedInstanceState == null, then we are entering the Activity directly from Locale and we need to check whether the
		 * Intent has forwarded a Bundle extra (e.g. whether we editing an old condition or creating a new one)
		 */
		if (savedInstanceState == null)
		{
			final Bundle forwardedBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);

			/*
			 * the forwardedBundle would be null if this was a new condition
			 */
			if (forwardedBundle != null)
			{
				int source = forwardedBundle.getInt(Constants.BUNDLE_EXTRA_POWER_SOURCE, -1);
                if (source == 4) source--;
				if (source >= 0) spinner.setSelection(source);
				notCB.setChecked(forwardedBundle.getBoolean(Constants.BUNDLE_EXTRA_NOT, false));
            }
		}
		/*
		 * if savedInstanceState != null, there is no need to restore any Activity state directly (e.g. onSaveInstanceState()).
		 * This is handled by the Spinner automatically
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish()
	{
		if (isCanceled())
			setResult(RESULT_CANCELED);
		else
		{
			final CheckBox notCB = ((CheckBox) findViewById(R.id.not));
			final Spinner spinner = ((Spinner) findViewById(R.id.spinner));

			/*
			 * This is the return Intent, into which we'll put all the required extras
			 */
			final Intent returnIntent = new Intent();

			/*
			 * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note that anything placed in
			 * this Bundle must be available to Locale's class loader. So storing String, int, and other basic objects will work
			 * just fine. You cannot store an object that only exists in your app, as Locale will be unable to serialize it.
			 */
			final Bundle storeAndForwardExtras = new Bundle();

			int source = spinner.getSelectedItemPosition();
            boolean invert = notCB.isChecked();
			storeAndForwardExtras.putBoolean(Constants.BUNDLE_EXTRA_NOT, invert);

			String blurb = (invert ? getString(R.string.not)+" " : "")
					+ getResources().getStringArray(R.array.sources)[source];
			returnIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);
			returnIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, storeAndForwardExtras);

            if (source == 3) {
                source++;
            }

            storeAndForwardExtras.putInt(Constants.BUNDLE_EXTRA_POWER_SOURCE, source);
			setResult(RESULT_OK, returnIntent);
		}

		super.finish();
	}

}