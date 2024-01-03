package com.chess.engine.player.AI;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

/**
 * 
 * @author Niraj Kr
 */
public interface MoveStrategy {
    Move execute(Board board);
}
