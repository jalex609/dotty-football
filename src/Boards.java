import java.awt.Color;

import javalib.worldimages.BesideImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;

//represents info about the game for the offense to know
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

  //renders the board with the info about the game
  WorldImage render(boolean homePossesion) {
    String s = "";
    WorldImage down = new OverlayImage(
        new TextImage("Down: " + this.down.toString(), 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));

    WorldImage yards = new OverlayImage(
        new TextImage("Yards To Go: " + this.yardstogo.toString(), 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));
    if ((homePossesion && this.overhalfway) || (!homePossesion && !this.overhalfway)) {
      s = " |-"; // on right side of field
      
    }
    else if ((homePossesion && !this.overhalfway) || (!homePossesion && this.overhalfway)) {
      s = " -|"; //on left side of field
    }
    WorldImage fieldPosition = new OverlayImage(
        new TextImage("Field Position: " + this.fieldposition.toString() + s, 10, Color.white),
        new RectangleImage(Field.IMG_SCALING, Field.IMG_SCALING, OutlineMode.SOLID, Color.black));
    return new BesideImage(down, fieldPosition, yards);

  }
}

//represents the score and time remaining
class Scoreboard {
  Integer home;
  Integer away;
  Integer time;

  Scoreboard(Integer home, Integer away, Integer time) {
    this.home = home;
    this.away = away;
    this.time = time;
  }

  //renders the score and time remaining
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
