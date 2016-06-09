//
// PlayN Performance Tests
// http://github.com/threerings/playn-perf/blob/master/LICENSE

package com.threerings.perf.core;

import java.util.Random;

import pythagoras.d.MathUtil;
import react.SignalView;
import playn.core.Image;
import playn.core.Surface;
import playn.core.Tile;
import playn.scene.ClippedLayer;
import playn.core.Clock;
import playn.core.Game;

import tripleplay.util.Hud;

/**
 * Renders a bunch of tiled quads to an immediate {@link Surface} at a scroll offset, which is
 * updated to effect a smooth scrolling, full-screen background.
 */
public class ScrollingQuads extends AbstractTest
{
    public static TestConfig config () {
        return new TestConfig() {
            @Override protected AbstractTest create () {
                return new ScrollingQuads();
            }

            /* init */ {
                addHeader("Scrolling Quads");
                // TODO: any config params?
                addStartButton();
            }
        };
    }

    @Override public void onTap () {
        // random velocity from -.25 to .25 (pixels/ms) in x and y
        _vx = _rando.nextFloat() * (_rando.nextBoolean() ? -0.25f : 0.25f);
        _vy = _rando.nextFloat() * (_rando.nextBoolean() ? -0.25f : 0.25f);
    }

    @Override public void wasShown () {
        super.wasShown();
        layer.add(new ClippedLayer(size().width(), size().height()) {
			@Override
			protected void paintClipped(Surface surf) {
				renderTiles(surf, _px, _py);
			}
		});
        
        game().update.connect(update);
        game().paint.connect(paint);
    }
    
    @Override
    public void wasHidden() {
    	game().update.disconnect(update);
    	game().paint.disconnect(paint);
    }
    
    SignalView.Listener<Clock> update = new SignalView.Listener<Clock>() {
		public void onEmit(Clock event) {
			_cx = _nx;
	        _cy = _ny;
	        _nx = _cx + event.dt * _vx;
	        _ny = _cy + event.dt * _vy;
		}
	};
	
	SignalView.Listener<Clock> paint = new SignalView.Listener<Clock>() {
		public void onEmit(Clock clock) {
			float alpha = clock.alpha;
	        _px = _cx + (_nx - _cx) * alpha;
	        _py = _cy + (_ny - _cy) * alpha;
		}
	};
    
    @Override protected void addHudBits (Hud hud) {
        hud.add("BouncingQuads:", true);
        hud.add("Tap HUD to change scroll dir", false);
    }

    protected ScrollingQuads () {
        // create our tile grid
        _cols = MathUtil.iceil(size().width() / SIZE);
        _rows = MathUtil.iceil(size().height() / SIZE);
        _grid = new int[_cols*_rows];

        // TODO: configure a random rotation?
        for (int ii = 0; ii < _grid.length; ii++) _grid[ii] = _rando.nextInt(TILES);
    }

    protected void renderTiles (Surface surf, float px, float py) {
        int sc = MathUtil.ifloor(px / SIZE), sr = MathUtil.ifloor(py / SIZE);
        float ox = -((px < 0) ? (((px % SIZE) + SIZE) % SIZE) : (px % SIZE));
        float oy = -((py < 0) ? (((py % SIZE) + SIZE) % SIZE) : (py % SIZE));
        float x, y = oy;
        if (oy == -64)
            System.err.println(px + " " + py + " " + sc + " " + sr + " " + ox + " " + oy);
        for (int rr = 0, llr = _rows; rr <= llr; rr++) {
            int er = (((sr + rr) % _rows) + _rows) % _rows;
            x = ox;
            for (int cc = 0, llc = _cols; cc <= llc; cc++) {
                int ec = (((sc + cc) % _cols) + _cols) % _cols;
                Tile tile = getTile(_grid[er*_cols+ec]);
                if (tile != null) {
                	surf.draw(tile, x, y);
                }
                x += SIZE;
            }
            y += SIZE;
        }
    }

    protected Tile getTile (int index) {
        Tile tile = _tiles[index];
        if (!_atlas.isLoaded()) {
        	return null;
        }
        
        if (tile == null) {
            int row = index / COLS, col = index % COLS;
            tile = _atlas.region(col*SIZE, row*SIZE, SIZE, SIZE).tile();
            _tiles[index] = tile;
        }
        return tile;
    }

    protected final Random _rando = new Random();
    protected final int _cols, _rows;
    protected final int[] _grid;

    protected float _cx, _cy, _nx, _ny, _vx, _vy, _px, _py;

    protected final Image _atlas = getImage("tiles.png");
    protected final Tile[] _tiles = new Tile[TILES];

    protected static final int COLS = 5, TILES = COLS*4-1;
    protected static final float SIZE = 64;
	@Override
	public Game game() {
		return PerfTest.game;
	}
}
