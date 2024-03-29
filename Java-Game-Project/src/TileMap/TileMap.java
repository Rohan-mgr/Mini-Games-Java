package TileMap;

import GameState.GameStateManager;
import GameState.MenuState;
import Main.GamePanel;
import Main.Main;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;


public class TileMap {
    //position
    private double x;
    private double y;
    // bounds
    private int xmin;
    private int ymin;
    private int xmax;
    private int ymax;
    private double tween;
    // map
    private int[][] map;
    private int tileSize;
    private int numRows;
    private int numCols;
    private int width;
    private int height;
    //tileset
    private BufferedImage tileset;
    private int numTilesAcross;
    private Tile[][] tiles;
    //drawing
    private int rowOffset;
    private int colOffset;
    private int numRowsToDraw;
    private int numColsToDraw;
    private JFrame jframe;
    public TileMap(int tileSize, JFrame window) {
        this.tileSize = tileSize;
        this.jframe = window;
        numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
        numColsToDraw = GamePanel.WIDTH / tileSize + 2;
        tween = 0.07;
    }
    public void loadTiles(String s) {
        try{
            tileset = ImageIO.read(getClass().getResourceAsStream(s));
            numTilesAcross = tileset.getWidth() / tileSize;
            tiles = new Tile[2][numTilesAcross];
            BufferedImage subimage;
            for(int col = 0; col < numTilesAcross; col++){
                subimage = tileset.getSubimage(col*tileSize, 0, tileSize, tileSize);
                tiles[0][col] = new Tile(subimage, Tile.NORMAL);
                subimage = tileset.getSubimage(col*tileSize, tileSize, tileSize, tileSize);
                tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String s) {
        try {
            InputStream in = getClass().getResourceAsStream(s);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            numCols = Integer.parseInt(br.readLine());
            numRows = Integer.parseInt(br.readLine());

            map = new int[numRows][numCols];
            width = numCols * tileSize;
            height = numRows * tileSize;

            xmin = GamePanel.WIDTH - width;
            xmax = 0;
            ymin = GamePanel.HEIGHT - height;
            ymax = 0;

            String delims = "\\s+";
            for(int row = 0; row < numRows; row++){
                String line = br.readLine();
                String[] tokens = line.split(delims);
                for(int col = 0; col < numCols; col++) {
                    map[row][col] = Integer.parseInt(tokens[col]);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTileSize() { return tileSize;};
    public double getx() { return x;};
    public double gety() { return y;};
    public int getWidth() { return width;};
    public  int getHeight() { return height;};

    private boolean hasConditionRun = false;
    public int getType(int row, int col) {
        // Check if the condition has already run, if yes, return a default value
        if (hasConditionRun) {
            return Tile.NORMAL; // Adjust this based on your needs
        }

        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            System.out.println("pranit crashed");

            // Set the flag to true to indicate that the condition has been triggered
            hasConditionRun = true;
            this.jframe.dispose();
            Main dragonGame = new Main();
            dragonGame.main(null);
            // Return a default value or handle the out-of-bounds case accordingly
            return Tile.NORMAL; // Adjust this based on your needs
        }

        int rc = map[row][col];
        int r = rc / numTilesAcross;
        int c = rc % numTilesAcross;

        // Check if indices are within bounds for the tiles array
        if (r >= 0 && r < tiles.length && c >= 0 && c < tiles[0].length) {
            return tiles[r][c].getType();
        }

        // Return a default value or handle the case where tiles array indices are out of bounds
        return Tile.NORMAL; // Adjust this based on your needs
    }


    public void setTween(double d) { tween = d;};

    public void setPosition(double x, double y) {
        this.x += (x-this.x) * tween;
        this.y += (y-this.y) * tween;
        fixBounds();
        colOffset = (int) - this.x / tileSize;
        rowOffset = (int) - this.y / tileSize;

    }

    public void fixBounds() {
        if(x < xmin) x = xmin;
        if(y < ymin) y = ymin;
        if(x > xmax) x = xmax;
        if(y > ymax) y = ymax;
    }

    public void draw(Graphics2D g) {
        for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
            if(row >= numRows) break;
            for(int col = colOffset; col < colOffset + numColsToDraw; col++) {
                if(col >= numCols) break;
                if(map[row][col] == 0) continue;
                int rc = map[row][col];
                int r = rc / numTilesAcross;
                int c = rc % numTilesAcross;

                g.drawImage(tiles[r][c].getImage(), (int)x + col * tileSize, (int)y + row * tileSize, null);
            }
        }
    }
}
