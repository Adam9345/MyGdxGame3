package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Assets;

public class Rock extends AbstractGameObject {

    private TextureRegion regEdge;
    private TextureRegion regMiddle;

    private int length;

    public Rock() {
        init();
    }

    private void init() {
        dimension.set(1, 1.5f);
        regEdge = Assets.instance.rock.edge;
        regMiddle = Assets.instance.rock.middle;
        setLength(1);
    }

    private void setLength(int length) {
        this.length = length;
    }

    public void increaseLength(int amount) {
        setLength(length + amount);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion reg = null;

        float relX = 0;
        float relY = 0;

        reg = regEdge;
        relX -= dimension.x / 4;
        batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
                origin.x, origin.y, dimension.x / 4, dimension.y, scale.x,
                scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(), false, false);

        relX = 0;
        reg = regMiddle;

        for (int i = 0; i < length; i++) {
            batch.draw(reg.getTexture(), position.x + relX,
                    position.y + relY, origin.x, origin.y, dimension.x, dimension.y, scale.x,
                    scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(), false, false);

            relX += dimension.x;
        }

        reg = regEdge;
        batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
                origin.x + dimension.x / 8, origin.y, dimension.x / 4,
                dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
                reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight()
                , true, false);
    }
}
