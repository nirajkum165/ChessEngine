package com.chess.engine;

import com.chess.engine.board.Board;
import com.chess.gui.Table;
/*
 *
 * @author Niraj Kr
 */
public class JChess{
    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        System.out.println(board);
        Table.get().show();
    }
}
