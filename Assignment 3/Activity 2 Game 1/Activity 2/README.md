# Assignment 3 Starter Code

## Task 2 - Preliminary Checklist
# a)
>  This Program is a image guessing game that allows for the functionality to be played over a network or locally depending on the users choice. This game begins by asking the user a name. After greeting the user it ask if they want to see leaderboard or not. Then how many times they would like to play. Once start is input, a timer begins giving the user 30 seconds for each image.
   After the initial starting data has been entered the user begins to guess what the presented image is. 
   If the user struggle to make a correct guess then they can type more and next image show up.
   If they are stuck they can opt to input "next" to continue on to the next image. Of course, they will lose another point.
   The game goes on till the number of times the user wishes to play is reached. If they guess all of the images within the alloted time, they win or lose.
   If they wish to play again they can choose to enter the name of the next player and continue.
   
  g) Switching from TCP to UDP for sending images in your game would introduce potential issues. TCP provides reliable and ordered delivery of data through mechanisms like the three-way handshake, ensuring that the image data arrives intact and in the correct order. However, UDP does not guarantee these properties.

With UDP, the reliability of the image data would be compromised. UDP packets may get lost, arrive out of order, or experience packet loss due to network congestion or other factors. As a result, the received images may not be complete or may contain errors, which would affect the intended gameplay experience. 
  
  b) Completed and Tested Requirements: All requirements

### Terminal

```
gradle runServer
// run with arguments e.g.: gradle Client -Phost=localhost -Pport=9099

```
gradle runClient



## GUI Usage

### Code

1. Create an instance of the GUI

   ```
   ClientGui main = new ClientGui();
   ```

2. Create a new game and give it a grid dimension

   ```
   // the pineapple example is 2, but choose whatever dimension of grid you want
   // you can change the dimension to see how the grid changes size
   main.newGame(2); 
   ```

*Depending on how you want to run the system, 3 and 4 can be run how you want*

3. Insert image

   ```
   // the filename is the path to an image
   // the first coordinate(0) is the row to insert in to
   // the second coordinate(1) is the column to insert in to
   // you can change coordinates to see the image move around the box
   main.insertImage("img/Pineapple-Upside-down-cake_0_1.jpg", 0, 1);
   ```

4. Show GUI

   ```
   // true makes the dialog modal meaning that all interaction allowed is 
   //   in the windows methods.
   // false makes the dialog a pop-up which allows the background program 
   //   that spawned it to continue and process in the background.
   main.show(true);
   ```


### ClientGui.java
#### Summary

> This is the main GUI to display the picture grid. 

#### Methods
  - show(boolean modal) :  Shows the GUI frame with the current state
     * NOTE: modal means that it opens the GUI and suspends background processes. Processing still happens in the GUI If it is desired to continue processing in the background, set modal to false.
   * newGame(int dimension) :  Start a new game with a grid of dimension x dimension size
   * insertImage(String filename, int row, int col) :  Inserts an image into the grid, this is when you know the file name, use the PicturePanel insertImage if you have a ByteStream
   * appendOutput(String message) :  Appends text to the output panel
   * submitClicked() :  Button handler for the submit button in the output panel

### PicturePanel.java

#### Summary

> This is the image grid

#### Methods

- newGame(int dimension) :  Reset the board and set grid size to dimension x dimension
- insertImage(String fname, int row, int col) :  Insert an image at (col, row)
- insertImage(ByteArrayInputStream fname, int row, int col) :  Insert an image at (col, row)

### OutputPanel.java

#### Summary

> This is the input box, submit button, and output text area panel

#### Methods

- getInputText() :  Get the input text box text
- setInputText(String newText) :  Set the input text box text
- addEventHandlers(EventHandlers handlerObj) :  Add event listeners
- appendOutput(String message) :  Add message to output text

