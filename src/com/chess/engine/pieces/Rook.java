package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Niraj Kr
 */
public class Rook extends Piece{
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES={-1,-8,1,8};
    
    public Rook(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.ROOK, piecePosition, pieceAlliance,true);
    }
    
    public Rook(final int piecePosition, final Alliance pieceAlliance,final boolean isFirstMove) {
        super(PieceType.ROOK, piecePosition, pieceAlliance,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves=new ArrayList<>();
        
        for(final int candidateOffset:CANDIDATE_MOVE_VECTOR_COORDINATES){
            int candidateDestinationCoordinate=this.piecePosition;
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                if(isFirstColumnExclusion(candidateDestinationCoordinate, candidateOffset) ||
                   isEightColumnExclusion(candidateDestinationCoordinate, candidateOffset))
                    break;
                
                candidateDestinationCoordinate += candidateOffset;
                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
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
                        break;
                    }
                }
            }
        }   
        return ImmutableList.copyOf(legalMoves);
    }
    
    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
    
    @Override
    public String toString(){
        return PieceType.ROOK.toString();
    }
    
    private static boolean isFirstColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (currentOffset==-1);
    }
    private static boolean isEightColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (currentOffset==1);
    }
}