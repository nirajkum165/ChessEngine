package com.chess.gui;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.AI.MiniMax;
import com.chess.engine.player.AI.MoveStrategy;
import com.chess.pgn.FenUtilities;
import com.google.common.collect.Lists;

/**
 *
 * @author Niraj Kr
 */
public class Table extends Observable {
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private final Themes themes;
    private final PawnPromotionPanel pawnPromotionPanel;
    
    private Board chessBoard;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    
    private Move computerMove;
    private boolean highLightLegalMoves;
    
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");
    
    private static final Table INSTANCE = new Table();
    
    private Table(){
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // || WindowsConstants.DESPOSE_ON_CLOSE
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardDirection = BoardDirection.NORMAL;
        this.highLightLegalMoves = true;
        this.boardPanel=new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        //Need to ponder on themes
        this.themes = new Themes(this.gameFrame, true);
        this.pawnPromotionPanel = new PawnPromotionPanel(this.gameFrame, true);
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setLocationRelativeTo(null);
        this.gameFrame.setVisible(true);
    }
    
    public static Table get(){
        return INSTANCE;
    }
    
    public void show(){
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }
    
    public GameSetup getGameSetup(){
        return this.gameSetup;
    }
    
    public PawnPromotionPanel getPawnPromotionPanel(){
        return this.pawnPromotionPanel;
    }
    
    public JFrame getGameFrame(){
        return this.gameFrame;
    }
    
    public Board getGameBoard(){
        return this.chessBoard;
    }
    
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openFEN = new JMenuItem("Load FEN File", KeyEvent.VK_F);
        openFEN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String fenString = JOptionPane.showInputDialog("Input FEN");
                Table.get().gameSetup.resetGamePlayer();
                chessBoard = FenUtilities.createGameFromFEN(fenString);
                Table.get().getBoardPanel().drawBoard(chessBoard);
            }
        });
        fileMenu.add(openFEN);
        
        fileMenu.addSeparator();
        
        final JMenuItem saveFEN = new JMenuItem("Save Game", KeyEvent.VK_F);
        saveFEN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                File save=new File("save/save.txt").getAbsoluteFile();
                try {
                    FileOutputStream fos = new FileOutputStream(save);
                    fos.write(FenUtilities.createFENFromGame(Table.get().getGameBoard()).getBytes());
                    fos.close();
                } 
                catch (FileNotFoundException ex) {
                    System.out.print("Could not find file");
                }
                catch (IOException ex) {
                    System.out.print("IO Exception");
                }
            }
        });
        fileMenu.add(saveFEN);
        
        fileMenu.addSeparator();
        
        final JMenuItem getSavedFEN = new JMenuItem("Resume Game", KeyEvent.VK_F);
        getSavedFEN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                File save=new File("save/save.txt").getAbsoluteFile();
                try {
                    BufferedReader brTest = new BufferedReader(new FileReader(save));
                    String fenString = brTest.readLine();
                    System.out.print(fenString);
                    chessBoard = FenUtilities.createGameFromFEN(fenString);
                    Table.get().getBoardPanel().drawBoard(chessBoard);
                } 
                catch (FileNotFoundException ex) {
                    System.out.print("Could not find file");
                }
                catch (IOException ex) {
                    System.out.print("IO Exception");
                }
            }
        });
        fileMenu.add(getSavedFEN);
        
        fileMenu.addSeparator();
        
        final JMenuItem exitMenuButton = new JMenuItem("Exit");
        exitMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameFrame().dispose();
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuButton);
        return fileMenu;
    }
    
    private JMenu createPreferencesMenu(){ 
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        
        preferencesMenu.addSeparator();
        
        final JMenuItem themesMenuItem = new JMenuItem("Themes");
        themesMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                themes.promptUser();
            }
            
        });
        preferencesMenu.add(themesMenuItem);
        
        preferencesMenu.addSeparator();
        
        final JCheckBoxMenuItem legalMoveHighLighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", true);
        legalMoveHighLighterCheckbox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                highLightLegalMoves = legalMoveHighLighterCheckbox.isSelected();
            }
            
        });
        preferencesMenu.add(legalMoveHighLighterCheckbox);
        return preferencesMenu;
    }
    
    private JMenu createOptionsMenu(){
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().gameSetup.resetGamePlayer();
                Themes.defaultPieceImagePath = "art/pieces/";
                Table.get().getBoardPanel().drawBoard(Board.createStandardBoard());
            }
        });
        optionsMenu.add(newGame);
        
        optionsMenu.addSeparator();
        
        final JMenuItem undoLastMove = new JMenuItem("Undo Move");
        undoLastMove.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Table.get().getMoveLog().size() > 0) {
                    undoLastMove();
                }
            }
        });
        optionsMenu.add(undoLastMove);
        
        optionsMenu.addSeparator();
        
        final JMenuItem pawnPromotion = new JMenuItem("Promotion Preference");
        pawnPromotion.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getPawnPromotionPanel().promptUser();
            }
        });
        optionsMenu.add(pawnPromotion);
        
        optionsMenu.addSeparator();
        
        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }
    
    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);
    }
    
    //Need to Understand
    private void undoLastMove() {
        final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
        this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getTransitionBoard();
        this.computerMove = null;
        Table.get().getMoveLog().removeMove(lastMove);
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(chessBoard);
        //Game History Panel
    }

    private static class TableGameAIWatcher implements Observer{

        @Override
        public void update(final Observable o, final Object arg) {
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) && 
                !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                !Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
                   
            if(Table.get().getGameBoard().currentPlayer().isInCheckMate())
                System.out.println("game over, " + Table.get().getGameBoard().currentPlayer() + "  is in Check-Mate !!");
            
            if(Table.get().getGameBoard().currentPlayer().isInStaleMate())
                System.out.println("game over, " + Table.get().getGameBoard().currentPlayer() + " is in Stale-Mate !!");
        }
    }
    
    public void updateGameBoard(final Board board){
        this.chessBoard = board;
    }
    
    public void updateComputerMove(final Move move){
        this.computerMove = move;
    }
    
    public MoveLog getMoveLog(){
        return this.moveLog;
    }
    
    private GameHistoryPanel getGameHistoryPanel(){
        return this.gameHistoryPanel;
    }
    
    private TakenPiecesPanel getTakenPiecesPanel(){
        return this.takenPiecesPanel;
    }
    public BoardPanel getBoardPanel(){
        return this.boardPanel;
    }
    
    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }
    
    private static class AIThinkTank extends SwingWorker<Move, String>{
        private AIThinkTank(){}

        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy miniMax = new MiniMax(Table.get().getGameSetup().getSearchDepth());
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }
        
        @Override
        public void done(){
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);     
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
            } 
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    
    public enum BoardDirection{
        NORMAL{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
            
        },
        FLIPPED{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }
    
    public class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;
        
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i=0; i<BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }
        
        public void drawBoard(final Board board){
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }
    
    public static class MoveLog{
        private final List<Move> moves;
        
        MoveLog(){
            this.moves = new ArrayList<>();
        }
        
        public List<Move> getMoves(){
            return this.moves;
        }
        
        public void addMove(final Move move){
            this.moves.add(move);
        }
        
        public int size(){
            return this.moves.size();
        }
        
        public void clear(){
            this.moves.clear();
        }
        
        public Move removeMove(int index){
            return this.moves.remove(index);
        }
        
        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }
    
    public enum PlayerType{
        HUMAN,
        COMPUTER
    }
    
    public class TilePanel extends JPanel{
        private final int tileId;
        TilePanel(final BoardPanel boardPanel, final int tileId){
            super(new GridBagLayout());   //For some kind of decoration available of GridBagLayout
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            addMouseListener(new MouseListener(){
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    }
                    else if(isLeftMouseButton(e)){
                        if(sourceTile == null){
                            //First click
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if(humanMovedPiece == null)
                                sourceTile = null;
                        }
                        else{
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, 
                                                                          sourceTile.getTileCoordinate(), 
                                                                          destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);    
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                       @Override
                        public void run() {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            takenPiecesPanel.redo(moveLog);
                            if(gameSetup.isAIPlayer(chessBoard.currentPlayer()));
                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                            boardPanel.drawBoard(chessBoard);
                        }
                    });
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                    
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    
                }
            });
            validate();
        }

        public void drawTile(final Board board){
            assignTileColor();
            assignTilePieceIcon(board);
            highlightAIMove();
            highlightTileBorder(board);
            highlightLegals(board);
            validate();
            repaint();
        }
        
        public void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    // White Bishop = "WB.gif"
                    final BufferedImage image =
                            ImageIO.read(new File(Themes.defaultPieceImagePath + board.getTile(tileId).getPiece().getPieceAlliance().toString().substring(0, 1) +
                                    board.getTile(tileId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void highlightAIMove() {
            if(computerMove != null) {
                if(this.tileId == computerMove.getCurrentCoordinate()) {
                    setBackground(Color.LIGHT_GRAY);
                } 
                else if(this.tileId == computerMove.getDestinationCoordinate()) {
                    setBackground(new Color(240,46,9));
                }
            }
        }
        
        private void highlightTileBorder(final Board board) {
            if(humanMovedPiece != null &&
               humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance() &&
               humanMovedPiece.getPiecePosition() == this.tileId) {
                setBorder(BorderFactory.createLineBorder(Color.cyan));
            } 
            else {
                setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        }
        
        private void highlightLegals(final Board board){
            if(highLightLegalMoves){
                for(final Move move : pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate() == this.tileId){
                        try{
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
        private Collection<Move> pieceLegalMoves(final Board board){
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }
        
        private void assignTileColor() {
            if(BoardUtils.EIGHTH_ROW[this.tileId] ||
               BoardUtils.SIXTH_ROW[this.tileId] ||
               BoardUtils.FOURTH_ROW[this.tileId] ||
               BoardUtils.SECOND_ROW[this.tileId]){
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            }
            else if(BoardUtils.SEVENTH_ROW[this.tileId] ||
               BoardUtils.FIFTH_ROW[this.tileId] ||
               BoardUtils.THIRD_ROW[this.tileId] ||
               BoardUtils.FIRST_ROW[this.tileId]){
                setBackground(this.tileId % 2 == 0 ? darkTileColor : lightTileColor);
            }
        }
    }
}