package zendo.games.grotto;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {

	Assets assets;
	TextureRegion logo;

	@Override
	public void create() {
		assets = new Assets();
		logo = assets.atlas.findRegion("libgdx");
	}

	@Override
	public void render() {
		ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

		var batch = assets.batch;
		batch.begin();
		batch.draw(logo,
				0.5f * (Gdx.graphics.getWidth()  - logo.getRegionWidth()),
				0.5f * (Gdx.graphics.getHeight() - logo.getRegionHeight()));
		batch.end();
	}

	@Override
	public void dispose() {
		assets.dispose();
	}
}