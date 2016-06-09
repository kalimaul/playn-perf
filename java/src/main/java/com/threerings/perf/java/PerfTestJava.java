//
// PlayN Performance Tests
// http://github.com/threerings/playn-perf/blob/master/LICENSE

package com.threerings.perf.java;

import playn.java.LWJGLPlatform;

import com.threerings.perf.core.PerfTest;

public class PerfTestJava
{
    public static void main (String[] args) {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
		config.width = 400;
		config.height = 400;
		// use config to customize the Java platform, if needed
		LWJGLPlatform plat = new LWJGLPlatform(config);
		new PerfTest(plat);
		plat.start();
    }
}
