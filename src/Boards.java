import java.awt.Color;

import javalib.worldimages.BesideImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;

class Infoboard {
  Integer down;
  Integer fieldposition;
  Integer yardstogo;
  boolean overhalfway;

  Infoboard(Integer down, Integer fieldposition, Integer yardstogo) {
    this.down = down;
    this.fieldposition = fieldposition;
    this.yardstogo = yardstogo;
    this.overhalfway = false;
  }

  WorldImage render() {
    WorldImage down = new OverlayImage(
        new TextImage("Down: " + this.down.toString(), 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));
    WorldImage fieldPosition = new OverlayImage(
        new TextImage("Field Position: " + this.fieldposition.toString(), 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));
    WorldImage yards = new OverlayImage(
        new TextImage("Yards To Go: " + this.yardstogo.toString(), 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));
    return new BesideImage(down, fieldPosition, yards);
  }
}

class Scoreboard {
  Integer home;
  Integer away;
  Integer time;

  Scoreboard(Integer home, Integer away, Integer time) {
    this.home = home;
    this.away = away;
    this.time = time;
  }
  
  WorldImage render() {
    WorldImage homeScore = new OverlayImage(
        new TextImage("Home: " + this.home.toString(), 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));
    WorldImage time = new OverlayImage(
        new TextImage("Time : " + this.time.toString(), 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));
    WorldImage awayScore = new OverlayImage(
        new TextImage("Away: " + this.away.toString(), 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));
    return new BesideImage(homeScore, time, awayScore);
  }
}


