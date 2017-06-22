import java.awt.Color;

import javalib.worldimages.BesideImage;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

abstract class APlayer {
  Cell position;

  APlayer(Cell position) {
    this.position = position;
  }

  public WorldImage draw() {
    return new CircleImage(5, OutlineMode.SOLID, Color.red);
  }
}

// class represents the player controlled player
class Offense extends APlayer {

  boolean tackled;

  Offense(Cell position, boolean tackled) {
    super(position);
    this.tackled = false;
  }
}

// class represents the AI controlled defenders
class Defense extends APlayer {

  Defense(Cell position) {
    super(position);
  }

  void move(Cell offensivePosition) {
    if (offensivePosition.x > this.position.x && !this.position.right.occupied) {
      this.position.occupied = false;
      this.position.right.occupied = true;
      this.position = this.position.right;
    }
    else if (offensivePosition.x < this.position.x && !this.position.left.occupied) {
      this.position.occupied = false;
      this.position.left.occupied = true;
      this.position = this.position.left;
    }
    else if (offensivePosition.y < this.position.y && !this.position.up.occupied) {
      this.position.occupied = false;
      this.position.up.occupied = true;
      this.position = this.position.up;
    }
    else if (offensivePosition.y > this.position.y && !this.position.down.occupied) {
      this.position.occupied = false;
      this.position.down.occupied = true;
      this.position = this.position.down;
    }

  }

}

// class representing A single cell
class Cell {
  int x;
  int y;
  Cell left;
  Cell right;
  Cell up;
  Cell down;
  boolean occupied;

  Cell(int x, int y) {
    this.x = x;
    this.y = y;
    this.left = this;
    this.right = this;
    this.up = this;
    this.down = this;
    this.occupied = false;
  }

  // to render the football field
  public WorldImage render() {

    WorldImage field = new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID,
        Color.BLACK);
    WorldImage lines = new RectangleImage(1, Field.IMG_SCALING, OutlineMode.SOLID, Color.white);
    return new BesideImage(lines, field);
  }
}