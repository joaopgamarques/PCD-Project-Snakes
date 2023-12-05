package game;

import environment.Board;
import environment.LocalBoard;

/** Class for a remote snake, controlled by a human.
  * 
  * @author luismota
  *
  */
public class HumanSnake extends Snake {
	public HumanSnake(int id, Board board) {
		super(id, board);
	}

	 @Override
	 public void run() {
		// TODO
		 doInitialPositioning();
		 System.out.println(Thread.currentThread().getName() + ": Started.");

		 // Checks if all automatic snakes on the board are idle.
		 if (((LocalBoard)getBoard()).areAllSnakesIdle()) {
			 try {
				 Thread.sleep(Board.REMOTE_CONNECTION_SETUP_DELAY);
			 } catch (InterruptedException e) {
				 System.out.println(Thread.currentThread().getName() + ": Interrupted during initial wait.");
			 }
		 }




	 }
}