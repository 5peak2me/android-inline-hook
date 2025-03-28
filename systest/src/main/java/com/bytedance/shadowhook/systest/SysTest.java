// Copyright (c) 2021-2024 ByteDance Inc.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

// Created by Kelun Cai (caikelun@bytedance.com) on 2021-04-11.

package com.bytedance.shadowhook.systest;

import com.bytedance.shadowhook.ShadowHook;

public class SysTest {

    private static boolean inited = false;
    private static int initErrno = 200; // uninit
    private static final String libName = "shadowhooksystest";

    public static int init(boolean sharedMode, boolean debuggable, boolean recordable) {
        if (inited) {
            return initErrno;
        }
        inited = true;

        // init shadowhook
        initErrno = ShadowHook.init(new ShadowHook.ConfigBuilder()
                .setMode(sharedMode ? ShadowHook.Mode.SHARED : ShadowHook.Mode.UNIQUE)
                .setDebuggable(debuggable)
                .setRecordable(recordable)
                .build());
        if(0 != initErrno) {
            return initErrno;
        }

        // load libshadowhooksystest.so
        try {
            System.loadLibrary(libName);
        } catch (Throwable ignored) {
            initErrno = 201;
            return initErrno;
        }

        initErrno = 0;
        return initErrno;
    }

    public static int hook() {
        if (initErrno != 0) {
            return initErrno;
        }

        return nativeHook();
    }

    public static int unhook() {
        if (initErrno != 0) {
            return initErrno;
        }

        return nativeUnhook();
    }

    public static int run() {
        if (initErrno != 0) {
            return initErrno;
        }

        return nativeRun();
    }

    public static native int nativeHook();
    public static native int nativeUnhook();
    public static native int nativeRun();
}
