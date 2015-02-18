package com.syssec.ptrace_wrapper.adapters;
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
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.syssec.ptrace_wrapper.ProcessInfo;
import com.syssec.ptrace_wrapper.R;
 

 
public class ListAdapter extends ArrayAdapter<ProcessInfo> {
    // List context
    private final Context context;
    // List values
    private final List<ProcessInfo> values;
 
    public ListAdapter(Context context, List<ProcessInfo> values) {
   
    	super(context, R.layout.activity_main, values);
        this.context = context;
        this.values = values;
    }
 

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_main, parent, false);
         
        TextView appName = (TextView) rowView.findViewById(R.id.appNameText);
        ImageView imgName = (ImageView) rowView.findViewById(R.id.detailsIco);
        
        appName.setText(values.get(position).appname);
        imgName.setImageDrawable(values.get(position).icon);
         
        return rowView;
    }  
}

