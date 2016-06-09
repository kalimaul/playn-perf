//
// PlayN Performance Tests
// http://github.com/threerings/playn-perf/blob/master/LICENSE

package com.threerings.perf.html;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.threerings.perf.core.PerfTest;

import playn.core.Log.Collector;
import playn.core.Log.Level;
import playn.html.HtmlPlatform;

public class PerfTestHtml implements EntryPoint {

	//@Override
	public void onModuleLoad() {
		HtmlPlatform.Config config = new HtmlPlatform.Config();
		config.antiAliasing = false;
		config.backgroundFrameMillis = 100; // 10 fps background rendering
		final HtmlPlatform plat = new HtmlPlatform(config);
		//plat.assets().setPathPrefix("playn-perf/");

		plat.graphics().setSize(Window.getClientWidth(), Window.getClientHeight());
		Window.scrollTo(0, 0);
		
		plat.log().setCollector(new Collector() {
			public void logged(Level level, String msg, Throwable e) {
				log(level.toString() + ": " + msg);
				if (e != null) {
					log (e.getMessage());
				}
			}
			
			native void log(String msg) /*-{ console.log(msg); }-*/;
		});

		new PerfTest(plat);
		plat.start();
	}
}
