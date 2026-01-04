package world;

public class Level {
    private String[] tileMap;
    private String name;

    public Level(String name, String[] tileMap) {
        this.name = name;
        this.tileMap = tileMap;
    }

    public String[] getTileMap() {
        return tileMap;
    }

    public String getName() {
        return name;
    }

    public static Level generic()
    {
        String[] map={
                "XXXXXXXXXXXXXXXXXXX",
                "X        X        X",
                "X        X        X",
                "X XXXXX  X  XXXXX X",
                "X X   X  X  X   X X",
                "X                 X",
                "X  XXXX     XXXX  X",
                "X  X  X     X  X  X",
                "X  XXXX     XXXX  X",
                "X                 X",
                "X        X        X",
                "XXXX   X r X   XXXX",
                "X      XbpoX      X",
                "XXXX   XXXXX   XXXX",
                "X                 X",
                "X       XXX       X",
                "X      X   X      X",
                "X      X P X      X",
                "X      X   X      X",
                "X       XXX       X",
                "X                 X",
                "XXXXXXXXXXXXXXXXXXX"
        };
        return new Level("world.Generic", map);
    }

    public static Level level1()
    {
        String[] map={
                "OOOOOOOOOOOOOOOOOOO",
                "XXXXXXXXXXXXXXXXXXX",
                "X        X        X",
                "X XX XXX X XXX XX X",
                "X                 X",
                "X XX X XXXXX X XX X",
                "X    X       X    X",
                "XXXX XXXX XXXX XXXX",
                "OOOX X       X XOOO",
                "XXXX X XXrXX X XXXX",
                "O       bpo       O",
                "XXXX X XXXXX X XXXX",
                "OOOX X       X XOOO",
                "XXXX X XXXXX X XXXX",
                "X        X        X",
                "X XX XXX X XXX XX X",
                "X  X     P     X  X",
                "XX X X XXXXX X X XX",
                "X    X   X   X    X",
                "X XXXXXX X XXXXXX X",
                "X                 X",
                "XXXXXXXXXXXXXXXXXXX"
        };
        return new Level("world.Level1", map);
    }
}
