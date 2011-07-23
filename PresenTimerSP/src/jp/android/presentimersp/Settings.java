package jp.android.presentimersp;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;


public class Settings extends PreferenceActivity {    
	Preference screenoffPref;  
	final static String SCREENOFF_KEY = "settings_screenoff"; 
	Map<String, String> screenoffMap = new HashMap<String, String>(); 

	Preference yellowtimePref;  
	final static String YELLOWTIME_KEY = "settings_yellowtime"; 
	Map<String, String> yellowtimeMap = new HashMap<String, String>(); 

	Preference redtimePref;  
	final static String REDTIME_KEY = "settings_redtime"; 
	Map<String, String> redtimeMap = new HashMap<String, String>(); 

	protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        addPreferencesFromResource(R.xml.preferences); 
        
    	String[] screenoffKeys       = getResources().getStringArray(R.array.list_entries);  
    	String[] screenoffValues       = getResources().getStringArray(R.array.list_entryvalues);  

    	String[] yellowtimeKeys       = getResources().getStringArray(R.array.list_resttime_entries);  
    	String[] yellowtimeValues       = getResources().getStringArray(R.array.list_resttime_entryvalues);  

       	String[] redtimeKeys       = getResources().getStringArray(R.array.list_resttime_entries);  
    	String[] redtimeValues       = getResources().getStringArray(R.array.list_resttime_entryvalues);  

    	//
    	for(int i = 0; i < screenoffKeys.length; i++)  
    		screenoffMap.put(screenoffValues[i], screenoffKeys[i]);  
      
    	screenoffPref = findPreference(SCREENOFF_KEY);  
        
    	ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference(SCREENOFF_KEY);
    	list_preference.setSummary(screenoffMap.get(list_preference.getValue()));
    	
    	//
    	for(int i = 0; i < yellowtimeKeys.length; i++)  
    		yellowtimeMap.put(yellowtimeValues[i], yellowtimeKeys[i]);  
      
    	yellowtimePref = findPreference(YELLOWTIME_KEY);  
    	list_preference = (ListPreference)getPreferenceScreen().findPreference(YELLOWTIME_KEY);
    	list_preference.setSummary(yellowtimeMap.get(list_preference.getValue()));

    	//
    	for(int i = 0; i < redtimeKeys.length; i++)  
    		redtimeMap.put(redtimeValues[i], redtimeKeys[i]);  
      
    	redtimePref = findPreference(REDTIME_KEY);  
    	list_preference = (ListPreference)getPreferenceScreen().findPreference(REDTIME_KEY);
    	list_preference.setSummary(redtimeMap.get(list_preference.getValue()));
    }  
  
    //public static boolean isSound(Context con){  
    //    return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("check1", false);  
    //}  
    //public static boolean isVib(Context con){  
    //    return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("check2", false);  
    //}  
    
    
    @Override  
    protected void onResume() {  
        super.onResume();  
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);  
    }  
       
    @Override  
    protected void onPause() {  
        super.onPause();  
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);  
    }  
      
    // ‚±‚±‚Å summary ‚ð“®“I‚É•ÏX  
    private SharedPreferences.OnSharedPreferenceChangeListener listener =   
        new SharedPreferences.OnSharedPreferenceChangeListener() {  
           
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {  
            
            if(key.equals(SCREENOFF_KEY))     
                screenoffPref.setSummary(screenoffMap.get(sharedPreferences.getString(key, "settings_screenoff")));  

            if(key.equals(YELLOWTIME_KEY))     
                yellowtimePref.setSummary(yellowtimeMap.get(sharedPreferences.getString(key, "settings_yellowtime")));  

            if(key.equals(REDTIME_KEY))     
                redtimePref.setSummary(redtimeMap.get(sharedPreferences.getString(key, "settings_redwtime")));  
        
        }  
    };  
}