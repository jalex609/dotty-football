import java.util.ArrayList;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import tester.*;
//TODO fix extra distance bug, clean up code and test and find more bugs

// class representing the football field itself
class Field extends World {
  ArrayList<ArrayList<Cell>> board;
  Offense player;
  ArrayList<Defense> defenders = new ArrayList<Defense>();
  boolean homepossesion;
  boolean firstMove;
  int tickNum;
  Infoboard info;
  boolean firstDown;
  Scoreboard score;

  static final int IMG_SCALING = 100;

  Field() {
    this.board = this.generateBoard(9, 3);
    this.linkNeighbors(this.board);
    this.player = new Offense(this.board.get(1).get(0), false);
    this.defenders.add(new Defense(this.board.get(0).get(3)));
    this.defenders.add(new Defense(this.board.get(1).get(3)));
    this.defenders.add(new Defense(this.board.get(2).get(3)));
    this.defenders.add(new Defense(this.board.get(1).get(5)));
    this.defenders.add(new Defense(this.board.get(1).get(8)));
    for (Defense d : this.defenders) {
      d.position.occupied = true;
    }
    this.homepossesion = true;
    this.tickNum = 0;
    this.firstMove = false;
    this.info = new Infoboard(1, 20, 10);
    this.firstDown = false;
    this.score = new Scoreboard(0, 0, 500);
  }

  // creates the initial game board
  ArrayList<ArrayList<Cell>> generateBoard(int length, int height) {

    ArrayList<ArrayList<Cell>> rows = new ArrayList<ArrayList<Cell>>();
    for (int row = 0; row < height; row += 1) {
      ArrayList<Cell> columns = new ArrayList<Cell>();
      for (int column = 0; column < length; column += 1) {
        columns.add(new Cell(column, row));

      }
      rows.add(columns);
    }
    return rows;
  }

  // EFFECT: links all of the cells in the given array to their neighbors
  void linkNeighbors(ArrayList<ArrayList<Cell>> cells) {

    for (int row = 0; row <= 2; row += 1) {
      for (int column = 0; column < 9; column += 1) {
        Cell current = cells.get(row).get(column);

        if (row == 0) {
          current.down = (cells.get(row + 1).get(column));
        }
        else if (row == 2) {
          current.up = cells.get(row - 1).get(column);
        }
        else {
          current.up = cells.get(row - 1).get(column);
          current.down = cells.get(row + 1).get(column);
        }

        if (column == 0) {
          current.right = cells.get(row).get(column + 1);
        }
        else if (column == 8) {
          current.left = cells.get(row).get(column - 1);
        }
        else {
          current.right = cells.get(row).get(column + 1);
          current.left = cells.get(row).get(column - 1);
        }
      }
    }
  }

  // what the world does every tick
  public void onTick() {
    this.tickNum += 1;

    if (this.info.yardstogo == 0) {
      this.info.yardstogo = 10;
      this.firstDown = true;
    }

    for (Defense d : defenders) {
      if (d.position.equals(player.position) && this.info.down == 4 && !this.firstDown) {
        this.homepossesion = !this.homepossesion;
        this.info.yardstogo = 10;
        this.firstDown = true;
        this.player.tackled = true;
      }
      else if (d.position.equals(player.position)) {
        this.info.down += 1;
        this.player.tackled = true;
      }
      else if (d.position.equals(player.position) && this.firstDown) {
        this.player.tackled = true;

      }
      else if (d.position.equals(player.position) && this.info.down == 4 && this.firstDown) {
        this.player.tackled = true;
      }
    }

    if (tickNum % 10 == 0 && this.firstMove) {
      this.defenders.get((int) (Math.random() * 5)).move(this.player.position);
    }

    if (this.player.tackled && homepossesion) {
      this.resetHomePosition();
    }
    else if (this.player.tackled && !homepossesion) {
      this.resetAwayPosition();
    }

  }

  void resetHomePosition() {
    for (ArrayList<Cell> cells : this.board) {
      for (Cell c : cells) {
        c.occupied = false;
      }
    }
    this.player.position = this.board.get(1).get(0);
    this.player.tackled = false;
    this.defenders.clear();
    this.defenders.add(new Defense(this.board.get(0).get(3)));
    this.defenders.add(new Defense(this.board.get(1).get(3)));
    this.defenders.add(new Defense(this.board.get(2).get(3)));
    this.defenders.add(new Defense(this.board.get(1).get(5)));
    this.defenders.add(new Defense(this.board.get(1).get(8)));
    for (Defense d : this.defenders) {
      d.position.occupied = true;
    }
    this.homepossesion = true;
    this.tickNum = 0;
    this.firstMove = false;
    if (firstDown) {
      this.firstDown = false;
      this.info.yardstogo = 10;
      this.info.down = 1;
    }

  }

  void resetAwayPosition() {
    for (ArrayList<Cell> cells : this.board) {
      for (Cell c : cells) {
        c.occupied = false;
      }
    }
    this.player.position = this.board.get(1).get(8);
    this.player.tackled = false;
    this.defenders.clear();
    this.defenders.add(new Defense(this.board.get(0).get(5)));
    this.defenders.add(new Defense(this.board.get(1).get(5)));
    this.defenders.add(new Defense(this.board.get(2).get(5)));
    this.defenders.add(new Defense(this.board.get(1).get(3)));
    this.defenders.add(new Defense(this.board.get(1).get(0)));
    for (Defense d : this.defenders) {
      d.position.occupied = true;
    }
    this.homepossesion = false;
    this.tickNum = 0;
    this.firstMove = false;
    if (firstDown) {
      this.firstDown = false;
      this.info.down = 1;
      this.info.yardstogo = 10;
    }
  }

  void resetAfterAction(int fieldPosition, boolean overhalf, int whatScore) {
    if (homepossesion) {
      this.resetAwayPosition();
      this.score.home += whatScore;
      this.info.down = 1;
      this.info.fieldposition = fieldPosition;
      this.info.overhalfway = overhalf;
    }
    else {
      this.resetHomePosition();
      this.score.away += whatScore;
      this.info.down = 1;
      this.info.fieldposition = fieldPosition;
      this.info.overhalfway = overhalf;
    }
  }

  // EFFECT: Modifies the world for certain key events
  public void onKeyEvent(String key) {

    if (key.equals("left")) {
      this.firstMove = true;
      this.score.time -= 1;
      if (!homepossesion) {
        if (this.player.position.x == 0) {
          this.player.position = this.board.get(this.player.position.y).get(8);
        }
        else {
          this.player.position = this.player.position.left;
        }

        this.info.yardstogo -= 1;
        if (info.fieldposition == 50) {
          this.info.overhalfway = true;
          this.info.fieldposition -= 1;
        }
        else if (info.overhalfway) {
          this.info.fieldposition -= 1;
        }
        else {
          this.info.fieldposition += 1;
        }
      }
    }
    else if (key.equals("right")) {
      this.score.time -= 1;
      this.firstMove = true;
      if (homepossesion) {
        if (this.player.position.x == 8) {
          this.player.position = this.board.get(this.player.position.y).get(0);
        }
        else {
          this.player.position = this.player.position.right;
        }
        this.info.yardstogo -= 1;
        if (info.fieldposition == 50) {
          this.info.overhalfway = true;
          this.info.fieldposition -= 1;
        }
        else if (info.overhalfway) {
          this.info.fieldposition -= 1;
        }
        else {
          this.info.fieldposition += 1;
        }
      }
    }
    else if (key.equals("up")) {
      this.score.time -= 1;
      this.firstMove = true;
      player.position = player.position.up;
    }
    else if (key.equals("down")) {
      this.score.time -= 1;
      this.firstMove = true;
      player.position = player.position.down;
    }
    else if (key.equals("p")) {
      if (this.info.down == 4 && !this.firstMove) {
        if (this.info.fieldposition + 40 > 50 && !this.info.overhalfway) {
          this.resetAfterAction(100 - (this.info.fieldposition + 40), false, 0);
        }
        else {
          this.resetAfterAction(this.info.fieldposition + 40, true, 0);
        }
      }
    }
    else if (key.equals("k")) {
      if (this.info.overhalfway && !this.firstMove) {
        double rand = Math.random();
        if (this.info.fieldposition > 40) {
          if (rand > .9) {
            this.resetAfterAction(20, false, 3);
          }
        }
        else if (this.info.fieldposition > 30 && this.info.fieldposition < 40) {
          if (rand > .7) {
            this.resetAfterAction(20, false, 3);
          }
        }
        else if (this.info.fieldposition > 20 && this.info.fieldposition < 30) {
          if (rand > .5) {
            this.resetAfterAction(20, false, 3);
          }
        }
        else if (this.info.fieldposition > 10 && this.info.fieldposition < 20) {
          if (rand > .3) {
            this.resetAfterAction(20, false, 3);
          }
        }
        else if (this.info.fieldposition > 0 && this.info.fieldposition < 10) {
          if (rand > .1) {
            this.resetAfterAction(20, false, 3);
          }
        }
        else {
          this.resetAfterAction(20, false, 0);
        }
      }
    }

    if (info.fieldposition == 0) {
      if (homepossesion) {
        this.score.home += 7;
        this.resetAfterAction(20, false, 7);
      }
      else {
        this.score.away += 7;
        this.resetAfterAction(20, false, 7);
      }
    }
  }

  public WorldScene makeScene() {
    WorldScene result = new WorldScene(9 * Field.IMG_SCALING, 4 * Field.IMG_SCALING);

    for (ArrayList<Cell> cells : this.board) {
      for (Cell c : cells) {
        result.placeImageXY(c.render(), c.x * Field.IMG_SCALING + (Field.IMG_SCALING / 2),
            c.y * Field.IMG_SCALING + (Field.IMG_SCALING / 2));
      }
    }

    result.placeImageXY(player.draw(),
        player.position.x * Field.IMG_SCALING + (Field.IMG_SCALING / 2),
        player.position.y * Field.IMG_SCALING + (Field.IMG_SCALING / 2));

    for (Defense d : this.defenders) {
      result.placeImageXY(d.draw(), d.position.x * Field.IMG_SCALING + (Field.IMG_SCALING / 2),
          d.position.y * Field.IMG_SCALING + (Field.IMG_SCALING / 2));
    }

    result.placeImageXY(new RectangleImage(4 * Field.IMG_SCALING, Field.IMG_SCALING,
        OutlineMode.SOLID, Color.BLACK), 0, 350);
    result.placeImageXY(new RectangleImage(2 * Field.IMG_SCALING, Field.IMG_SCALING,
        OutlineMode.SOLID, Color.BLACK), 800, 350);
    result.placeImageXY(this.info.render(this.homepossesion), 650, 350);
    result.placeImageXY(this.score.render(), 350, 350);
    return result;
  }
}

class ExamplesFootball {
  Field f1;

  void initField() {
    f1 = new Field();
  }

  void testGame(Tester t) {
    this.initField();
    f1.bigBang(9 * Field.IMG_SCALING, 4 * Field.IMG_SCALING, .05);

  }

}
