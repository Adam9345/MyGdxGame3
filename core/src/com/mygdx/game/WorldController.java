package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.utils.CameraHelper;

import java.security.Key;

public class WorldController extends InputAdapter {
    private static final String TAG = WorldController.class.getName();

    public Sprite[] testSprites;
    public int selectedSprite;
    public CameraHelper cameraHelper;

    public WorldController() {
        init();
    }

    private void init() {
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        initTestObjects();
    }

    @Override
    public boolean keyUp(int keycode) {
        // Resetowanie świata gry

        if(keycode == Input.Keys.R) {
            init();
            Gdx.app.debug(TAG, "Świat gry został zresetowany");
        } else if (keycode == Input.Keys.SPACE) {
            selectedSprite = (selectedSprite + 1) % testSprites.length;

            // Aktualizowanie kamery, aby podążała za zaznaczonym spritem

            if(cameraHelper.hasTarget()){
                cameraHelper.setTarget(testSprites[selectedSprite]);
            }

            Gdx.app.debug(TAG, "Sprite #" + selectedSprite + " zaznaczono");
        } else if (keycode == Input.Keys.ENTER) {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null :
                    testSprites[selectedSprite]);
            Gdx.app.debug(TAG, "Podążanie kamery: " + cameraHelper.hasTarget());
        }

        return false;
    }

    private void initTestObjects() {
        // Stwórz nową tablicę dla 5 spritów
        testSprites = new Sprite[5];

        // Stwórz pusty 8-bitowy Pixmap

        int width = 32;
        int height = 32;

        Pixmap pixmap = createProceduralPixmap(width, height);

        // Stwórz nową teksturę dla Pixmapy

        Texture texture = new Texture(pixmap);

        Array<TextureRegion> regions = new Array<TextureRegion>();
        regions.add(Assets.instance.bunny.head);
        regions.add(Assets.instance.feather.feather);
        regions.add(Assets.instance.goldCoin.goldCoin);

        // Stwórz nowe sprity używając stworzonej tekstury

        for(int i = 0; i < testSprites.length; i++) {
            Sprite spr = new Sprite(regions.random());

            // Ustaw rozmiar sprite'a 1m x 1m w świecie gry

            spr.setSize(1, 1);

            // Ustaw pozycję początkową na środku

            spr.setOrigin(spr.getWidth() / 2, spr.getHeight() / 2);

            // Oblicz losową pozycję dla sprit'a

            float randomX = MathUtils.random(-2.0f, 2.0f);
            float randomY = MathUtils.random(-2.0f, 2.0f);

            // Umieść nowego sprit'a w tablicy

            spr.setPosition(randomX, randomY);

            testSprites[i] = spr;
        }

        // Zaznacz pierwszego sprite'a

        selectedSprite = 0;
    }

    private Pixmap createProceduralPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Wypełnij prostokąt pół przezroczystym czerwonym kolorem

        pixmap.setColor(1, 0, 0, 0.5f);
        pixmap.fill();

        // Rysuj żółty znak X w prostokącie

        pixmap.setColor(1, 1, 0, 1);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);

        // Rysuj linię w kolorze cyan wokół prostokąta

        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width,height);

        return pixmap;
    }

    public void update (float deltaTime) {
        handleDebugInput(deltaTime);
        updateTestObjects(deltaTime);
        cameraHelper.update(deltaTime);
    }

    private void handleDebugInput(float deltaTime) {
        if(Gdx.app.getType() != Application.ApplicationType.Desktop) return;

        float sprMoveSpeed = 5 * deltaTime;

        // Sterowanie zaznaczonym spritem

        if(Gdx.input.isKeyPressed(Input.Keys.A))
            moveSelectedSprite(-sprMoveSpeed, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.D))
            moveSelectedSprite(sprMoveSpeed, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.W))
            moveSelectedSprite(0, sprMoveSpeed);
        if(Gdx.input.isKeyPressed(Input.Keys.S))
            moveSelectedSprite(0, -sprMoveSpeed);


        // Sterowanie kamerą (ruch)

        float camMoveSpeed = 5 * deltaTime;
        float camMoveSpeedAccelerationFactor = 5;

        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            camMoveSpeed *= camMoveSpeedAccelerationFactor;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            moveCamera(-camMoveSpeed, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            moveCamera(camMoveSpeed, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.UP))
            moveCamera(0, camMoveSpeed);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            moveCamera(0, -camMoveSpeed);
        if(Gdx.input.isKeyPressed(Input.Keys.BACKSPACE))
            cameraHelper.setPosition(0, 0);

        // Sterowanie kamerą (zoom)

        float camZoomSpeed = 1 * deltaTime;
        float camZoomSpeedAccelerationFactor = 5;
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            camZoomSpeed *= camZoomSpeedAccelerationFactor;
        if(Gdx.input.isKeyPressed(Input.Keys.COMMA))
            cameraHelper.addZoom(camZoomSpeed);
        if(Gdx.input.isKeyPressed(Input.Keys.PERIOD))
            cameraHelper.addZoom(-camZoomSpeed);
        if(Gdx.input.isKeyPressed(Input.Keys.SLASH))
            cameraHelper.setZoom(1);
    }

    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }

    private void moveSelectedSprite(float x, float y) {
        testSprites[selectedSprite].translate(x, y);
    }

    private void updateTestObjects(float deltaTime) {

        // Pobierz obecną wartość rotacji znaznaczonego sprit'a
        float rotation = testSprites[selectedSprite].getRotation();

        // Obracaj sprit'a o 90 stopni co sekundę

        rotation += 90 * deltaTime;

        // Obracaj do 360 stopni

        rotation %= 360;

        // Ustaw nową wartość rotacji dla zaznaczonego sprit'a

        testSprites[selectedSprite].setRotation(rotation);
    }
}
