package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import space.earlygrey.shapedrawer.ShapeDrawer;
import zendo.games.grotto.Config;
import zendo.games.grotto.Game;
import zendo.games.grotto.utils.Calc;
import zendo.games.grotto.utils.Point;
import zendo.games.grotto.utils.RectI;
import zendo.games.grotto.utils.VectorPool;

import java.util.Arrays;

public class Collider implements Component {

    public static final String TAG = Collider.class.getSimpleName();

    public static class Mask {
        public static final int solid         = 1 << 0;
        public static final int room_bounds   = 1 << 1;
        public static final int player        = 1 << 2;
    }

    public enum Shape { none, rect, grid }

    public static class Grid {
        public int tileSize;
        public int cols;
        public int rows;
        public boolean[] cells;
    }

    public int mask = 0;
    public Point origin;

    private Shape shape;
    private RectI rect;
    private Grid grid;

    private final Entity entity;
    private final RectI worldRect;

    public Collider(Entity entity) {
        this.entity = entity;
        this.origin = Point.zero();
        this.worldRect = RectI.zero();
    }

    // ------------------------------------------------------------------------

    public static Collider makeRect(Entity entity, RectI rect) {
        var collider = new Collider(entity);
        collider.shape = Shape.rect;
        collider.rect = RectI.at(rect);
        return collider;
    }

    public static Collider makeGrid(Entity entity, int tileSize, int cols, int rows) {
        var collider = new Collider(entity);
        collider.shape = Shape.grid;
        collider.grid = new Grid();
        collider.grid.tileSize = tileSize;
        collider.grid.cols = cols;
        collider.grid.rows = rows;
        collider.grid.cells = new boolean[cols * rows];
        Arrays.fill(collider.grid.cells, false);
        return collider;
    }

    // ------------------------------------------------------------------------

    public Shape shape() {
        return shape;
    }

    public RectI rect() {
        if (shape != Shape.rect) {
            throw new GdxRuntimeException("Collider is not a Rectangle");
        }
        return rect;
    }

    public RectI worldRect() {
        var position = VectorPool.vec2.obtain().setZero();
        if (Mappers.positions.has(entity)) {
            position.set(Mappers.positions.get(entity).position());
        }
        worldRect.set(
                (int) position.x + origin.x + rect.x,
                (int) position.y + origin.y + rect.y,
                rect.w, rect.h);
        VectorPool.vec2.free(position);
        return worldRect;
    }

    public Collider rect(RectI rect) {
        if (shape != Shape.rect) {
            throw new GdxRuntimeException("Collider is not a Rectangle");
        }
        this.rect.set(rect);
        return this;
    }

    public Collider rect(int x, int y, int w, int h) {
        if (shape != Shape.rect) {
            throw new GdxRuntimeException("Collider is not a Rectangle");
        }
        rect.set(x, y, w, h);
        return this;
    }

    // ------------------------------------------------------------------------

    public Grid grid() {
        if (shape != Shape.grid) {
            throw new GdxRuntimeException("Collider is not a Grid");
        }
        return grid;
    }

    public boolean getCell(int x, int y) {
        if (shape != Shape.grid) {
            throw new GdxRuntimeException("Collider is not a Grid");
        }
        if (x < 0 || y < 0 || x >= grid.cols || y >= grid.rows) {
            throw new GdxRuntimeException("Cell is out of bounds");
        }
        return grid.cells[x + y * grid.cols];
    }

    public void setCell(int x, int y, boolean value) {
        if (shape != Shape.grid) {
            if (Config.Debug.general) {
                Gdx.app.log(TAG, "Collider is not a grid");
            }
            return;
        }
        if (x < 0 || y < 0 || x >= grid.cols || y >= grid.rows) {
            if (Config.Debug.general) {
                Gdx.app.log(TAG, "Cell is out of bounds");
            }
            return;
        }
        grid.cells[x + y * grid.cols] = value;
    }

    public void setCells(int x, int y, int w, int h, boolean value) {
        if (shape != Shape.grid) {
            throw new GdxRuntimeException("Collider is not a Grid");
        }
        if (x < 0 || y < 0 || x + w > grid.cols || y + h > grid.rows) {
            if (Config.Debug.general) {
                Gdx.app.log(TAG, "Cell is out of bounds");
            }
            return;
        }
        for (int ix = x; ix < x + w; ix++) {
            for (int iy = y; iy < y + h; iy++) {
                grid.cells[ix + iy * grid.cols] = value;
            }
        }
    }

    // ------------------------------------------------------------------------

    public Collider first(int mask) {
        return first(mask, Point.Zero);
    }

    public Collider first(int mask, Point offset) {
        var entities = Game.instance.engine.getEntitiesFor(Families.colliders);
        for (var entity : entities) {
            var other = Mappers.colliders.get(entity);
            var isDifferent = (other != this);
            var isMasked = ((other.mask & mask) == mask);
            var isOverlap = overlaps(other, offset);
            if (isDifferent && isMasked && isOverlap) {
                return other;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------

    public boolean check(int mask) {
        return check(mask, Point.Zero);
    }

    public boolean check(int mask, Point offset) {
        // TODO - add active toggle to all Components?
//        if (!active) return false;
        var entities = Game.instance.engine.getEntitiesFor(Families.colliders);
        for (var entity : entities) {
            var other = Mappers.colliders.get(entity);
            var isDifferent = (other != this);
            var isMasked = ((other.mask & mask) == mask);
            var isOverlap = overlaps(other, offset); // TODO - issue in rectVsGrid check
            if (isDifferent && isMasked && isOverlap) {
                return true;
            }
        }
        return false;
    }

    public boolean overlaps(Collider other, Point offset) {
        if (shape == Shape.rect) {
            if (other.shape == Shape.rect) {
                return rectOverlapsRect(this, other, offset);
            } else if (other.shape == Shape.grid) {
                return rectOverlapsGrid(this, other, offset);
            }
        } else if (shape == Shape.grid) {
            if (other.shape == Shape.rect) {
                return rectOverlapsGrid(other, this, offset);
            } else if (other.shape == Shape.grid) {
                throw new GdxRuntimeException("Grid->Grid overlap checks not supported");
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------

    private final Color DEBUG_COLOR = new Color(1f, 0f, 0f, 0.75f);

    public void render(ShapeDrawer shapes) {
        var position = VectorPool.int2.obtain();
        if (Mappers.positions.has(entity)) {
            position.set(Mappers.positions.get(entity).position());
        }

        shapes.setColor(DEBUG_COLOR);
        if (shape == Shape.rect) {
            var x = position.x + origin.x + rect.x;
            var y = position.y + origin.y + rect.y;
            shapes.rectangle(x, y, rect.w, rect.h);
        } else if (shape == Shape.grid) {
            var rect = RectI.pool.obtain();
            for (int x = 0; x < grid.cols; x++) {
                for (int y = 0; y < grid.rows; y++) {
                    if (!grid.cells[x + y * grid.cols]) continue;
                    rect.set(
                            position.x + origin.x + x * grid.tileSize,
                            position.y + origin.y + y * grid.tileSize,
                            grid.tileSize, grid.tileSize
                    );
                    shapes.rectangle(rect.x, rect.y, rect.w, rect.h);
                }
            }
            RectI.pool.free(rect);
        }
        shapes.setColor(Color.WHITE);

        VectorPool.int2.free(position);
    }

    // ------------------------------------------------------------------------

    private static boolean rectOverlapsRect(Collider a, Collider b, Point offset) {
        var a_position = VectorPool.int2.obtain();
        var b_position = VectorPool.int2.obtain();
        if (Mappers.positions.has(a.entity)) {
            a_position.set(Mappers.positions.get(a.entity).position());
        }
        if (Mappers.positions.has(b.entity)) {
            b_position.set(Mappers.positions.get(b.entity).position());
        }

        var rectA = RectI.pool.obtain().set(
                a_position.x + a.origin.x + a.rect.x + offset.x,
                a_position.y + a.origin.y + a.rect.y + offset.y,
                a.rect.w, a.rect.h
        );
        var rectB = RectI.pool.obtain().set(
                b_position.x + b.origin.x + b.rect.x,
                b_position.y + b.origin.y + b.rect.y,
                b.rect.w, b.rect.h
        );

        var overlap = rectA.overlaps(rectB);
        RectI.pool.free(rectA);
        RectI.pool.free(rectB);
        VectorPool.int2.free(a_position);
        VectorPool.int2.free(b_position);
        return overlap;
    }

    private static boolean rectOverlapsGrid(Collider a, Collider b, Point offset) {
        var a_position = VectorPool.int2.obtain();
        var b_position = VectorPool.int2.obtain();
        if (Mappers.positions.has(a.entity)) {
            a_position.set(Mappers.positions.get(a.entity).position());
        }
        if (Mappers.positions.has(b.entity)) {
            b_position.set(Mappers.positions.get(b.entity).position());
        }

        // get a relative rectangle to the grid
        var rect = RectI.pool.obtain().set(
                a.origin.x + a.rect.x + a_position.x + offset.x - b_position.x,
                a.origin.y + a.rect.y + a_position.y + offset.y - b_position.y,
                a.rect.w,
                a.rect.h
        );

        // first do a sanity check that the Rect is within the bounds of the Grid
        var gridBounds = RectI.pool.obtain().set(
                b.origin.x,
                b.origin.y,
                b.grid.cols * b.grid.tileSize,
                b.grid.rows * b.grid.tileSize
        );

        if (!rect.overlaps(gridBounds)) {
            RectI.pool.free(rect);
            RectI.pool.free(gridBounds);
            VectorPool.int2.free(a_position);
            VectorPool.int2.free(b_position);
            return false;
        }

        // get the cells the rectangle overlaps
        int left   = Calc.clampInt((int) Calc.floor  (rect.left()   / (float) b.grid.tileSize), 0, b.grid.cols);
        int right  = Calc.clampInt((int) Calc.ceiling(rect.right()  / (float) b.grid.tileSize), 0, b.grid.cols);
        int top    = Calc.clampInt((int) Calc.ceiling(rect.top()    / (float) b.grid.tileSize), 0, b.grid.rows);
        int bottom = Calc.clampInt((int) Calc.floor  (rect.bottom() / (float) b.grid.tileSize), 0, b.grid.rows);

        // check each cell
        for (int x = left; x < right; x++) {
            for (int y = bottom; y < top; y++) {
                var cellFilled = b.grid.cells[x + y * b.grid.cols];
                if (cellFilled) {
                    RectI.pool.free(rect);
                    RectI.pool.free(gridBounds);
                    VectorPool.int2.free(a_position);
                    VectorPool.int2.free(b_position);
                    return true;
                }
            }
        }

        // all cells were empty
        RectI.pool.free(rect);
        RectI.pool.free(gridBounds);
        VectorPool.int2.free(a_position);
        VectorPool.int2.free(b_position);
        return false;
    }


}
