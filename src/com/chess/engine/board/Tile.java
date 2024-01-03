package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Niraj Kr
 */
public abstract class Tile {
    protected final int tileCoordinate;
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();
    
    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles(){
        final Map<Integer, EmptyTile> emptyTileMap=new HashMap<>();
        for(int i=0; i<BoardUtils.NUM_TILES; i++){
            emptyTileMap.put(i,new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }
     
    public static Tile createTile(final int tileCoordinate, final Piece piece){
        return piece!=null ? new OccupiedTile(tileCoordinate, piece): EMPTY_TILES_CACHE.get(tileCoordinate);
    }
    
    private Tile(final int tileCoordinate){
        this.tileCoordinate=tileCoordinate;
    }
    
    public abstract Piece getPiece();
    public abstract boolean isTileOccupied();
    
    public int getTileCoordinate(){
        return this.tileCoordinate;
    }
    
    public static final class EmptyTile extends Tile{
        private EmptyTile(final int Coordinate){
            super(Coordinate);
        }
        
        @Override
        public String toString(){
            return "-";
        }
        
        @Override
        public boolean isTileOccupied(){
            return false;
        }
        
        @Override
        public Piece getPiece(){
            return null;
        }
    }
    
    public static final class OccupiedTile extends Tile{
        private final Piece pieceontile;
        
        private OccupiedTile(final int tileCoordinate,final Piece pieceontile){
            super(tileCoordinate);
            this.pieceontile=pieceontile;
        }
        
        @Override
        public String toString(){
            return getPiece().getPieceAlliance().isBlack() 
                    ? getPiece().toString().toLowerCase() 
                    : getPiece().toString();
        }
        
        @Override
        public boolean isTileOccupied(){
            return true;
        }
        
        @Override
        public Piece getPiece(){
            return this.pieceontile;
        }
    }
}