//
// PlayN Performance Tests
// http://github.com/threerings/playn-perf/blob/master/LICENSE

package com.threerings.perf.core;

import playn.core.Platform;
import playn.scene.Pointer;
import playn.scene.SceneGame;
import tripleplay.game.ScreenStack;

public class PerfTest extends SceneGame
{
    public final ScreenStack stack;
    public static PerfTest game;

    public PerfTest (Platform plat) {
        super(plat, 1);
        game = this;
        stack = new ScreenStack(this, rootLayer);
        stack.push(new TestMenu(plat));
        new Pointer(plat, rootLayer, true);
    }
}
