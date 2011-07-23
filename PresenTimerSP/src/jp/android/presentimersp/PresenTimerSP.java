package jp.android.presentimersp;

import jp.android.presentimersp.Settings;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PresenTimerSP extends Activity {

	Chronometer chronometer;
	boolean bStarted = false;
	long starttime = 600; // [sec]
	long updownsec = 30;
	long yellowsec = 120;	// rest time to turn yellow
	long redsec = 0; 		// rest time to turn red
	long screenoffsec = 20 * 60; // passedtime to clear screen on flag

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final TextView txtRestMin;
		final TextView txtRestSec;
		final TextView txtAllottedMin;
		final TextView txtAllottedSec;
		final LinearLayout layoutRest;

		//load app pref
		loadMyPref();
		
		txtRestMin = (TextView) findViewById(R.id.txtRestMin);
		txtRestSec = (TextView) findViewById(R.id.txtRestSec);
		txtAllottedMin = (TextView) findViewById(R.id.txtAllottedMin);
		txtAllottedSec = (TextView) findViewById(R.id.txtAllottedSec);

		// set rest time
		txtRestMin.setText(String.format("%d", (int) starttime / 60));
		txtRestSec.setText(String.format("%1$02d", starttime % 60));
		// set allotted time
		txtAllottedMin.setText(String.format("%d:", (int) starttime / 60));
		txtAllottedSec.setText(String.format("%1$02d", starttime % 60));
		// set BG color
		layoutRest = (LinearLayout) findViewById(R.id.linearLayoutRest);
		layoutRest.setBackgroundColor(Color.GREEN);

		// Chronometer(timer)
		chronometer = (Chronometer) findViewById(R.id.chronometer1);
		chronometer
				.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer chronometer) {
						long pasttime = (SystemClock.elapsedRealtime() - chronometer
								.getBase()) / 1000;
						long resttime = starttime - pasttime;
						if (resttime >= 0) {
							txtRestMin.setText(String.format("%d",
									(int) resttime / 60));
							txtRestSec.setText(String.format("%1$02d",
									resttime % 60));
							if (resttime <= redsec)
								layoutRest.setBackgroundColor(Color.RED);
							else if (resttime <= yellowsec)
								layoutRest.setBackgroundColor(Color.YELLOW);
						}
						if (pasttime >= screenoffsec) {
							getWindow()
									.clearFlags(
											WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						}
					}
				});

		// Up button
		final Button upbutton = (Button) findViewById(R.id.btnUp);
		upbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bStarted != true) {
					starttime = starttime + updownsec;
					txtRestMin.setText(String.format("%d",
							(int) starttime / 60));
					txtRestSec.setText(String.format("%1$02d", starttime % 60));
					txtAllottedMin.setText(String.format("%d:",
							(int) starttime / 60));
					txtAllottedSec.setText(String.format("%1$02d", starttime % 60));
				}
			}
		});

		// Down button
		final Button downbutton = (Button) findViewById(R.id.btnDown);
		downbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bStarted != true) {
					starttime = starttime - updownsec;
					if (starttime < 0)
						starttime = 0;
					txtRestMin.setText(String.format("%d",
							(int) starttime / 60));
					txtRestSec.setText(String.format("%1$02d", starttime % 60));
					txtAllottedMin.setText(String.format("%d:",
							(int) starttime / 60));
					txtAllottedSec.setText(String.format("%1$02d", starttime % 60));
				}
			}
		});
		
		// Start button
		final Button startbutton = (Button) findViewById(R.id.btnStart);
		startbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// chronometer.setBaseTime(System.currentTimeMillis());
				chronometer.setFormat("%s");
				if (bStarted == false) {
					//load settings
					getScreenoffPref();
					getYellowTimePref();
					getRedTimePref();
					//start timer
					chronometer.setBase(SystemClock.elapsedRealtime()); 
					chronometer.start();
					bStarted = true;
					getWindow().addFlags(
							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					//
					startbutton.setText("STOP");
					layoutRest.setBackgroundColor(Color.GREEN);
					upbutton.setTextColor(Color.GRAY);
					downbutton.setTextColor(Color.GRAY);
					txtAllottedMin.setTextColor(Color.GRAY);
					txtAllottedSec.setTextColor(Color.GRAY);
				} else {
					//stop timer
					chronometer.stop();
					bStarted = false;
					getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					//
					startbutton.setText("START");
					upbutton.setTextColor(Color.BLACK);
					downbutton.setTextColor(Color.BLACK);
					txtAllottedMin.setTextColor(Color.WHITE);
					txtAllottedSec.setTextColor(Color.WHITE);
				}			
			}
		});

	}//onCreate
	
    @Override
    protected void onDestroy() {
		//save app pref
		saveMyPref();
        super.onDestroy();
    }

	//Preferences(App)
	private void saveMyPref() {
		SharedPreferences sp;
		sp = getPreferences(MODE_PRIVATE);
		
		Editor editor = sp.edit();
		editor.putLong("pref_starttime", starttime);
		editor.commit();
	}
	
	private void loadMyPref() {
		SharedPreferences sp;
		sp = getPreferences(MODE_PRIVATE);
		
		starttime = sp.getLong("pref_starttime", starttime);
	}
	
	// MENU - Settings
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem pref_item = menu.add("settings");
		OnMenuItemClickListener listener = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				openPref();
				return false;
			}
		};
		pref_item.setOnMenuItemClickListener(listener);
		return true;
	}
	
	public void openPref() {
		Intent intent = new Intent(this, (Class<?>) Settings.class);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}

	private void getScreenoffPref() {
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		String text = sp.getString("settings_screenoff", "1200");
		int new_num = Integer.parseInt(text);
		if( screenoffsec != new_num ){		
			screenoffsec = new_num;
		}
	}
	
	private void getYellowTimePref() {
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		String text = sp.getString("settings_yellowtime", "60");
		int new_num = Integer.parseInt(text);
		if( yellowsec != new_num ){		
			yellowsec = new_num;
		}
	}
	private void getRedTimePref() {
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		String text = sp.getString("settings_redtime", "0");
		int new_num = Integer.parseInt(text);
		if( redsec != new_num ){		
			redsec = new_num;
		}
	}

}