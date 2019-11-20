package com.mhunter.game.map;

import com.mhunter.game.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;


public class Map {

    public enum BlockType {
        EMPTY, WALL

    }

    public static final int CELL_SIZE = 80;
    public static final int MAP_SIZE_X = 20;
    public static final int MAP_SIZE_X_PX = MAP_SIZE_X * CELL_SIZE;
    public static final int MAP_SIZE_Y = 18;
    public static final int MAP_SIZE_Y_PX = MAP_SIZE_Y * CELL_SIZE;


    private BlockType[][] data;
    private int[][] treeType;
    private TextureRegion[] textureWall;
    private TextureRegion textureGrass;
    private TextureRegion texturePortal;


    private  int outX, outY;



    public Map() {
        this.data = new BlockType[MAP_SIZE_X][MAP_SIZE_Y];
        this.treeType = new int[MAP_SIZE_X][MAP_SIZE_Y];
        this.textureGrass = Assets.getInstance().getAtlas().findRegion("Grass");
        this.texturePortal = Assets.getInstance().getAtlas().findRegion("MagicPortalTwo");
        this.textureWall = new TextureRegion(Assets.getInstance().getAtlas().findRegion("trees")).split(80, 120)[0];
        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                data[i][j] = BlockType.EMPTY;
                if (MathUtils.random() < 0.18) {
                    data[i][j] = BlockType.WALL;
                    treeType[i][j] = MathUtils.random(0, 4);
                }
            }
        }
        do {
            outX = MathUtils.random(0, MAP_SIZE_X - 1);
            outY = MathUtils.random(0, MAP_SIZE_Y - 1);
        } while (data[outX][outY] != BlockType.EMPTY);
    }

    public void setRefVectorToEmptyPoint(Vector2 refInput) {
        do {
            refInput.set(MathUtils.random(0, Map.MAP_SIZE_X_PX), MathUtils.random(0, Map.MAP_SIZE_Y_PX));
        } while (!isCellPassable(refInput));
    }

    public void renderGround(SpriteBatch batch, int x, int y) {
        batch.draw(textureGrass, x * 80, y * 80);
        if (x == outX && y == outY) {
            batch.draw(texturePortal, x * 80, y * 80);
        }
    }


    public boolean isOut(Vector2 position) {
        return (int) (position.x / 80) == outX && (int) (position.y / 80) == outY;
    }

    public void renderWalls(SpriteBatch batch, int x, int y) {
        if (data[x][y] == BlockType.WALL) {
            batch.draw(textureWall[treeType[x][y]], x * 80, y * 80);
            //batch.draw(textureWall[MathUtils.random(0,4)], x * 80, y * 80);
        }
    }


    public void render(SpriteBatch batch) {
        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = MAP_SIZE_Y - 1; j >= 0; j--) {
                batch.draw(textureGrass, i * CELL_SIZE, j * CELL_SIZE);
                if (data[i][j] == BlockType.WALL) {
                    batch.draw(textureWall[1], i * CELL_SIZE, j * CELL_SIZE);
                }
            }
        }
    }

    public boolean isCellPassable(Vector2 position) {
        if (position.x < 0.0f || position.y < 0.0f || position.x >= MAP_SIZE_X_PX || position.y >=MAP_SIZE_Y_PX) {
            return false;
        }

        int cellX = (int) position.x / CELL_SIZE;
        int cellY = (int) position.y / CELL_SIZE;

        if (cellX < 0 || cellX >= MAP_SIZE_X || cellY < 0 || cellY >= MAP_SIZE_Y) {
            return false;
        }
        return data[cellX][cellY] == BlockType.EMPTY;

    }

    public boolean isCellPassableForProjectiles(Vector2 position) { // позднее планирую внести изменения в метод при добавлении водных преград
        if (position.x < 0.0f || position.y < 0.0f || position.x >= MAP_SIZE_X_PX || position.y >= MAP_SIZE_Y_PX) {
            return false;
        }

        int cellX = (int) position.x / CELL_SIZE;
        int cellY = (int) position.y / CELL_SIZE;

        if (cellX < 0 || cellX >= MAP_SIZE_X || cellY < 0 || cellY >= MAP_SIZE_Y) {
            return false;
        }
        return data[cellX][cellY] == BlockType.EMPTY;

    }


}
