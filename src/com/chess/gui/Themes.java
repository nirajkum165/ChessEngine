package com.chess.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * 
 * @author Niraj Kr
 */
public class Themes extends JDialog{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5951131245461873898L;
	
	private static AudioInputStream audioInputStream;
    private static Clip clip;
    private int songChoice;
    
    public static String defaultPieceImagePath = "art/pieces/";
    private static String defaultMusicPath = "music/";
    private static String fileName;
    private static int songPreference = 2;
    
    private final JRadioButton classicPieceButton;
    private final JRadioButton pieceStyleButton1;
    private final JRadioButton pieceStyleButton2;
    private final JRadioButton pieceStyleButton3;
    
    Themes(final JFrame frame, final boolean modal){
        super(frame,modal);
        final JPanel myPanel = new JPanel(new GridLayout(0, 1));
        classicPieceButton = new JRadioButton("Classic");
        pieceStyleButton1 = new JRadioButton("Style 1");
        pieceStyleButton2 = new JRadioButton("Style 2");
        pieceStyleButton3 = new JRadioButton("Style 3");
        
        final JComboBox<ComboItem> musicSelect = new JComboBox<>();
        musicSelect.addItem(new ComboItem(0, "Stop"));
        musicSelect.addItem(new ComboItem(1, "Creative Mind"));
        musicSelect.addItem(new ComboItem(2, "Drum Beat"));
        musicSelect.addItem(new ComboItem(3, "Electronic"));
        musicSelect.addItem(new ComboItem(4, "KBC Tune"));
        musicSelect.addItem(new ComboItem(5, "Piano"));
        musicSelect.setSelectedIndex(3);
        
        final ButtonGroup styleGroup = new ButtonGroup();
        styleGroup.add(classicPieceButton);
        styleGroup.add(pieceStyleButton1);
        styleGroup.add(pieceStyleButton2);
        styleGroup.add(pieceStyleButton3);
        classicPieceButton.setSelected(true);
        
        getContentPane().add(myPanel);
        myPanel.add(new JLabel("Pieces Styles"));
        myPanel.add(classicPieceButton);
        myPanel.add(pieceStyleButton1);
        myPanel.add(pieceStyleButton2);
        myPanel.add(pieceStyleButton3);
        myPanel.add(new JLabel("Music Options"));
        myPanel.add(musicSelect);
        
        final JButton okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> {
            if(pieceStyleButton1.isSelected()){
                defaultPieceImagePath = "art/Piece1/";
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
            }
            if(pieceStyleButton2.isSelected()){
                defaultPieceImagePath = "art/Piece2/";
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
            }
            if(pieceStyleButton3.isSelected()){
                defaultPieceImagePath = "art/Piece3/";
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
            }
            if(classicPieceButton.isSelected()){
                defaultPieceImagePath = "art/pieces/";
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
            }
            
            Object item = musicSelect.getSelectedItem();
            songChoice = ((ComboItem)item).getKey();
            
            if(songPreference != songChoice){
                songPreference = songChoice;
                try{
                    switch(songChoice){
                        case 0 : clip.stop();
                                 clip.close();
                            break;
                        case 1 : fileName = "CreativeMind.wav";
                            resetAudioStream();
                            break;
                        case 2 : fileName = "DrumBeat.wav";
                            resetAudioStream();
                            break;
                        case 3 : fileName = "Electronic.wav";
                            resetAudioStream();
                            break;
                        case 4 : fileName = "KBC.wav";
                            resetAudioStream();
                            break;
                        case 5 : fileName = "Piano.wav";
                            resetAudioStream();
                    }
                }
                catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Themes.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (IOException ex) {
                    Logger.getLogger(Themes.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (LineUnavailableException ex) {
                    Logger.getLogger(Themes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Themes.this.setVisible(false);
        });
        
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
                Themes.this.setVisible(false);
            }
        });
        
        myPanel.add(okButton);
        myPanel.add(cancelButton);
        setLocationRelativeTo(frame);
        pack();
        setVisible(false);

        try{
            simpleAudioPlayer();
            clip.start();
            Thread.sleep(0);
        } 
        catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
    
    class ComboItem{
        private int key;
        private String value;

        public ComboItem(int key, String value){
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString(){
            return value;
        }

        public int getKey(){
            return key;
        }

        public String getValue(){
            return value;
        }
    }
    
    void promptUser(){
        setVisible(true);
        repaint();
    }
    
    private void simpleAudioPlayer() throws LineUnavailableException  {
        clip = AudioSystem.getClip();
    }
    
    private void resetAudioStream() 
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        // create clip reference
        // create AudioInputStream object
        // open audioInputStream to the clip
        audioInputStream = AudioSystem.getAudioInputStream(new File(defaultMusicPath + fileName).getAbsoluteFile());
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }
}