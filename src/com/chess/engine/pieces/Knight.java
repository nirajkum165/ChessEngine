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
public class Knight extends Piece{
    public final static int[] CANDIDATE_MOVE_COORDINATES={-17,-15,-10,-6, 6, 10, 15, 17};
    
    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance,true);
    }
    
    public Knight(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves=new ArrayList<>();
        
        for(final int candidateOffset : CANDIDATE_MOVE_COORDINATES){
            final int candidateDestinationCoordinate = super.piecePosition + candidateOffset;
            
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                
                if(isFirstColumnExclusion(this.piecePosition, candidateOffset) ||
                        isSecondColumnExclusion(this.piecePosition, candidateOffset) ||
                        isSeventhColumnExclusion(this.piecePosition, candidateOffset) ||
                        isEightColumnExclusion(this.piecePosition, candidateOffset))
                    continue;
                
                final Tile candidateDestinationTile=board.getTile(candidateDestinationCoordinate);
                
                if(!candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));
                }
                else{
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if(this.pieceAlliance!=pieceAlliance){
                        legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                    }
                }
            }
        }       
        return ImmutableList.copyOf(legalMoves); 
    }
    
    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }
    
    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
    
    private static boolean isFirstColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (currentOffset==-17 || currentOffset==-10 || currentOffset==6 || currentOffset==15);
    }
    private static boolean isSecondColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.SECOND_COLUMN[currentPosition] && (currentOffset==-10 || currentOffset==6);
    }
    private static boolean isSeventhColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (currentOffset==-6 || currentOffset==10);
    }
    private static boolean isEightColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (currentOffset==17 || currentOffset==-15 || currentOffset==-6 || currentOffset==10);
    }
}