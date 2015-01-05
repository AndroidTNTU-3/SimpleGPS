package com.example.simplegpstracker.preference;

import com.example.simplegpstracker.R;
import com.example.simplegpstracker.R.array;
import com.example.simplegpstracker.R.string;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

@SuppressLint("NewApi")
public class FragmentPreference extends PreferenceFragment{
	
	private PreferenceClickListener preferenceClickListener = new PreferenceClickListener();
	private UrlDialogFragment dialogUrl;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogUrl = new UrlDialogFragment();
        setPreferenceScreen(createPreferences());
    }
	
	//1.Create PreferenceScreen
	private PreferenceScreen createPreferences() {
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());
		addCategory(root);
		return root;
	}
	//2.Add PreferenceCategory
	private PreferenceCategory addCategory(PreferenceScreen root) {

		PreferenceCategory rootPreferenceCategory = new PreferenceCategory(getActivity());
		rootPreferenceCategory.setTitle(getString(R.string.title_activity_pref));
		root.addPreference(rootPreferenceCategory);
		
		addListPreference(rootPreferenceCategory);
		return rootPreferenceCategory;
	}
	
	private void addListPreference(PreferenceCategory rootPreferenceCategory){
		rootPreferenceCategory.addPreference(createLocationListPreference());
		rootPreferenceCategory.addPreference(createRefreshListPreference());
		rootPreferenceCategory.addPreference(createViewRouteListPreference());
		rootPreferenceCategory.addPreference(createTravelListPreference());
		rootPreferenceCategory.addPreference(createRootPreference());
	}
	
	private Preference createLocationListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setTitle(getResources().getString(R.string.providers_title));
    	listPreference.setKey(getResources().getString(R.string.providers_key));
    	listPreference.setEntries(R.array.location_provider);
    	listPreference.setEntryValues(R.array.location_provider_value);
    	//listPreference.setOnPreferenceChangeListener(preferenceChangeListener);
    	return listPreference;
	}
	
	private Preference createRefreshListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setTitle(getResources().getString(R.string.refresh_time_title));
    	listPreference.setKey(getResources().getString(R.string.refresh_time_key));
    	listPreference.setEntries(R.array.refresh_time);
    	listPreference.setEntryValues(R.array.refresh_time_value);
    	//listPreference.setOnPreferenceChangeListener(preferenceChangeListener);
    	return listPreference;
	}
	
	private Preference createViewRouteListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setTitle(getResources().getString(R.string.view_route_title));
    	listPreference.setKey(getResources().getString(R.string.view_route_key));
    	listPreference.setEntries(R.array.view_route);
    	listPreference.setEntryValues(R.array.view_route_value);
    	//listPreference.setOnPreferenceChangeListener(preferenceChangeListener);
    	return listPreference;
	}
	
	private Preference createTravelListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setTitle(getResources().getString(R.string.travel_mode_title));
    	listPreference.setKey(getResources().getString(R.string.travel_mode_key));
    	listPreference.setEntries(R.array.travel_mode);
    	listPreference.setEntryValues(R.array.travel_mode_value);
    	//listPreference.setOnPreferenceChangeListener(preferenceChangeListener);
    	return listPreference;
	}
	
	private Preference createRootPreference() {
		Preference preference = new Preference(getActivity());
        preference.setTitle(R.string.url_server_title);
        preference.setOnPreferenceClickListener(preferenceClickListener);

        preference.setKey(getResources().getString(R.string.url_server_key));
        
		return preference;
	}
	
	private class PreferenceClickListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            	if (key.equals(getResources().getString(R.string.url_server_key))) {
            	
            	dialogUrl.show(getFragmentManager(), "dlg1");

                return true;
            }

            return false;
        }
    }
}
