public class Tile extends GameObject {
    public Tile(int x, int y) {
        super(x, y, Game.TILE_SIZE, Game.TILE_SIZE);
        this.image = ResourceManager.floorTileImage;
    }
} 