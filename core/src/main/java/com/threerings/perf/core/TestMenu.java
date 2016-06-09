//
// PlayN Performance Tests
// http://github.com/threerings/playn-perf/blob/master/LICENSE

package com.threerings.perf.core;

import pythagoras.d.MathUtil;

import playn.core.Game;
import playn.core.Platform;
import tripleplay.game.ScreenStack.UIScreen;
import tripleplay.ui.Background;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.layout.TableLayout;
import tripleplay.util.Ref;

/**
 * Displays a menu from which performance tests can be selected.
 */
public class TestMenu extends UIScreen
{
    public TestMenu(Platform plat) {
		super(plat);
	}

	@Override public void wasAdded () {
        super.wasAdded();
        int cols = Math.max(1, MathUtil.ifloor(size().width() / 200));
        Root root = _root.set(iface.createRoot(new TableLayout(cols).gaps(10, 10),
                                               SimpleStyles.newSheet(game().plat.graphics()), layer));
        root.addStyles(Style.BACKGROUND.is(Background.solid(0xFF99CCFF).inset(10)),
                       Style.VALIGN.top);
        root.setSize(size().width(), size().height());

        Background configBG = Background.solid(0xFFCCCCCC).inset(10);
        root.add(TableLayout.colspan(new Label("PlayN Performance Tests"), cols));
        root.add(BouncingQuads.config().addStyles(Style.BACKGROUND.is(configBG)));
        root.add(ScrollingQuads.config().addStyles(Style.BACKGROUND.is(configBG)));
        root.add(ParticleBurst.config().addStyles(Style.BACKGROUND.is(configBG)));
    }

    @Override public void wasRemoved () {
        super.wasRemoved();
        _root.clear();
    }

    protected final Ref<Root> _root = Ref.<Root>create(null);

	@Override
	public Game game() {
		return PerfTest.game;
	}
}
