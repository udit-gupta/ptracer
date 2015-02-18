package com.syssec.ptrace_wrapper;
/*********************************************************************************
 * Project Title : ptrace for android
 * Developer : Laukik Hoshing and Udit Gupta
 * Functionality : On running this Application, user has freedom to either select
 * 					App for analysis (Syscall Analysis) or look previous statistics.
 * 
 * Logic : File distribution :  PtraceActivity -> Deals with selecting option from user
 * 								MainActivity -> Logic to trace app & call Native JNI code
 * 								Adapters/Display : Deals with display & GUI of app
 * 
 * References : 
 * 1)http://lxr.free-electrons.com/source/kernel/ptrace.c
 * 2)http://developer.android.com/tools/sdk/ndk/index.html#GetStarted
 * 3)https://groups.google.com/forum/#!topic/android-kernel/ZvSf-AXdpMc
 * 4)http://stackoverflow.com/questions/11561216/type-jnicall-could-not-be-resolved
 * 5)http://stackoverflow.com/questions/9420619/android-strace-or-any-linux-binary-execution
 * 6)http://developer.android.com/tools/help/adb.html
 * 7)http://stackoverflow.com/questions/9530131/exec-command-android-native-code
 * 8)http://blog.techveda.org/system-call-tracing-with-strace/
 * 9)http://mohsin-junaid.blogspot.com/2013/01/android-system-calls-hooking-to-trace.html
 * 10) http://www.androidsnippets.com/get-installed-applications-with-name-package-name-version-and-icon
 * 11)http://www.vogella.com/tutorials/AndroidListView/article.html
 * 12)http://stackoverflow.com/questions/17165972/android-how-to-open-a-specific-folder-via-intent-and-show-its-content-in-a-file
 *********************************************************************************/
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.syssec.ptrace_wrapper.adapters.ListAdapter;
 
public class MainActivity extends ListActivity {
	
	public final static String EXTRA_MESSAGE="com.syssec.ptrace_wrapper.MESSAGE";
	
	private ArrayList<ProcessInfo> getInstalledApps(boolean getSysPackages) {
	    ArrayList<ProcessInfo> res = new ArrayList<ProcessInfo>();        
	    List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
	    for(int i=0;i<packs.size();i++) {
	        PackageInfo p = packs.get(i);
	        if ((!getSysPackages) && (p.versionName == null)) {
	            continue ;
	        }
	        ProcessInfo processInfo = new ProcessInfo();
	        processInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
	        processInfo.pname = p.packageName;
	        processInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
	        res.add(processInfo);
	    }
	    return res; 
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<ProcessInfo> runningProcesses = this.getInstalledApps(false);
        if (runningProcesses != null && runningProcesses.size() > 0) {
            // Set data to the list adapter
            setListAdapter(new ListAdapter(this, runningProcesses));
        } else {
            // In case there are no processes running (not a chance :))
            Toast.makeText(getApplicationContext(), "No application is running", Toast.LENGTH_LONG).show();
        }
 
    }

    public native String  stringFromJNI();

    public native String  unimplementedStringFromJNI();

    static {
        System.loadLibrary("hello-jni");
        
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        
        // Get UID of the selected process
    	System.out.println(id);
        String packageName = ((ProcessInfo)getListAdapter().getItem(position)).pname;
        //String packageName = "com.android.gallery3d";
        System.out.println(packageName);
       
        PackageManager pm = this.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        this.startActivity(intent);
        int processid = 0;
        ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
                 System.out.println(pids.size());
           for(int i = 0; i < pids.size(); i++)
           {
               ActivityManager.RunningAppProcessInfo info = pids.get(i);
               //System.out.println(info.processName+"  packg ==>"+packageName);
               if(info.processName.equalsIgnoreCase(packageName)){
                  processid = info.pid;
                  System.out.println("PID :"+processid);
                  loadJNI(processid,packageName);
               } 
           }
    }
    
    static void loadJNI(int processid,String packageName){
    	        
    	        Process process;
    			try {
    			
    				process = Runtime.getRuntime().exec("su");
    				OutputStream os;
    		        os = process.getOutputStream();
    		        System.out.println("PID inside try:"+processid);

    		            		        
    		        File dir = new File(Environment.getExternalStorageDirectory() + "/Stats_ptrace");
    		        if(!dir.exists()) {
    		            dir.mkdirs();
    		        }
    		        
    		        File dir_sub = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stats_ptrace/"+packageName);
    		        if(!dir_sub.exists()) {
    		            dir_sub.mkdirs();
    		        }
    		        
    		        String command[] = {"strace -c -p "+ processid+" &> /mnt/sdcard/Stats_ptrace/"+packageName+"/app_stats_"+System.currentTimeMillis()+".txt","kill -9 "+processid};
    		        for (String tmpCmd : command) {
    	   			 os.write((tmpCmd+"\n").getBytes());
    		        }
    		        os.write("exit\n".getBytes());
    		        os.close();
    		        
    		        process.waitFor();
    		        
    		        BufferedReader reader=new BufferedReader(new InputStreamReader(process.getErrorStream()));
    		        String line=reader.readLine();
    		        
    		        while(line!=null)
    		        {
    		            System.out.println(line);
    		            line=reader.readLine();
    		        }
    		        
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	    }
 
}
