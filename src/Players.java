import java.util.ArrayList;
import java.util.Arrays;

import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.util.List;

import tester.*;

// class representing the football field itself
class Field extends World {
  ArrayList<ArrayList<Cell>> board;
  Offense player;
  ArrayList<Defense> defenders = new ArrayList<Defense>();
  boolean homepossesion; // who has the ball
  boolean firstMove; // has the player moved once (triggers defense)
  int tickNum;
  Infoboard info; // represents info for the offense
  boolean firstDown; // has a first down been scored
  Scoreboard score; // score and time

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
      d.position.occupied = true; // they occupy their position
    }
    this.homepossesion = true;
    this.tickNum = 0;
    this.firstMove = false;
    this.info = new Infoboard(1, 20, 10); // home starts on the 20 for every
                                          // game
    this.firstDown = false;
    this.score = new Scoreboard(0, 0, 1);
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

  // EFFECT: Allows defense to tackle, resets after tackles and record first
  // downs
  public void onTick() {
    if (this.score.time > 0) {
    this.tickNum += 1;

    // checks if there is a first down
    if (this.info.yardstogo == 0) {
      this.info.yardstogo = 10;
      this.firstDown = true;
    }

    for (Defense d : defenders) {
      if (d.position.equals(player.position) && this.info.down == 4 && !this.firstDown) { // turnover
                                                                                          // on
                                                                                          // downs
        this.homepossesion = !this.homepossesion;
        this.info.yardstogo = 10;
        this.firstDown = true;
        this.info.overhalfway = !this.info.overhalfway;
        this.player.tackled = true; // prevents concurrent modification
      }
      else if (d.position.equals(player.position)) {
        this.player.tackled = true; // prevents concurrent modification
      }
    }
    // moves defenders
    if (tickNum % 10 == 0 && this.firstMove) {
      this.defenders.get((int) (Math.random() * 5)).move(this.player.position);
    }
    // resets to next down
    if (this.player.tackled && homepossesion) {
      this.resetHomePosition();
    }
    // resets to next down for away
    else if (this.player.tackled && !homepossesion) {
      this.resetAwayPosition();
    }
   }
  }

  // EFFECT: Resets player after tackled after moving
  void resetAfterMove(Cell oPosition, List<Cell> dPositions) {
    this.player.position = oPosition;
    this.player.tackled = false;
    this.defenders.clear();
    this.defenders.add(new Defense(dPositions.get(0)));
    this.defenders.add(new Defense(dPositions.get(1)));
    this.defenders.add(new Defense(dPositions.get(2)));
    this.defenders.add(new Defense(dPositions.get(3)));
    this.defenders.add(new Defense(dPositions.get(4)));
    for (ArrayList<Cell> cells : this.board) {
      for (Cell c : cells) {
        c.occupied = false;
      }
    }
    for (Defense d : this.defenders) {
      d.position.occupied = true;
    }
    this.tickNum = 0;
    this.firstMove = false;
    if (firstDown) {
      this.firstDown = false;
      this.info.yardstogo = 10;
      this.info.down = 1;
    }
    else {
      this.info.down += 1;
    }
  }

  // EFFECT: resets to home position on left side
  void resetHomePosition() {
    this.resetAfterMove(this.board.get(1).get(0),
        Arrays.asList(this.board.get(0).get(3), this.board.get(1).get(3), this.board.get(2).get(3),
            this.board.get(1).get(5), this.board.get(1).get(8)));
  }

  // EFFECT: resets to away position on right side
  void resetAwayPosition() {
    this.resetAfterMove(this.board.get(1).get(8),
        Arrays.asList(this.board.get(0).get(5), this.board.get(1).get(5), this.board.get(2).get(5),
            this.board.get(1).get(3), this.board.get(1).get(0)));
  }

  // EFFECT: resets after action (score, punt, kick)
  void resetAfterAction(int fieldPosition, boolean overhalf, int whatScore) {
    if (homepossesion) {
      this.resetAwayPosition();
      this.homepossesion = false;
      this.score.home += whatScore;
      this.info.down = 1;
      this.info.fieldposition = fieldPosition;
      this.info.overhalfway = overhalf;
    }
    else {
      this.resetHomePosition();
      this.homepossesion = false;
      this.score.away += whatScore;
      this.info.down = 1;
      this.info.fieldposition = fieldPosition;
      this.info.overhalfway = overhalf;
    }
  }

  // EFFECT: resets the field position to
  // correct position if player runs into defender to be tackled
  void updateAfterTackle() {
    for (Defense d : defenders) {
      if (this.player.position.equals(d.position)) {
        this.info.yardstogo += 1;
        if (this.info.overhalfway) {
          this.info.fieldposition += 1;
        }
        else {
          this.info.fieldposition -= 1;
        }
      }
    }
  }

  // EFFECT: updates the infoboard after movement
  void updateAfterMovement() {
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

  // EFFECT: allows user to kick from past the halfway mark and to varying
  // success
  void userKick() {
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

  //EFFECT: allows user to punt on 4th down
  void updateAfterPunt() {
    if (this.info.down == 4 && !this.firstMove) {
      if (this.info.fieldposition + 40 > 50 && !this.info.overhalfway) {
        this.resetAfterAction(100 - (this.info.fieldposition + 40), false, 0);
      }
      else if (this.info.overhalfway) {
        if (this.info.fieldposition > 40) {
          this.resetAfterAction(this.info.fieldposition - 40, false, 0);
        }
        else {
          this.resetAfterAction(1, false, 0);
        }
      }
      else {
        this.resetAfterAction(this.info.fieldposition + 40, true, 0);
      }
    }
  }

  // EFFECT: Modifies the world for certain key events
  public void onKeyEvent(String key) {

   if (this.score.time > 0) {
    if (key.equals("left")) {
      if (!homepossesion) {
        this.firstMove = true; //defenders move
        this.score.time -= 1;
        if (this.player.position.x == 0) {
          this.player.position = this.board.get(this.player.position.y).get(8); //allows player to wrap around
        }
        else {
          this.player.position = this.player.position.left;
        }
        this.updateAfterTackle();
        this.updateAfterMovement();
      }
    }
    else if (key.equals("right")) {
      if (homepossesion) {
        this.score.time -= 1;
        this.firstMove = true; //defenders move
        if (this.player.position.x == 8) {
          this.player.position = this.board.get(this.player.position.y).get(0); //allows player to wrap around
        }
        else {
          this.player.position = this.player.position.right;
        }
        this.updateAfterTackle();
        this.updateAfterMovement();
      }
    }
    else if (key.equals("up")) {
      if (!(this.player.position.up == this.player.position)) { 
        this.score.time -= 1;  //if they are not trying to move to the same spot
      }
      this.firstMove = true;
      player.position = player.position.up;
    }
    else if (key.equals("down")) {
      if (!(this.player.position.down == this.player.position)) {
        this.score.time -= 1;
      }
      this.firstMove = true;
      player.position = player.position.down;
    }
    else if (key.equals("p")) {
      this.updateAfterPunt();
    }
    else if (key.equals("k")) {
      this.userKick();
    }
    //for scoring
    if (info.fieldposition == 0) {
      if (homepossesion) {
        this.resetAfterAction(20, false, 7);
      }
      else {
        this.resetAfterAction(20, false, 7);
      }
    }
   }
  }

  //draws scene
  //EFFECT: adds time if in tie
  public WorldScene makeScene() {
    WorldScene result = new WorldScene(9 * Field.IMG_SCALING, 4 * Field.IMG_SCALING);

    //places cells
    for (ArrayList<Cell> cells : this.board) {
      for (Cell c : cells) {
        result.placeImageXY(c.render(), c.x * Field.IMG_SCALING + (Field.IMG_SCALING / 2),
            c.y * Field.IMG_SCALING + (Field.IMG_SCALING / 2));
      }
    }

    //places player
    result.placeImageXY(player.render(),
        player.position.x * Field.IMG_SCALING + (Field.IMG_SCALING / 2),
        player.position.y * Field.IMG_SCALING + (Field.IMG_SCALING / 2));

    //defenders
    for (Defense d : this.defenders) {
      result.placeImageXY(d.render(), d.position.x * Field.IMG_SCALING + (Field.IMG_SCALING / 2),
          d.position.y * Field.IMG_SCALING + (Field.IMG_SCALING / 2));
    }

    //covers
    result.placeImageXY(new RectangleImage(4 * Field.IMG_SCALING, Field.IMG_SCALING,
        OutlineMode.SOLID, Color.BLACK), 0, 350);
    result.placeImageXY(new RectangleImage(2 * Field.IMG_SCALING, Field.IMG_SCALING,
        OutlineMode.SOLID, Color.BLACK), 800, 350);
    //info board
    result.placeImageXY(this.info.render(this.homepossesion), 650, 350);
    //score board
    result.placeImageXY(this.score.render(), 350, 350);
    
    if (this.score.time == 0) {
      if (this.score.home > this.score.away) {
      result.placeImageXY(new TextImage("Home team wins!", 14, Color.white), 450, 150);
    } else if (this.score.home < this.score.away) {
      result.placeImageXY(new TextImage("Away team wins!", 14, Color.white), 450, 150);
    } else if (this.score.home == this.score.away) {
      this.score.time += 200;
      }
    }

    return result;
  }
}

//TODO test 
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
