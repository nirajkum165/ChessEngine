package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Niraj Kr
 */
public class King extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES={-9,-8,-7,-2,-1,1,2,7,8,9};
    private boolean kingSideCastleCapable;
    private boolean queenSideCastleCapable;
    private final boolean isCastled;
    
    public King(final int piecePosition, 
                final Alliance pieceAlliance,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    
    public King(final int piecePosition, 
                final Alliance pieceAlliance, 
                final boolean isFirstMove,
                final boolean isCastled,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public boolean isCastled(){
        return this.isCastled;
    }
    
    public boolean isKingSideCastleCapable(){
        return this.kingSideCastleCapable;
    }
    
    public boolean isQueenSideCastleCapable(){
        return this.queenSideCastleCapable;
    }
    
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves=new ArrayList<>();
        
        for(final int candidateOffset:CANDIDATE_MOVE_COORDINATES){
            if(isFirstColumnExclusion(this.piecePosition, candidateOffset) ||
               isEightColumnExclusion(this.piecePosition, candidateOffset)){
                break;
            }
            final int candidateDestinationCoordinate = this.piecePosition + candidateOffset;
            
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if(!candidateDestinationTile.isTileOccupied()){
                    if(this.isFirstMove() && 
                       ((candidateOffset == -2 && this.isQueenSideCastleCapable()) || 
                        (candidateOffset == 2) && this.isKingSideCastleCapable())){
                        if(this.isKingSideCastleCapable()){
                            Piece castleRook = board.getTile(candidateDestinationCoordinate + 1).getPiece();
                            if(castleRook instanceof Rook)
                                legalMoves.add(new Move.KingSideCastleMove(board, this, candidateDestinationCoordinate, (Rook)castleRook, 
                                        candidateDestinationCoordinate + 1, candidateDestinationCoordinate - 1));
                            else
                                this.kingSideCastleCapable = false;
                        }
                        if(this.isQueenSideCastleCapable()){
                            Piece castleRook = board.getTile(candidateDestinationCoordinate - 2).getPiece();
                            if(castleRook instanceof Rook)
                                legalMoves.add(new Move.QueenSideCastleMove(board, this, candidateDestinationCoordinate, (Rook)castleRook, 
                                        candidateDestinationCoordinate - 2, candidateDestinationCoordinate + 1));
                            else
                                this.queenSideCastleCapable = false;
                        }
                    }
                    else if(candidateOffset != -2 && candidateOffset != 2)
                        legalMoves.add(new Move.MajorMove(board,this,candidateDestinationCoordinate));
                }
                else{
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if(this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }
    
    @Override
    public String toString(){
        return PieceType.KING.toString();
    }
    
    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), 
                        move.getMovedPiece().getPieceAlliance(),
                        false,
                        move.isCastlingMove(),
                        false, false);
    }
    
    private static boolean isFirstColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (currentOffset==-9 || currentOffset==-1 || currentOffset==7);
    }
    private static boolean isEightColumnExclusion(final int currentPosition, final int currentOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (currentOffset==-7 || currentOffset==1 || currentOffset==9);
    }
}