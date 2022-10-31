import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

public class WFC
{
    // the different types of tiles
    public static enum Tiles
    {
        Sea, Coast, Land
    }

    // stores the connections different tiles can have
    private static class TileConnections
    {
        public Tiles[] left;
        public Tiles[] right;
        public Tiles[] up;
        public Tiles[] down;

        // the constructor
        public TileConnections(Tiles[] left, Tiles[] right, Tiles[] up, Tiles[] down)
        {
            this.left = left;
            this.right = right;
            this.up = up;
            this.down = down;
        }

        // gets the rule for the direction
        public Tiles[] GetDir(Neighbor.Directions dir)
        {
            if (dir == Neighbor.Directions.Left)  return left;
            if (dir == Neighbor.Directions.Right) return right;
            if (dir == Neighbor.Directions.Up)    return up;
            return down;
        }
    }

    // stores information on a neighboring cell
    private static class Neighbor
    {
        // directions
        public static enum Directions
        {
            Left,
            Right,
            Up,
            Down
        }

        // the direction and tile options
        public ArrayList<Tiles> tileOptions;
        public Directions dir;

        // the constructor
        public Neighbor(ArrayList<Tiles> tileOptions, Directions dir)
        {
            this.tileOptions = tileOptions;
            this.dir = dir;
        }
    }

    // gets the neighbor for a direction and point
    private static Neighbor GetNeighbor(ArrayList<Tiles>[][] cells, int x, int y, Neighbor.Directions dir)
    {
        // checking if the point is in bounds on the x and y axises
        boolean inX = x >= 0 && x < GRID_X;
        boolean inY = y >= 0 && y < GRID_Y;

        // if the points in bounds return the neighboring point there
        if (inX && inY) return new Neighbor(cells[x][y], dir);
        // if not in bounds return a point with all options sense it shouldn't change the tile options
        else return new Neighbor(allTiles, dir);
    }

    public static String GetColored(String text, String color, String background) {return color + background + text + "\u001B[0m";}
    public static String GetColored(String text, String color) {return color + text + "\u001B[0m";}

    // base colors
    public static final String BLACK  = "\u001B[30m";
    public static final String RED	  = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN   = "\u001B[36m";
    public static final String WHITE  = "\u001B[37m";

    // background colors
    public static final String BLACK_BACKGROUND  = "\u001B[40m";
    public static final String RED_BACKGROUND    = "\u001B[41m";
    public static final String GREEN_BACKGROUND  = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND   = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND   = "\u001B[46m";
    public static final String WHITE_BACKGROUND  = "\u001B[47m";

    // some constants
    private static final int GRID_X = 25;
    private static final int GRID_Y = 25;

    // an array list with all the tiles already in it
    final static ArrayList<Tiles> allTiles = new ArrayList<>(Arrays.asList(Tiles.Sea, Tiles.Coast, Tiles.Land));

    public static void main(String[] args)
    {
        // the rules for connection between multiple tiles
        HashMap<Tiles, TileConnections> connectionRules = new HashMap<>();
        HashMap<Tiles, Tiles[]> requiredConnections = new HashMap<>();  // for required tiles to be touched

        // setting up the connection rules for sea tiles
        connectionRules.put(Tiles.Sea, new TileConnections(
                new Tiles[] {Tiles.Sea, Tiles.Coast},  // left
                new Tiles[] {Tiles.Sea, Tiles.Coast},  // right
                new Tiles[] {Tiles.Sea, Tiles.Coast},  // up
                new Tiles[] {Tiles.Sea, Tiles.Coast}   // down
        ));
        // setting up the connection rules for coast tiles
        connectionRules.put(Tiles.Coast, new TileConnections(
                new Tiles[] {Tiles.Sea, Tiles.Coast, Tiles.Land},
                new Tiles[] {Tiles.Sea, Tiles.Coast, Tiles.Land},
                new Tiles[] {Tiles.Sea, Tiles.Coast},
                new Tiles[] {Tiles.Sea, Tiles.Coast, Tiles.Land}
        ));
        // setting up the connection rules for land tiles
        connectionRules.put(Tiles.Land, new TileConnections(
                new Tiles[] {Tiles.Coast, Tiles.Land},
                new Tiles[] {Tiles.Coast, Tiles.Land},
                new Tiles[] {Tiles.Coast, Tiles.Land},
                new Tiles[] {Tiles.Coast, Tiles.Land}
        ));
        
        // setting up the required connections for the different tiles
        requiredConnections.put(Tiles.Sea, new Tiles[] {Tiles.Sea});
        requiredConnections.put(Tiles.Coast, new Tiles[] {Tiles.Sea, Tiles.Land});
        requiredConnections.put(Tiles.Land, new Tiles[] {});

        // the grid for the wave function collapse
        ArrayList<Tiles>[][] cells = new ArrayList[GRID_X][GRID_Y];

        // filling in the grid with all tiles to start
        for (int x = 0; x < GRID_X; x++)
        {
            for (int y = 0; y < GRID_Y; y++)
            {
                // filling in the cell with all the tiles (narrowed down later)
                cells[x][y] = (ArrayList<Tiles>) allTiles.clone();
            }
        }

        // looping over each cell, finding the lowest entropy, and updating the rest of the tiles
        for (int i = 0; i < GRID_X * GRID_Y; i++)
        {
            // looking through the cells and finding one with the minimum entropy
            int lowestEntropy = allTiles.size();
            ArrayList<int[]> lowestEntropyCells = new ArrayList<>();

            // looping through all cells and adding up all tiles with the lowest entropy
            for (int x = 0; x < GRID_X; x++)
            {
                for (int y = 0; y < GRID_Y; y++) {
                    // checking the entropy
                    int entropy = cells[x][y].size();

                    if (entropy > 1)
                    {
                        // checking if the entropy is lower than the rest
                        if (entropy < lowestEntropy)
                        {
                            // adding the entropy
                            lowestEntropy = entropy;
                            // emptying the array list of the other higher entropy items
                            lowestEntropyCells = new ArrayList<>(Collections.singletonList(new int[]{x, y}));
                        }
                        else if (entropy == lowestEntropy) lowestEntropyCells.add(new int[]{x, y});
                    }
                }
            }

            if (lowestEntropyCells.size() == 0) continue;

            // finding a random one of the lowest entropy cells and filling it with one of its options
            int randomIndex = (int) Math.round(Math.random() * (lowestEntropyCells.size() - 1));

            // getting the cells position
            int[] cellPos = lowestEntropyCells.get(randomIndex);

            // getting the cell and the random tile and filling the cell with that tile
            ArrayList<Tiles> randomCell = cells[cellPos[0]][cellPos[1]];
            Tiles randomTile = randomCell.get((int) Math.round(Math.random() * (randomCell.size() - 1)));
            cells[cellPos[0]][cellPos[1]] = new ArrayList<>(Collections.singletonList(randomTile));

            // looping through all cells and updating their valid tile options
            for (int x = 0; x < GRID_X; x++)
            {
                for (int y = 0; y < GRID_Y; y++)
                {
                    // getting the cell
                    ArrayList<Tiles> cell = cells[x][y];

                    // checking if the cell has already been found
                    if (cell.size() > 1)
                    {
                        // looping through all the tile options and verifying them
                        ArrayList<Tiles> newTiles = new ArrayList<>();
                        for (Tiles tileOption : cell)
                        {
                            // getting the neighboring cells and their directions
                            Neighbor[] neighbors = new Neighbor[4];
                            neighbors[0] = GetNeighbor(cells, x - 1, y, Neighbor.Directions.Left);
                            neighbors[1] = GetNeighbor(cells, x + 1, y, Neighbor.Directions.Right);
                            neighbors[2] = GetNeighbor(cells, x, y - 1, Neighbor.Directions.Up);
                            neighbors[3] = GetNeighbor(cells, x, y + 1, Neighbor.Directions.Down);

                            // getting the connection rules
                            TileConnections tileConnectionRules = connectionRules.get(tileOption);

                            // looping through the neighbors
                            boolean validOption = true;  // set to false if connection rule not met
                            for (Neighbor neighbor : neighbors)
                            {
                                // getting the connection rules for the direction
                                Tiles[] connections = tileConnectionRules.GetDir(neighbor.dir);

                                // looping through the connection tiles and checking if it contains any of the required tiles
                                boolean containsTileRule = false;
                                for (Tiles tile : connections)
                                {
                                    // checking if the neighbor contains the tile for the connection rule
                                    if (neighbor.tileOptions.contains(tile))
                                    {
                                        // the connection rule was met for the neighbor
                                        containsTileRule = true;
                                        break;
                                    }
                                }

                                // checking if the neighbor is missing the connection rules
                                if (!containsTileRule)
                                {
                                    // the tile option is invalid
                                    validOption = false;
                                    break;
                                }
                            }

                            // checking if the tile has any required tiles to touch
                            for (Tiles requiredTile : requiredConnections.get(tileOption))
                            {
                                // looping through the neighbors and checking for the tile
                                boolean found = false;
                                for (Neighbor neighbor : neighbors)
                                {
                                    // checking if the neighbor has the required tile
                                    if (neighbor.tileOptions.contains(requiredTile))
                                    {
                                        // the neighbor has the tile
                                        found = true;
                                        break;
                                    }
                                }

                                // making sure the tile has the required tiles
                                if (!found) validOption = false;
                            }

                            // checking to make sure neighbors have their required tiles met

                            // adding the tile option if its valid
                            if (validOption) newTiles.add(tileOption);
                        }

                        // updating the cell with the new options
                        cells[x][y] = newTiles;
                    }
                }
            }
        }

        // printing the final array
        for (int x = 0; x < GRID_X; x++)
        {
            for (int y = 0; y < GRID_Y; y++)
            {
                //System.out.print(cells[x][y].size() + ", ");
                if      (cells[x][y].get(0) == Tiles.Land ) System.out.print(GetColored("##", GREEN));
                else if (cells[x][y].get(0) == Tiles.Coast) System.out.print(GetColored("##", YELLOW));
                else if (cells[x][y].get(0) == Tiles.Sea  ) System.out.print(GetColored("##", BLUE));
            }
            System.out.println("");
        }
    }
}
