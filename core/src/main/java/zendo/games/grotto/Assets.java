package zendo.games.grotto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import space.earlygrey.shapedrawer.ShapeDrawer;
import zendo.games.grotto.sprites.Content;

public class Assets extends Content implements Disposable {

    public enum Load { ASYNC, SYNC }

    public boolean initialized;

    public SpriteBatch batch;
    public ShapeDrawer shapes;
    public AssetManager mgr;
    public TextureAtlas atlas;

    public GlyphLayout layout;
    public BitmapFont font;
    public BitmapFont smallFont;
    public BitmapFont largeFont;

    public Texture pixel;
    public TextureRegion pixelRegion;

    public Assets() {
        this(Load.SYNC);
    }

    public Assets(Load load) {
        initialized = false;

        // pack texture atlas from sprites folder
        {
            var packJson = Gdx.files.local("../sprites/pack.json").readString();
            var settings = (new Json()).fromJson(TexturePacker.Settings.class, packJson);
            TexturePacker.processIfModified(settings, "../sprites", "sprites", "sprites");
        }

        // create a single pixel texture and associated region
        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        {
            pixmap.setColor(Color.WHITE);
            pixmap.drawPixel(0, 0);
            pixel = new Texture(pixmap);
        }
        pixmap.dispose();
        pixelRegion = new TextureRegion(pixel);

        // generate fonts
        {
            final int baseSize = 20;

            var fontFile = Gdx.files.internal("fonts/outfit-medium.ttf");
            var generator = new FreeTypeFontGenerator(fontFile);
            var parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = baseSize;
            parameter.color = Color.WHITE;
            parameter.borderColor = Color.DARK_GRAY;
            parameter.shadowColor = Color.BLACK;
            parameter.borderWidth = 2;
            parameter.shadowOffsetX = 1;
            parameter.shadowOffsetY = 2;
            font = generator.generateFont(parameter);

            parameter.size = baseSize / 2;
            smallFont = generator.generateFont(parameter);

            parameter.size = 2 * baseSize;
            largeFont = generator.generateFont(parameter);

            generator.dispose();
        }
        layout = new GlyphLayout();

        batch = new SpriteBatch();
        shapes = new ShapeDrawer(batch, pixelRegion);

        mgr = new AssetManager();
        {
            mgr.load("sprites/sprites.atlas", TextureAtlas.class);
            mgr.load("gui/uiskin.json", Skin.class);

            // textures ---------------------------------------------
//            var param = new TextureLoader.TextureParameter();
//            param.minFilter = Texture.TextureFilter.MipMapLinearLinear;
//            param.magFilter = Texture.TextureFilter.MipMapLinearLinear;
//            param.wrapU = Texture.TextureWrap.Repeat;
//            param.wrapV = Texture.TextureWrap.Repeat;
//            param.genMipMaps = true;
        }

        if (load == Load.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1;

        atlas = mgr.get("sprites/sprites.atlas");

        // load aseprite sprites from an atlas and json definitions
        var aseAtlas = new TextureAtlas("sprites/aseprites.atlas");
        var spritesDir = Gdx.files.internal("sprites");
        for (var fileHandle : spritesDir.list(".json")) {
            sprites.add(Content.loadSprite(fileHandle.path(), aseAtlas));
        }

        initialized = true;
        return 1;
    }

    @Override
    public void dispose() {
        mgr.dispose();
        batch.dispose();
        pixel.dispose();
        font.dispose();
        smallFont.dispose();
        largeFont.dispose();
    }

    public static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));
        String log = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + log);
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + log);
        } else if (Config.Debug.shaders) {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + log);
        }

        return shaderProgram;
    }

}
