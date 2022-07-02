package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import zendo.games.grotto.sprites.Content;
import zendo.games.grotto.sprites.Sprite;
import zendo.games.grotto.utils.Calc;

public class Animator implements Component {

    public enum LoopMode { none, loop }

    public Vector2 scale;
    public LoopMode mode;
    public float rotation;
    public float speed;

    private Color tint;
    private Sprite sprite;
    private int animationIndex;
    private int frameIndex;
    private float frameCounter;

    public final Entity entity;

    public Animator(Entity entity, String spriteName) {
        this.entity = entity;
        scale = new Vector2(1f, 1f);
        tint = Color.WHITE.cpy();
        mode = LoopMode.loop;
        speed = 1;
        sprite = Content.findSprite(spriteName);
    }

    public Animator(Entity entity, String spriteName, String animationName) {
        this(entity, spriteName);
        play(animationName);
    }

    public void update(float delta) {
        if (!inValidState()) return;

        var anim = sprite.animations.get(animationIndex);
        var frame = anim.frames.get(frameIndex);

        // increment frame counter
        frameCounter += speed * delta;

        // move to next frame after duration
        while (frameCounter >= frame.duration) {
            // reset frame counter
            frameCounter -= frame.duration;

            // increment frame, adjust based on loop mode
            frameIndex++;
            switch (mode) {
                case none -> frameIndex = Calc.clampInt(frameIndex, 0, animation().frames.size() - 1);
                case loop -> {
                    if (frameIndex >= anim.frames.size()) {
                        frameIndex = 0;
                    }
                }
            }
        }

        // lerp scale back to normal
        {
            var facing = 1;
            var sx = Calc.approach(Calc.abs(scale.x), 1f, 4 * delta);
            var sy = Calc.approach(Calc.abs(scale.y), 1f, 4 * delta);
            scale.set(facing * sx, sy);
        }
    }

    public void render(SpriteBatch batch) {
        var position = Mappers.positions.get(entity);
        if (!inValidState()) return;

        var sprite = sprite();
        var anim = sprite.animations.get(animationIndex());
        var frame = anim.frames.get(frameIndex());

        batch.setColor(tint());
        batch.draw(frame.image,
                position.x() - sprite().origin.x,
                position.y() - sprite().origin.y,
                sprite.origin.x,
                sprite.origin.y,
                frame.image.getRegionWidth(),
                frame.image.getRegionHeight(),
                scale.x, scale.y,
                rotation
        );
        batch.setColor(Color.WHITE);
    }

    public void render(ShapeDrawer shapes) {
        var position = Mappers.positions.get(entity);
        if (!inValidState()) return;

        var sprite = sprite();
        var anim = sprite.animations.get(animationIndex());
        var frame = anim.frames.get(frameIndex());
        var lineWidth = 1f;

        // TODO - clunky 'fix' to offset animator bounds when facing changes
        var player = Mappers.players.get(entity);
        var facingAdjust = 1;
        if (player != null) {
            if (player.facing < 0) {
                facingAdjust = -1;
            }
        }

        shapes.setColor(Color.YELLOW);
        shapes.rectangle(
                position.x() - sprite().origin.x,
                position.y() - sprite().origin.y,
                facingAdjust * frame.image.getRegionWidth() * scale.x,
                frame.image.getRegionHeight() * scale.y,
                lineWidth,
                JoinType.SMOOTH
        );
        shapes.setColor(Color.WHITE);
    }

    public Sprite sprite() {
        return sprite;
    }

    public float duration() {
        return (animation() != null) ? animation().duration() : 0f;
    }

    public Sprite.Anim animation() {
        if (sprite != null && animationIndex >= 0 && animationIndex < sprite.animations.size()) {
            return sprite.animations.get(animationIndex);
        }
        return null;
    }

    public int animationIndex() {
        return animationIndex;
    }

    public int frameIndex() {
        return frameIndex;
    }

    public Sprite.Frame frame() {
        return animation().frames.get(frameIndex);
    }

    public Color tint() {
        return tint;
    }

    public float getAlpha() {
        return tint.a;
    }

    public void setAlpha(float a) {
        tint.a = a;
    }

    public void setRGB(float r, float g, float b) {
        tint.set(r, g, b, tint.a);
    }

    public void setColor(float r, float g, float b, float a) {
        tint.set(r, g, b, a);
    }

    public Animator play(String animation) {
        return play(animation, false);
    }

    public Animator play(String animation, boolean restart) {
        if (sprite == null) {
            throw new GdxRuntimeException("No Sprite assigned to animator");
        }

        for (int i = 0; i < sprite.animations.size(); i++) {
            if (sprite.animations.get(i).name.equals(animation)) {
                if (animationIndex != i || restart) {
                    animationIndex = i;
                    frameIndex = 0;
                    frameCounter = 0;
                }
                break;
            }
        }
        return this;
    }

    public boolean inValidState() {
        return (sprite != null
            && animationIndex >= 0
            && animationIndex < sprite.animations.size()
            && frameIndex >= 0
            && frameIndex < sprite.animations.get(animationIndex).frames.size()
        );
    }

}
