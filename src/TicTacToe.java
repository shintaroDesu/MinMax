import java.util.Random;

public class TicTacToe {
/*	private Random random;*/
	private final int NORMAL = 0;
	private final int LOSE = -1;
	private final int DIRECT_WIN = 4;
	private final int DIRECT_LOSE = -4;
	private final int WIN = 1;
	private final int BLOCK = 2;
	private final int CANNOT_BLOCK = 3;
	private final int UNINTIALIZE = -5;

	private int[][] board = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

	public TicTacToe() {
/*		random = new Random();*/
	}

	public boolean tick(int row, int column, int player) {

		if(board[row][column] != 0)
			return false;

		board[row][column] = player;
		return true;
	}

	public int win() {
		// check column and row
		for (int x = 0; x < board.length; x++) {
			if (board[0][x] == board[1][x] && board[0][x] == board[2][x] && board[0][x] != 0)
				return board[0][x];
			if (board[x][0] == board[x][1] && board[x][0] == board[x][2] && board[x][0] != 0)
				return board[x][0];
		}

		// check diagonal
		if (board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] != 0)
			return board[0][0];

		if (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] != 0)
			return board[0][2];
		return 0;
	}

	public boolean full() {
		for (int[] xBoard : board) {
			for (int element : xBoard) {
				if (element == 0)
					return false;
			}
		}
		return true;
	}
	public void printBoard() {
		for (int[] xBoard : board) {
			for (int element : xBoard) {
				System.out.print(element + " ");
			}
			System.out.print("\n");
		}
	}

	public int[] choose(int turn) {
		return choose(turn, 0);
	}

	private void updateStatus(int[] coord, int[] opponent) {
		switch (opponent[2]) {
			case LOSE:
				coord[2] = WIN;
				break;

			case NORMAL:
				coord[2] = NORMAL;
				break;

			case WIN:
				coord[2] = LOSE;
				break;

			case DIRECT_WIN:
				coord[2] = DIRECT_LOSE;
				break;

			case BLOCK:
				coord[2] = NORMAL;
				break;

			case CANNOT_BLOCK:
				coord[2] = WIN;
				break;
		}
	}

	private int changeTurn(int currentTurn) {
		// change turn
		if (currentTurn == 1)
			return 2;
		else
			return 1;
	}
	
	/**
	 *
	 * @param turn          current turn
	 * @return array coord  int[0] row, int[1] column, int[3] status
	 */
	public int[] choose(int turn, int depth) {

		// coordinate of best move
		int[] coord = {UNINTIALIZE, UNINTIALIZE, UNINTIALIZE};

		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board.length; y++) {
				if (board[x][y] == 0 && !(x == coord[0] && y == coord[1])) {
					board[x][y] = turn;

					// Initialize coord if it's not initialized
					if (coord[0] == UNINTIALIZE || coord[2] == LOSE) {
						coord[0] = x;
						coord[1] = y;
						coord[2] = UNINTIALIZE;
					}

					if (win() == turn) {
						coord[0] = x;
						coord[1] = y;
						coord[2] = DIRECT_WIN;
						board[x][y] = 0;
						return coord;
					}

					int tmpTurn = changeTurn(turn);

					// if turn are winning, then check another place if there's double win
					// if turn are not winning then continue the recursion
					// min max here
					int[] opponent = choose(tmpTurn, depth + 1);
					board[x][y] = 0;

					// update status of the current best move
					// check if current x, y is the best move
					if (coord[0] == x && coord[1] == y && coord[2] == UNINTIALIZE) {
						updateStatus(coord, opponent);
					}

					// if the opponent wins the very next turn, then block it
					boolean directLose = coord[2] == DIRECT_LOSE;
					if (directLose) {

						// block by updating the position of coord
						coord[0] = opponent[0];
						coord[1] = opponent[1];
						coord[2] = BLOCK;


						// recheck the new position of coord
						board[coord[0]][coord[1]] = turn;

						opponent = choose(tmpTurn, depth + 1);
						updateStatus(coord, opponent);

						board[coord[0]][coord[1]] = 0;


						// check the status
						boolean lost = coord[2] == LOSE || coord[2] == DIRECT_LOSE;
						if (lost) {
							coord[2] = CANNOT_BLOCK;
							return coord;
						}

						if (coord[2] == NORMAL)
							coord[2] = BLOCK;

					}

					// if this move allow us to win which means opponent[2] (the opponent status) is lose
					boolean winMove = opponent[2] == LOSE && coord[2] != BLOCK;
					// when a move that opponent cannot block. We need to verify that we are not defending
					boolean winMove2 = opponent[2] == CANNOT_BLOCK && coord[2] != BLOCK;

					if (winMove || winMove2) {
						coord[0] = x;
						coord[1] = y;
						coord[2] = WIN;
						return coord;
					}
				}
			}
		}
		return coord;
	}




}
