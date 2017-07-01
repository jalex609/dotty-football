import java.util.ArrayList;

import tester.Tester;

class ExamplesFootball {
  Field f1;

  void initField() {
    f1 = new Field();
  }

  void testGenerateBoard(Tester t) {
    this.initField();
    ArrayList<ArrayList<Cell>> b = f1.generateBoard(10, 5);
    t.checkExpect(b.size(), 5);
    t.checkExpect(b.get(1).size(), 10);
    t.checkExpect(b.size() * b.get(1).size(), 50);

  }

  void testLinkNeighbors(Tester t) {
    this.initField();

    for (ArrayList<Cell> rows : f1.board) {
      for (Cell c : rows) {
        if (!(c.y == 0)) {
          t.checkExpect(c.up == c, false);
        }
        if (!(c.y == f1.board.size() - 1)) {
          t.checkExpect(c.down == c, false);
        }
        if (!(c.x == 0)) {
          t.checkExpect(c.left == c, false);
        }
        if (!(c.x == rows.size() - 1)) {
          t.checkExpect(c.right == c, false);
        }
      }
    }

  }

  void testOnTick(Tester t) {
    this.initField();
    f1.score.time = -5;
    f1.onTick();
    t.checkExpect(f1.score.time, -5);
    this.initField();
    f1.info.yardstogo = 0;
    f1.onTick();
    t.checkExpect(f1.info.yardstogo == 10, true);
    t.checkExpect(f1.firstDown,true);
    this.initField();
    f1.defenders.get(2).position = f1.player.position;
    f1.info.down = 4;
    f1.firstDown = false;
    f1.onTick();
    t.checkExpect(f1.homepossesion, false);
    t.checkExpect(f1.info.yardstogo, 10);
    t.checkExpect(f1.info.down, 1);
    t.checkExpect(f1.info.overhalfway, true);
    t.checkExpect(f1.player.tackled, false);
    t.checkExpect(f1.player.position.x == 8 && f1.player.position.y == 1, true);
    this.initField();
    f1.defenders.get(2).position = f1.player.position;
    f1.onTick();
    t.checkExpect(f1.player.position.x == 0 && f1.player.position.y == 1, true);
    this.initField();
    t.checkExpect(f1.defenders, f1.defenders);
  }

  void testReset(Tester t) {
    this.initField();
    f1.resetHomePosition();
    t.checkExpect(f1.firstMove, false);
    t.checkExpect(f1.firstDown, false);
    t.checkExpect(f1.info.down, 2);
    t.checkExpect(f1.tickNum, 0);
    this.initField();
    f1.resetAfterAction(5, false, 7);
    t.checkExpect(f1.score.home, 7);
    t.checkExpect(f1.score.away, 0);
    t.checkExpect(f1.info.fieldposition, 5);    
  }

  void testUpdate(Tester t) {
    this.initField();
    f1.defenders.get(2).position = f1.player.position;
    f1.info.overhalfway = true;
    f1.updateAfterMovement();
    t.checkExpect(f1.info.fieldposition, 19);
    f1.updateAfterTackle();
    t.checkExpect(f1.info.fieldposition, 20);
    f1.firstMove = false;
    f1.info.down = 4;
    f1.updateAfterPunt();
    t.checkExpect(f1.info.fieldposition, 1);
  }

  void testOnKey(Tester t) {
    this.initField();
    f1.homepossesion = false;
    f1.onKeyEvent("left");
    t.checkExpect(f1.player.position.x , 8);
    f1.resetAwayPosition();
    f1.homepossesion = true;
    f1.onKeyEvent("right");
    t.checkExpect(f1.player.position.x, 0);
    this.initField();
    f1.onKeyEvent("up");
    t.checkExpect(f1.player.position.y, 0);
    t.checkExpect(f1.score.time, 499);
    f1.onKeyEvent("up");
    t.checkExpect(f1.player.position.y, 0);
    t.checkExpect(f1.score.time, 499);
  }

  void testMove(Tester t) {
    this.initField();
    f1.firstMove = true;
    f1.onTick();
    int i = 0;
    for (ArrayList<Cell> cells: f1.board) {
      for (Cell c: cells) {
        if (c.occupied) {
          i += 1;
        }
      }
      
    }
    t.checkExpect(i, 5);
  }


  void testGame(Tester t) {
     this.initField();
     f1.bigBang(9 * Field.IMG_SCALING, 4 * Field.IMG_SCALING, .05);

  }

}