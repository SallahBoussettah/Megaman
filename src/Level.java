import java.util.ArrayList;
import java.util.List;

public class Level {
    // Level dimensions
    private int width;
    private int height;
    
    // Level objects
    private List<Tile> tiles;
    private List<Metall> metalls;
    
    public Level(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new ArrayList<>();
        this.metalls = new ArrayList<>();
    }
    
    public void createLevel(Player player) {
        // Create a longer level with multiple platforms and obstacles
        
        // Main floor
        for (int i = 0; i < width / Game.TILE_SIZE; i++) {
            // Create gaps in the floor for challenge
            if ((i > 15 && i < 18) || (i > 30 && i < 33) || (i > 45 && i < 48)) {
                continue;
            }
            addTile(i * Game.TILE_SIZE, height - Game.TILE_SIZE * 2);
        }
        
        // Platforms
        // Platform 1
        for (int i = 5; i < 10; i++) {
            addTile(i * Game.TILE_SIZE, height - Game.TILE_SIZE * 5);
        }
        
        // Platform 2
        for (int i = 15; i < 20; i++) {
            addTile(i * Game.TILE_SIZE, height - Game.TILE_SIZE * 7);
        }
        
        // Platform 3
        for (int i = 25; i < 30; i++) {
            addTile(i * Game.TILE_SIZE, height - Game.TILE_SIZE * 5);
        }
        
        // Platform 4
        for (int i = 35; i < 40; i++) {
            addTile(i * Game.TILE_SIZE, height - Game.TILE_SIZE * 7);
        }
        
        // Platform 5
        for (int i = 45; i < 50; i++) {
            addTile(i * Game.TILE_SIZE, height - Game.TILE_SIZE * 5);
        }
        
        // Vertical walls - reduced height as requested
        for (int i = 0; i < 4; i++) {
            addTile(Game.TILE_SIZE * 12, height - Game.TILE_SIZE * (3 + i));
        }
        
        for (int i = 0; i < 3; i++) {
            addTile(Game.TILE_SIZE * 22, height - Game.TILE_SIZE * (3 + i));
        }
        
        for (int i = 0; i < 4; i++) {
            addTile(Game.TILE_SIZE * 42, height - Game.TILE_SIZE * (3 + i));
        }
        
        // Ceiling in some areas
        for (int i = 13; i < 22; i++) {
            addTile(i * Game.TILE_SIZE, Game.TILE_SIZE * 3);
        }
        
        for (int i = 43; i < 50; i++) {
            addTile(i * Game.TILE_SIZE, Game.TILE_SIZE * 3);
        }
        
        // Add enemies throughout the level
        addMetall(Game.TILE_SIZE * 8, height - Game.TILE_SIZE * 6);
        addMetall(Game.TILE_SIZE * 17, height - Game.TILE_SIZE * 8);
        addMetall(Game.TILE_SIZE * 27, height - Game.TILE_SIZE * 6);
        addMetall(Game.TILE_SIZE * 37, height - Game.TILE_SIZE * 8);
        addMetall(Game.TILE_SIZE * 47, height - Game.TILE_SIZE * 6);
        
        // Add enemies on the ground
        addMetall(Game.TILE_SIZE * 10, height - Game.TILE_SIZE * 3);
        addMetall(Game.TILE_SIZE * 20, height - Game.TILE_SIZE * 3);
        addMetall(Game.TILE_SIZE * 30, height - Game.TILE_SIZE * 3);
        addMetall(Game.TILE_SIZE * 40, height - Game.TILE_SIZE * 3);
    }
    
    private void addTile(int x, int y) {
        tiles.add(new Tile(x, y));
    }
    
    private void addMetall(int x, int y) {
        metalls.add(new Metall(x, y));
    }
    
    public List<Tile> getTiles() {
        return tiles;
    }
    
    public List<Metall> getMetalls() {
        return metalls;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
} 