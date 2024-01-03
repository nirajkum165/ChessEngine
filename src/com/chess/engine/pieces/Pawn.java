package com.chess.engine.pieces;

import static com.chess.engine.Board.BoardUtils.NUM_TILES_PER_ROW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.PawnAttackMove;
import com.chess.engine.board.Move.PawnEnPassantAttackMove;
import com.chess.engine.board.Move.PawnJump;
import com.chess.engine.board.Move.PawnMove;
import com.chess.engine.board.Move.PawnPromotion;
import com.chess.gui.Table;
import com.chess.gui.Table.PlayerType;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Niraj Kr
 */
public class Pawn extends Piece{
    private final static int[] CANDIDATE_MOVE_COORDINATES={8,16,7,9};
     
    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }
    
    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves=new ArrayList<>();
        
        for(final int candidateOffset:CANDIDATE_MOVE_COORDINATES){
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection()*candidateOffset);
            
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                //  Normal Move
                if(candidateOffset==8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate))
                        legalMoves.add(new PawnPromotion(new PawnMove(board,this,candidateDestinationCoordinate)));
                    else
                        legalMoves.add(new PawnMove(board,this,candidateDestinationCoordinate));
                }
                // Initial Double-Step Move
                if(candidateOffset==16 && this.isFirstMove()&&
                        ((BoardUtils.SEVENTH_ROW[this.piecePosition] && this.pieceAlliance.isBlack())||
                        (BoardUtils.SECOND_ROW[this.piecePosition] && this.pieceAlliance.isWhite()))){
                
                    final int behindCandidateDestinationCoordinate=this.piecePosition + (this.pieceAlliance.getDirection()*NUM_TILES_PER_ROW);
                    if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                       !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                        legalMoves.add(new PawnJump(board,this,candidateDestinationCoordinate));
                    }
                }
                // Left Direction attacking move
                if(candidateOffset == 7 && 
                        !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack() ||
                          (BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()))) ){
                    if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                        final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate))
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                            else    
                                legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                    else if(board.getEnPassantPawn() != null){
                        if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))){
                            final Piece pieceOnCandidate = board.getEnPassantPawn();
                            if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                                legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
                // Right Direction attacking move
                if(candidateOffset == 9 && 
                        !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                          (BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) ){
                    if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                        final Piece pieceOnCandidate=board.getTile(candidateDestinationCoordinate).getPiece();
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate))
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                            else    
                                legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                    else if(board.getEnPassantPawn() != null){
                        if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))){
                            final Piece pieceOnCandidate = board.getEnPassantPawn();
                            if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                                legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }
    
    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
    
    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
    
    public Piece getPromotionPiece(){
        if((this.pieceAlliance == Alliance.WHITE && Table.get().getGameSetup().getWhitePlayerType()==PlayerType.HUMAN) ||
           (this.pieceAlliance == Alliance.BLACK && Table.get().getGameSetup().getBlackPlayerType()==PlayerType.HUMAN)){
            return Table.get().getPawnPromotionPanel().getPromotedPiece(this.piecePosition, this.pieceAlliance);
        }
        else
            return new Queen(this.piecePosition, this.pieceAlliance, false);        
    }
}