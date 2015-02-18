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
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.syssec.ptrace_wrapper.adapters.InitialListAdapter;

public class PtraceActivity extends ListActivity {

	public final static String EXTRA_MESSAGE="com.syssec.ptrace_wrapper.MESSAGE";
	
	public final static String item1 = "Start Ptrace Wrapper";
	public final static String item2 = "Display Statistics";
	
	private ArrayList<String> getInitialList() {
	    ArrayList<String> res = new ArrayList<String>();        
	    res.add(item1);
	    res.add(item2);
	    return res; 
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> initial_items = this.getInitialList();
        if (initial_items != null && initial_items.size() > 0) {
            setListAdapter(new InitialListAdapter(this, initial_items));
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong !!!", Toast.LENGTH_LONG).show();
        }
 
    }
	
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	if(id == 0){
    	// Tracing Option is selected
        String AppName = ((String)getListAdapter().getItem(position));
        Intent intent = new Intent(this,MainActivity.class);
		intent.putExtra(EXTRA_MESSAGE, AppName);
		startActivity(intent);
    	}else {
    		// Opening Stats_ptrace code 
    		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    		Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
    		    + "/Stats_ptrace/");
    		intent.setDataAndType(uri, "*/*");
    		startActivity(Intent.createChooser(intent, "Folder"));
    	}

    }	
}