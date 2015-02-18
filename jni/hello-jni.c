/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <sys/ptrace.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <android/log.h>
#include <sys/ptrace.h>
#include <sys/user.h>
#include <linux/pci_regs.h>

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
jstring
Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
#if defined(__arm__)
  #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
   #define ABI "x86"
#elif defined(__x86_64__)
   #define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
   #define ABI "mips64"
#elif defined(__mips__)
   #define ABI "mips"
#elif defined(__aarch64__)
   #define ABI "arm64-v8a"
#else
   #define ABI "unknown"
#endif

	int ret=999;
	//struct user_regs regs;
	//int pid = getpid();
	//__android_log_print(ANDROID_LOG_DEBUG, "JNI_TAG", "Pid = %d",pid );

	pid_t child = fork();
	//__android_log_print(ANDROID_LOG_DEBUG, "JNI_TAG", "Child = %d",child );

/*	if (child == 0) {

		ptrace(PTRACE_TRACEME);
		kill(getpid(), SIGSTOP);
		//pid = execl("/system/bin/am", "start", "-c", "am start -a android.intent.action.MAIN -n com.android.settings/.Settings", (char *)NULL);
		//execl("/system/bin/sh", "sh", "am start -a android.intent.action.MAIN -n com.android.settings/.Settings", (char *)NULL);
		//ret = execl("/system/bin/sh", "sh", "-c", "am start -a android.intent.action.MAIN -n com.android.browser/.BrowserActivity", (char *)NULL);
	__android_log_print(ANDROID_LOG_DEBUG, "CHILD_ZERO", "Exec execute");
		execvp(NULL, (char *)NULL);
		//ret = execl("/system/bin/sh", "sh", "-c", "ls -al>C:/syssec.txt", (char *)NULL);
	    }
	else {
	   	 int status, syscall, retval;
	    	    waitpid(child, &status, 0);
	    	    ptrace(PTRACE_SETOPTIONS, child, 0, PTRACE_O_TRACESYSGOOD);

	    	    while(1) {
	    	           if (wait_for_syscall(child) != 0) break;
	    	           //ptrace(PTRACE_GETREGS, NULL, &regs);
	    	           __android_log_print(ANDROID_LOG_DEBUG, "PARENT_WHILE_LOOP", "Before Syscall: %d", ret);
	    	           //register long r_7 asm("r7");
	    	           //asm("mov r_7,r7");
	    	          // syscall = ptrace(PTRACE_PEEKUSER, child, regs.uregs[7]);
	    	          // __android_log_print(ANDROID_LOG_DEBUG, "JNI_TAG", "Syscall = %d", syscall );


	    	       }


	    }*/

    return (*env)->NewStringUTF(env, "Hello from JNI !  Compiled with ABI " ABI ".");



}

int wait_for_syscall(pid_t child) {
	__android_log_print(ANDROID_LOG_DEBUG, "WAIT_FOR_SYSCALL", "Inside wait for syscall");
    int status;
    while (1) {
        ptrace(PTRACE_SYSCALL, child, 0, 0);
        waitpid(child, &status, 0);
        if (WIFSTOPPED(status) && (WSTOPSIG(status) & 0x80))
            return 0;
        if (WIFEXITED(status))
            return 1;
    }
}
