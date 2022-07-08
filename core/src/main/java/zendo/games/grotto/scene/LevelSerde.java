package zendo.games.grotto.scene;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import zendo.games.grotto.Assets;
import zendo.games.grotto.Config;
import zendo.games.grotto.scene.components.*;

import java.nio.charset.StandardCharsets;

public class LevelSerde {

    private static final String TAG = LevelSerde.class.getSimpleName();

    // TODO - decide how to store the texture region info... texture name plus u,v,w,h from region?
    @NoArgsConstructor
    @AllArgsConstructor
    static class TileInfo {
        int x;
        int y;
        boolean blocking;
    }
    @NoArgsConstructor
    @AllArgsConstructor
    static class LevelInfo {
        int cols;
        int rows;
        Array<TileInfo> tileInfos;
    }

    public static void saveLevel(Entity entity, String filename) {
        // get the relevant components
        var tilemap = Mappers.tilemaps.get(entity);
        var collider = Mappers.colliders.get(entity);

        // sanity checks
        if (tilemap == null || collider == null) {
            throw new GdxRuntimeException("Unable to save level, entity is missing required components [tilemap, collider]");
        }
        if (collider.shape() != Collider.Shape.grid) {
            throw new GdxRuntimeException("Unable to save level, entity collider component is not of shape 'grid'");
        }
        if (collider.grid().cols != tilemap.cols() || collider.grid().rows != tilemap.rows()) {
            var componentSizesStr = String.format("tilemap(%d, %d) vs collider(%d, %d)",
                    tilemap.cols(), tilemap.rows(), collider.grid().cols, collider.grid().rows);
            throw new GdxRuntimeException("Unable to save level, entity collider and tilemap sizes don't match: " + componentSizesStr);
        }

        // serialize level data
        var cols = collider.grid().cols;
        var rows = collider.grid().rows;
        var tileInfos = new Array<TileInfo>(cols * rows);
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                var cell = collider.getCell(x, y);
                var tile = tilemap.getCell(x, y);
                tileInfos.add(new TileInfo(x, y, cell));
            }
        }
        var levelInfo = new LevelInfo(cols, rows, tileInfos);

        // convert to json
        var json = new Json(JsonWriter.OutputType.json);
        var jsonData = json.prettyPrint(levelInfo);

        // write to file system
        var path = "levels/" + filename;
        var file = Gdx.files.getFileHandle(path, Files.FileType.Local);
        file.writeString(jsonData, false, StandardCharsets.UTF_8.name());
        Gdx.app.log(TAG, "Saved level data to '" + file.path()  + "'");
    }

    public static Entity loadLevel(String filename, Assets assets, Engine engine) {
        var path = "levels/" + filename;
        var file = Gdx.files.local(path);
        if (!file.exists() || file.isDirectory()) {
            throw new GdxRuntimeException("Unable to load level '" + path + "', file does not exist or is not a valid file");
        }

        var json = new Json();
        var jsonData = file.readString(StandardCharsets.UTF_8.name());
        var levelData = json.fromJson(LevelInfo.class, jsonData);
        if (levelData.tileInfos.isEmpty()) {
            throw new GdxRuntimeException("Unable to load level '" + file.path() + "', tile info is empty");
        }

        // unpack level data into an entity
        var entity = engine.createEntity();
        {
            var name = new Name("map");
            var map = new Map();

            var width = Config.Screen.framebuffer_width;
            var height = Config.Screen.framebuffer_height;
            var bounds = new Boundary(width, height);

            var tileSize = 8;
            var cols = levelData.cols;
            var rows = levelData.rows;
            var tilemap = new Tilemap(tileSize, cols, rows);
            var collider = Collider.makeGrid(entity, tileSize, cols, rows);
            collider.mask = Collider.Mask.solid;

            for (var tileInfo : levelData.tileInfos) {
                if (tileInfo.blocking) {
                    tilemap.setCell(tileInfo.x, tileInfo.y, assets.pixelRegion);
                    collider.setCell(tileInfo.x, tileInfo.y, true);
                }
            }

            var recti = bounds.rect();
            var rect = new Rectangle(recti.x, recti.y, recti.w, recti.h);
            var shape = new Shape(rect);

            entity.add(name);
            entity.add(map);
            entity.add(tilemap);
            entity.add(collider);
            entity.add(bounds);
            entity.add(shape);
        }
        return entity;
    }

}
