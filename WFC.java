import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class WFC
{
    // the different types of tiles
    public static enum Tiles
    {
        Sea, Coast, Land, Forest
    }

    // stores the faces of tiles surrounding a tile
    public static class TileFaces
    {
        public Tiles leftTile;
        public Tiles rightTile;
        public Tiles topTile;
        public Tiles bottomTile;

        public TileFaces(Tiles leftTile, Tiles rightTile, Tiles topTile, Tiles bottomTile)
        {
            this.leftTile   = leftTile;
            this.rightTile  = rightTile;
            this.topTile    = topTile;
            this.bottomTile = bottomTile;
        }

        // checks if two TileFaces are equal
        public boolean Equals(TileFaces otherFace)
        {
            return leftTile == otherFace.leftTile && rightTile == otherFace.rightTile && topTile == otherFace.topTile && bottomTile == otherFace.bottomTile;
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

    // gets the valid options for tiles
    private static ArrayList<Tiles> GetValid(ArrayList<Tiles>[][] cells, int x, int y)
    {
        ArrayList<Tiles> cell;
        if (x < 0 || x >= GRID_X || y < 0 || y >= GRID_Y) cell = (ArrayList<Tiles>) allTiles.clone();
        else cell = cells[x][y];

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

            // getting the rules for the faces
            ArrayList<TileFaces> faces = faceRules.get(tileOption);

            // looping through all the face rules and checking if any of them are met
            boolean validFace = true;
            for (TileFaces face : faces)
            {
                // going through all the neighbors and checking if they have a tile lining up with the face rule
                validFace = true;
                for (Neighbor neighbor : neighbors)
                {
                    // checking if its valid depending on the direction
                    if      (neighbor.dir == Neighbor.Directions.Left  && !neighbor.tileOptions.contains(face.leftTile  )) validFace = false;
                    else if (neighbor.dir == Neighbor.Directions.Right && !neighbor.tileOptions.contains(face.rightTile )) validFace = false;
                    else if (neighbor.dir == Neighbor.Directions.Up    && !neighbor.tileOptions.contains(face.topTile   )) validFace = false;
                    else if (neighbor.dir == Neighbor.Directions.Down  && !neighbor.tileOptions.contains(face.bottomTile)) validFace = false;

                    if (!validFace) break;  // leaving the loop early sense the face is invalid
                }

                // the face is valid stopping early sense the answer is known
                if (validFace) break;
            }

            // another idea, each tile has a set for every combination of valid tiles that can be next to it generated by an image
            // example: land: {{left: grass, right: grass, bot: sand, top: grass}, {ect... for the rest of them}} and verify if the requirement is met
            // allows for more complicated rules in an easier way

            // adding the tile option if its valid
            if (validFace) newTiles.add(tileOption);
        }

        return newTiles;
    }

    public static String GetColored(String text, String color, String background) {return color + background + text + "\u001B[0m";}
    public static String GetColored(String text, String color) {return color + text + "\u001B[0m";}

    // base colors
    private static final String BLACK  = "\u001B[30m";
    private static final String RED	  = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN   = "\u001B[36m";
    private static final String WHITE  = "\u001B[37m";

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
    private static final int GRID_X = 15;
    private static final int GRID_Y = 15;

    // an array list with all the tiles already in it
    final static ArrayList<Tiles> allTiles = new ArrayList<>(Arrays.asList(Tiles.Sea, Tiles.Coast, Tiles.Land, Tiles.Forest));

    private static HashMap<Tiles, ArrayList<TileFaces>> faceRules;

    public static void main(String[] args)
    {
        faceRules = new HashMap<>();

        // the example map that the rules are taken from
        Tiles[][] exampleMap = {
                {Tiles.Land, Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land  , Tiles.Land  , Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land  , Tiles.Land  , Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Land , Tiles.Coast, Tiles.Coast, Tiles.Land , Tiles.Land , Tiles.Coast, Tiles.Land  , Tiles.Land  , Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Coast, Tiles.Sea  , Tiles.Sea  , Tiles.Coast, Tiles.Coast, Tiles.Sea  , Tiles.Coast , Tiles.Land  , Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Coast, Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Coast , Tiles.Land  , Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Coast, Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Coast, Tiles.Land  , Tiles.Land  , Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Coast, Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Coast, Tiles.Land , Tiles.Land  , Tiles.Forest, Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Coast, Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Coast, Tiles.Land , Tiles.Forest, Tiles.Forest, Tiles.Forest, Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Coast, Tiles.Sea  , Tiles.Sea  , Tiles.Sea  , Tiles.Coast, Tiles.Land , Tiles.Forest, Tiles.Forest, Tiles.Forest, Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Land , Tiles.Coast, Tiles.Sea  , Tiles.Coast, Tiles.Land , Tiles.Land , Tiles.Forest, Tiles.Forest, Tiles.Forest, Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Coast, Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Forest, Tiles.Forest, Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land  , Tiles.Forest, Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land  , Tiles.Land  , Tiles.Land  , Tiles.Land, Tiles.Land},
                {Tiles.Land, Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land , Tiles.Land  , Tiles.Land  , Tiles.Land  , Tiles.Land, Tiles.Land}
        };

        // looping through the map looking at the different rules and adding them
        for (int x = 1; x < exampleMap.length-1; x++)
        {
            for (int y = 1; y < exampleMap[x].length-1; y++)
            {
                // getting the tile at the position
                Tiles tile = exampleMap[x][y];

                // checking if the dictionary already added this tile
                if (faceRules.containsKey(tile))
                {
                    // getting the neighboring tiles
                    Tiles leftTile   = exampleMap[x-1][y  ];
                    Tiles rightTile  = exampleMap[x+1][y  ];
                    Tiles topTile    = exampleMap[x  ][y-1];
                    Tiles bottomTile = exampleMap[x  ][y+1];

                    // getting the face
                    TileFaces face = new TileFaces(leftTile, rightTile, topTile, bottomTile);

                    // checking if the dictionary already has this rule
                    ArrayList<TileFaces> faces = faceRules.get(tile);

                    // looping through the face rules in place and checking if this rule is in it
                    boolean added = false;
                    for (TileFaces tileFace : faces)
                    {
                        // checking if the face contains the new face
                        if (tileFace.Equals(face))
                        {
                            // it was added, ending the loop early
                            added = true;
                            break;
                        }
                    }

                    if (!added) faces.add(face);
                }
                else
                {
                    // getting the neighboring tiles
                    Tiles leftTile   = exampleMap[x-1][y  ];
                    Tiles rightTile  = exampleMap[x+1][y  ];
                    Tiles topTile    = exampleMap[x  ][y-1];
                    Tiles bottomTile = exampleMap[x  ][y+1];

                    // getting the face
                    TileFaces face = new TileFaces(leftTile, rightTile, topTile, bottomTile);
                    ArrayList<TileFaces> newArrayList = new ArrayList<>();
                    newArrayList.add(face);

                    // adding the face and tile to the dictionary
                    faceRules.put(tile, newArrayList);
                }
            }
        }

        // printing the rules
        for (Tiles tile : allTiles)
        {
            System.out.println("Rule for " + tile);
            ArrayList<TileFaces> rules = faceRules.get(tile);
            for (TileFaces face : rules)
            {
                System.out.println("Left: " + face.leftTile + ", Right: " + face.rightTile + ", Top: " + face.topTile + ", Bottom: " + face.bottomTile);
            }
            System.out.println("");
        }

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
            /*
            // printing the array
            System.out.println(i + ": ");
            for (int i1 = 0; i1 < GRID_X; i1++)
            {
                for (int i2 = 0; i2 < GRID_Y; i2++)
                {
                    if (cells[i1][i2].contains(Tiles.Land)) System.out.print(GetColored("##", GREEN));
                    else System.out.print("  ");
                    if (cells[i1][i2].contains(Tiles.Coast)) System.out.print(GetColored("##", YELLOW));
                    else System.out.print("  ");
                    if (cells[i1][i2].contains(Tiles.Sea)) System.out.print(GetColored("##", BLUE));
                    else System.out.print("  ");
                    if (cells[i1][i2].contains(Tiles.Forest)) System.out.print(GetColored("##", BLACK));
                    else System.out.print("  ");
                    System.out.print("|");
                }
                System.out.println("");
            }
            System.out.println("");
            */

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

                        // getting the valid options
                        ArrayList<Tiles> validOptions = GetValid(cells, x, y);

                        // the final valid options
                        ArrayList<Tiles> newTiles = new ArrayList<>();

                        // narrowing down the valid options by checking the neighbors requirements
                        for (Tiles option : validOptions)
                        {
                            // getting the original possibilities of the cell
                            ArrayList<Tiles> original = cells[x][y];

                            // setting the cell to the new tile
                            ArrayList<Tiles> newArrayList = new ArrayList<>();
                            newArrayList.add(option);
                            cells[x][y] = newArrayList;

                            // checking if the neighbors can function without that tile
                            ArrayList<Tiles> neighborLeft   = GetValid(cells, x-1, y);
                            ArrayList<Tiles> neighborRight  = GetValid(cells, x+1, y);
                            ArrayList<Tiles> neighborTop    = GetValid(cells, x, y-1);
                            ArrayList<Tiles> neighborBottom = GetValid(cells, x, y+1);

                            cells[x][y] = original;

                            // making sure that the neighbors have at least one item each
                            if (Math.min(Math.min(neighborLeft.size(), neighborRight.size()), Math.min(neighborTop.size(), neighborBottom.size())) > 0) newTiles.add(option);
                        }

                        // updating the cell with the new options
                        if (newTiles.size() > 0) cells[x][y] = newTiles;
                        else  // looking for a valid face to put here sense none of the other were valid
                        {
                            // getting the tiles neighbors
                            ArrayList<Tiles> leftNeighbor   = GetNeighbor(cells, x - 1, y, Neighbor.Directions.Left) .tileOptions;
                            ArrayList<Tiles> rightNeighbor  = GetNeighbor(cells, x + 1, y, Neighbor.Directions.Right).tileOptions;
                            ArrayList<Tiles> topNeighbor    = GetNeighbor(cells, x, y - 1, Neighbor.Directions.Up)   .tileOptions;
                            ArrayList<Tiles> bottomNeighbor = GetNeighbor(cells, x, y + 1, Neighbor.Directions.Down) .tileOptions;

                            System.out.print("Left: " + leftNeighbor + ", ");
                            System.out.print("Right: " + rightNeighbor + ", ");
                            System.out.print("Top: " + topNeighbor + ", ");
                            System.out.print("Bottom: " + bottomNeighbor);

                            // getting the neighboring faces and looking through them and the tile rules
                            for (Tiles tile : allTiles)
                            {
                                // checking the rules for a rule that meets this tiles rules
                                boolean validTile = false;
                                ArrayList<TileFaces> rules = faceRules.get(tile);
                                for (TileFaces rule : rules)
                                {
                                    // checking if the rule is met
                                    boolean left   = leftNeighbor  .contains(rule.leftTile);
                                    boolean right  = rightNeighbor .contains(rule.rightTile);
                                    boolean top    = topNeighbor   .contains(rule.topTile);
                                    boolean bottom = bottomNeighbor.contains(rule.bottomTile);
                                    validTile = left && right && top && bottom;
                                    if (validTile) break;
                                }

                                // checking if the tile is valid
                                if (validTile)
                                {
                                    // setting the tile
                                    ArrayList<Tiles> newArrayList = new ArrayList<>();
                                    newArrayList.add(tile);
                                    cells[x][y] = newArrayList;

                                    System.out.print(", Fixed: " + newArrayList);

                                    // ending the search
                                    break;
                                }
                            }
                            System.out.println("");
                        }
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
                if      (cells[x][y].get(0) == Tiles.Land  ) System.out.print(GetColored("##", GREEN));
                else if (cells[x][y].get(0) == Tiles.Forest) System.out.print(GetColored("##", BLACK));
                else if (cells[x][y].get(0) == Tiles.Coast ) System.out.print(GetColored("##", YELLOW));
                else if (cells[x][y].get(0) == Tiles.Sea   ) System.out.print(GetColored("##", BLUE));
            }
            System.out.println("");
        }
    }
}
