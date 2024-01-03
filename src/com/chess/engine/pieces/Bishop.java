package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Niraj Kr
 */
public class Bishop extends Piece {

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES={-9,-7,7,9};
    
    public Bishop(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.BISHOP , piecePosition, pieceAlliance, true);
    }
    
    public Bishop(int piecePosition, Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.BISHOP , piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves=new ArrayList<>();
        
        for(final int candidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES){
            int candidateDestinationCoordinate = this.piecePosition;
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                if(isFirstColumnExclusion(candidateDestinationCoordinate, candidateOffset) ||
                   isEightColumnExclusion(candidateDestinationCoordinate, candidateOffset)){
                    break;
                }
                candidateDestinationCoordinate += candidateOffset;
                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if(!candidateDestinationTile.isTileOccupied()){
                        legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));
                    }
                    else{
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if(this.pieceAlliance!=pieceAlliance){
                            legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }   
        return ImmutableList.copyOf(legalMoves);
    }
    
    @Override
    public String toString(){
        return PieceType.BISHOP.toString();
    }
    
    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
    
    private static boolean isFirstColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (currentOffset==-9 || currentOffset==7);
    }
    private static boolean isEightColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (currentOffset==-7 || currentOffset==9);
    }
}