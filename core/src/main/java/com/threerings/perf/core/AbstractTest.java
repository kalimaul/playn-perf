//
// PlayN Performance Tests
// http://github.com/threerings/playn-perf/blob/master/LICENSE

package com.threerings.perf.core;

import playn.core.Clock;
import playn.core.Image;
import playn.core.Keyboard;
import playn.core.Keyboard.KeyEvent;
import playn.core.Mouse.ButtonEvent;
import playn.scene.Mouse.Interaction;
import react.SignalView.Listener;
import react.Value;
import tripleplay.util.Hud;

/**
 * The base class for a performance test.
 */
public abstract class AbstractTest extends tripleplay.game.ScreenStack.Screen
{
    /** Called when the user taps/clicks once. */
    public void onTap () {
    }

    @Override public void wasShown () {
        super.wasShown();
        addHudBits(_hud);
        _hud.layer.setDepth(Short.MAX_VALUE);
        layer.add(_hud.layer);

        // wire up listeners for tapping and going back to the menu
        _hud.layer.events().connect(new playn.scene.Mouse.Listener() {
        	public void onButton (ButtonEvent event, Interaction iact) {
        		switch (event.button) {
                case LEFT: pop(); break;
                default: break;
                }
        	}
		});

        _hud.layer.events().connect(new playn.scene.Pointer.Listener() {
        	@Override
        	public void onStart(playn.scene.Pointer.Interaction event) {
                _tapStart = System.currentTimeMillis();
            }
            @Override public void onEnd(playn.scene.Pointer.Interaction event) {
                double duration = System.currentTimeMillis() - _tapStart;
                if (duration > 1000) pop();
                else onTap();
            }
            @Override public void onCancel(playn.scene.Pointer.Interaction event) {
            }
            protected double _tapStart;
        });
        
        game().plat.input().keyboardEvents.connect(keySlot);
        game().update.connect(update);        
    }
    
    int[] frameTimes = new int[60];
    int frameTimeCounter = 0;
    
    Listener<Clock> update = new Listener<Clock>(){
		public void onEmit(Clock event) {
			frameTimes[frameTimeCounter] = event.dt;
			frameTimeCounter = (frameTimeCounter + 1) % frameTimes.length;
			if (frameTimeCounter == 0) {
				int avg = 0;
				for (int i = 0; i < frameTimes.length; ++i) {
					avg += frameTimes[i];
				}
				
				avg = avg / frameTimes.length;
				
				game().plat.log().debug("" + avg);
			}
		}};
    
    Keyboard.KeySlot keySlot = new Keyboard.KeySlot() {
		@Override
		public void onEmit(KeyEvent event) {
			if (event.down) {
				switch (event.key) {
                case ESCAPE:
                case BACK: pop(); break;
                case SPACE: onTap(); break;
                case H: _hud.layer.setVisible(_hudActive = !_hudActive); break;
                default: break;
                }
			}
		}
	};

    @Override public void wasHidden () {
        super.wasHidden();
        game().plat.input().keyboardEvents.disconnect(keySlot);
        game().update.disconnect(update);
    }

    protected void pop () {
        PerfTest.game.stack.remove(this);
    }

    protected Image getImage (String path) {
        return PerfTest.game.plat.assets().getImage("images/" + path);
    }

    /** Override this and add UI elements to the HUD as needed. */
    protected void addHudBits (Hud hud) {
    }
    
    Value<Integer> frameTime = new Value<Integer>(0);

    private final Hud.Stock _hud = new Hud.Stock(PerfTest.game);
    private boolean _hudActive = true;
}
