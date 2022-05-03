# York Pirates!

![York Pirates!](https://engteam14.github.io/media/Logo.gif)

A University of York Computer Science Engineering 1 group project.

Move around the lake and fight opposing colleges!

1. [The Game](#the-game)
2. [The Code](#the-code)
3. [Downloads](#downloads)
4. [Team Members](#team-members)
5. [Attribution](#attribution)

## The Game

### Animated Title Screen

![Title Screen](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Title_Screen.gif)

Application comes with a main screen, where you can enter a name of your choice, start the game, and view our wonderful
title graphic!
<hr/>

### Dynamic Tutorial

![Tutorial](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Tutorial.gif)

Our game begins with a tutorial to show even the least inventive player just how to get 100% out of York Pirates!

<hr/>

### Fluid Goal Indicators

![Indicators](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Indicators.gif)

In addition to our seamless movement, the game contains indicators so that you're never lost in our wonderfully crafted
map environment!

<hr/>

### Fast Paced Combat!

![Combat](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Combat.gif)
![Combat](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Combat2.gif)

Engage in fierce and engaging combat against colleges that feature individually processed projectiles to keep you on
your toes!

<hr/>

### Game Over...

![Game Over](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Game_Over.gif)

No punches (or in this case cannonballs) were pulled during the making of this game, and player death is implemented and
possible!

<hr/>

### Player Tasklist

![Tasks](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Tasks.gif)

In addition to allowing inferior foes to join your ranks (complete with full image and palette swaps!), defeating
colleges and other commendable tasks award you with well-earned loot!

<hr/>

### Game Win!

![Game Win](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Game_Win.gif)

Once all the colleges are captured and you are as satisfied with the gameplay experience as you are with our code,
return to your home college to win the game (and pick our code to win at life)!

<hr/>

## The Code

### Sprite2D.UI

We use LibGDX Sprite2D's UI system to allow User Interface and Heads Up Display (HUD) to be easily rendered and
customised.

```java
// Make a table
Table table = new Table();
table.setBackground(skin.getDrawable("Background"));

// Create widgets
Image title = new Image(titleTexture);
ImageButton button = new ImageButton(skin, styleName: "Restart");

// Add widgets to table
table.add(title).expand();
table.row();
table.add(button).expand();
```

But that's not all...

![Debug Mode](https://github.com/engteam14/engteam14.github.io/blob/4eec4ebe7497bdd52e685b58480de33d258af742/media/Debug.gif)

Featuring a UI debug mode! All at the flip of a boolean.

<hr/>

### ScreenAdapter

To make managing the project easy, we use LibGDX's ScreenAdapter.
This package allows many screens for both gameplay and menus to be easily put together and organised as their own
classes.
The easiest way to get the best project structure!

```java
public class ScreenName extends ScreenAdapter {
    
    public ScreenName() {
        // Initialise the screen
    }
    
    @Override
    public void render(float delta) {
        // Called every frame
        // Perform calculations
        // Render graphics
    }
    
    @Override
    public void dispose() {
        // Dispose of assets when done to improve performance
    }
}
```

<hr/>

### TiledMap

Using LibGDX's package for Tiled maps is the easiest way to rapidly produce gameplay levels.
The software [Tiled](https://www.mapeditor.org) can be used to produce levels, with easy to use wide-ranging
functionality, it's the industry's best for a reason!

![Tiled](https://github.com/engteam14/engteam14.github.io/blob/8bcd1e29772d236150961fb6efaf845062fdc000/media/tiled.gif)

Maps from Tiled can be easily imported into LibGDX, and we've even implemented our own collision system around it -
ready out of the box!

## Downloads

The most recent version of the game can be downloaded from the releases tab.

## Team Members

- Osama Azaz
- Adam Dawtry
- Tom Jackson
- Brendan Liew
- Holly Reed
- Harry Ryan

## Attribution

- [libGDX](https://libgdx.com/)
- [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff)
- [Tiled](https://www.mapeditor.org/)
- [gdx-skins](https://github.com/czyzby/gdx-skins)
- [Pirate Pack](https://opengameart.org/content/pirate-pack-190)
- [City Tiles](https://opengameart.org/content/12x12-city-tiles-top-down)
- [Animated Tutorial](https://opengameart.org/content/animated-game-controls-tutorial)
- [skin-composer](https://github.com/raeleus/skin-composer)
- [Raleway font](https://fonts.google.com/specimen/Raleway)
