package com.example.simplegpstracker;

import java.util.Date;

import com.example.simplegpstracker.utils.UtilsNet;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	Button bViewMap;
	Button bStartService;
	Button bStopService;
	Button bSendService;

	Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        context = getApplicationContext();
        
        bViewMap = (Button)findViewById(R.id.bViewMap);
        bViewMap.setOnClickListener(new ClickListener());
        bStartService = (Button)findViewById(R.id.bStartService);
        bStartService.setOnClickListener(new ClickListener());
        bStopService = (Button)findViewById(R.id.bStopService);
        bStopService.setOnClickListener(new ClickListener());
        bSendService = (Button)findViewById(R.id.bSendServer);
        bSendService.setOnClickListener(new ClickListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Intent intentPref = new Intent(this, PrefActivity.class);
        	startActivity(intentPref);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.bStartService:
				Intent iStartService = new Intent(context, TrackService.class);
				startService(iStartService);
				Toast toast_start = Toast.makeText(context, context.getResources().getString(R.string.service_start), Toast.LENGTH_SHORT); 
				toast_start.show(); 
				break;
			case R.id.bStopService:
				Intent iStopService = new Intent(context, TrackService.class);
				stopService(iStopService);
				Toast toast_stop = Toast.makeText(context, context.getResources().getString(R.string.service_stop), Toast.LENGTH_SHORT);
				toast_stop.show();
				break;
			case R.id.bViewMap:
				if(!UtilsNet.isOnline(getApplicationContext())){
					Toast toast = Toast.makeText(context, context.getResources().getString(R.string.network_off), Toast.LENGTH_SHORT); 
					toast.show();				
				}else if(UtilsNet.IsServiceRunning(context)){
					Toast toast = Toast.makeText(context, context.getResources().getString(R.string.service_started), Toast.LENGTH_SHORT); 
					toast.show();
				}else{
					Intent iMap = new Intent(context, ViewMapActivity.class);
					startActivity(iMap);
				}
				break;
			case R.id.bSendServer:
				new Transmitter(context).send();
				break;
			default:
				break;
			}
		
		}
    	
    }   

}
