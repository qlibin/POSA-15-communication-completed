package vandy.mooc.activities;

import vandy.mooc.R;
import vandy.mooc.aidl.WeatherData;
import vandy.mooc.operations.WeatherOps;
import vandy.mooc.operations.WeatherOpsImpl;
import vandy.mooc.utils.RetainedFragmentManager;
import vandy.mooc.utils.Utils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The main Activity that prompts the user for location to get weather information
 * via various implementations of WeatherServiceSync and
 * WeatherServiceAsync and view via the results.  Extends
 * LifecycleLoggingActivity so its lifecycle hook methods are logged
 * automatically.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Used to retain the ImageOps state between runtime configuration
     * changes.
     */
    protected final RetainedFragmentManager mRetainedFragmentManager = 
        new RetainedFragmentManager(this.getFragmentManager(),
                                    TAG);

    /**
     * Provides weather-related operations.
     */
    private WeatherOps mWeatherOps;

    /**
     * Location entered by the user.
     */
    protected EditText mEditText;

    protected TextView mTextView;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., runtime
     * configuration changes.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Get references to the UI components.
        setContentView(R.layout.activity_main);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = (EditText) findViewById(R.id.editText1);

        mTextView = (TextView) findViewById(R.id.result);

        // Handle any configuration change.
        handleConfigurationChanges();
    }

    /**
     * Hook method called by Android when this Activity is
     * destroyed.
     */
    @Override
    protected void onDestroy() {
        // Unbind from the Service.
        mWeatherOps.unbindService();

        // Always call super class for necessary operations when an
        // Activity is destroyed.
        super.onDestroy();
    }

    /**
     * Handle hardware reconfigurations, such as rotating the display.
     */
    protected void handleConfigurationChanges() {
        // If this method returns true then this is the first time the
        // Activity has been created.
        if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d(TAG,
                  "First time onCreate() call");

            // Create the WeatherOps object one time.  The "true"
            // parameter instructs WeatherOps to use the
            // DownloadImagesBoundService.
            mWeatherOps = new WeatherOpsImpl(this);

            // Store the WeatherOps into the RetainedFragmentManager.
            mRetainedFragmentManager.put("WEATHER_OPS_STATE",
                    mWeatherOps);
            
            mWeatherOps.bindService();
        } else {

            Log.d(TAG,
                  "Second or subsequent onCreate() call");

            mWeatherOps =
                mRetainedFragmentManager.get("WEATHER_OPS_STATE");

            // This check shouldn't be necessary under normal
            // circumtances, but it's better to lose state than to
            // crash!
            if (mWeatherOps == null) {
                mWeatherOps = new WeatherOpsImpl(this);

                mRetainedFragmentManager.put("WEATHER_OPS_STATE",
                        mWeatherOps);

                mWeatherOps.bindService();
            } else
                mWeatherOps.onConfigurationChange(this);
        }
    }

    /*
     * Initiate the synchronous location weather lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void requestWeatherSync(View v) {
        final String location =
            mEditText.getText().toString();
        
        resetDisplay();

        mWeatherOps.requestWeatherSync(location);
    }

    /*
     * Initiate the asynchronous weather lookup when the user presses
     * the "Look Up Async" button.
     */
    public void requestWeatherAsync(View v) {
        final String location =
            mEditText.getText().toString();
        
        resetDisplay();
        
        mWeatherOps.requestWeatherAsync(location);
    }

    /**
     * Display the results to the screen.
     * 
     * @param result
     *            List of Results to be displayed.
     */
    public void displayResults(WeatherData result,
                               String errorMessage) {
        if (result == null) {
            Utils.showToast(this,
                    errorMessage);
            // display results
            mTextView.setText("No weather data for the location");
        } else {
            Log.d(TAG, "displayResults()");

            try {
                // display results
                mTextView.setText(
                        String.format(
                                "Location: %s\n" +
                                "Temperature: %.0fF\n" +
                                "Humidity: %d%%\n" +
                                "Wind speed: %.2fmph\n",
                                result.getmName(),
                                result.getmTemp(),
                                result.getmHumidity(),
                                result.getmSpeed()
                        )
                );
            } catch (Exception e) {
                Utils.showToast(this,
                        e.getMessage());
            }
        }
    }

    /**
     * Reset the display prior to attempting to find another location weather.
     */
    private void resetDisplay() {
        Utils.hideKeyboard(this,
                           mEditText.getWindowToken());
        mTextView.setText("");
    }
}
