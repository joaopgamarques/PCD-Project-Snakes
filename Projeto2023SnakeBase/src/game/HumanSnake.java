package game;

import environment.Board;
 /** Class for a remote snake, controlled by a human.
  * 
  * @author luismota
  *
  */
public class HumanSnake extends Snake {
	public HumanSnake(int id,Board board) {
		super(id, board);
	}

	 @Override
	 public void run() {
		// TODO
		 doInitialPositioning();
	 }
}