package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import zendo.games.grotto.Config;
import zendo.games.grotto.utils.Point;

public class Tilemap implements Component {

    private static final String TAG = Tilemap.class.getSimpleName();

    private int tileSize;
    private int rows;
    private int cols;

    protected TextureRegion[] grid;

    public Point offset;

    public Tilemap(int tileSize, int cols, int rows) {
        this.tileSize = tileSize;
        this.cols = cols;
        this.rows = rows;
        this.grid = new TextureRegion[rows * cols];
        this.offset = Point.zero();
    }

    public int tileSize() {
        return tileSize;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public void setCell(int x, int y, TextureRegion texture) {
        if (x < 0 || y < 0 || x >= cols || y >= rows) {
            if (Config.Debug.general) {
                Gdx.app.log(TAG, String.format("Tilemap indices out of bounds: (%d, %d)", x, y));
            }
            return;
        }
        grid[x + y * cols] = texture;
    }

    public void setCells(int x, int y, int w, int h, TextureRegion texture) {
        if (x < 0 || y < 0 || x + w > cols || y + h > rows) {
            if (Config.Debug.general) {
                Gdx.app.log(TAG, String.format("Tilemap indices out of bounds: (%d, %d : %d, %d)", x, y, w, h));
            }
            return;
        }
        for (int ix = x; ix < x + w; ix++) {
            for (int iy = y; iy < y + h; iy++) {
                grid[ix + iy * cols] = texture;
            }
        }
    }

    public TextureRegion getCell(int x, int y) {
        if (x < 0 || y < 0 || x >= cols || y >= rows) {
            throw new GdxRuntimeException(String.format("Tilemap indices out of bounds: (%d, %d)", x, y));
        }
        return grid[x + y * cols];
    }

    public void render(SpriteBatch batch, Point origin) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                var texture = grid[x + y * cols];
                if (texture == null) {
                    continue;
                }
                batch.draw(texture,
                        origin.x + x * tileSize + offset.x,
                        origin.y + y * tileSize + offset.y,
                        tileSize, tileSize);
            }
        }
    }

}
