package com.chess.gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.player.Player;
import com.chess.gui.Table.PlayerType;


/**
 * 
 * @author Niraj Kr
 */
public class GameSetup extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 569019884729944098L;
	private static PlayerType whitePlayerType = PlayerType.HUMAN;
    private static PlayerType blackPlayerType = PlayerType.HUMAN;
    private final JRadioButton whiteHumanButton;
    private final JRadioButton blackHumanButton;
    private final JRadioButton whiteComputerButton;
    private final JRadioButton blackComputerButton;
    private JSpinner searchDepthSpinner;

    private static final String HUMAN_TEXT = "Human";
    private static final String COMPUTER_TEXT = "Computer";

    GameSetup(final JFrame frame,
              final boolean modal) {
        super(frame, modal);
        final JPanel myPanel = new JPanel(new GridLayout(0, 1));
        whiteHumanButton = new JRadioButton(HUMAN_TEXT);
        whiteComputerButton = new JRadioButton(COMPUTER_TEXT);
        blackHumanButton = new JRadioButton(HUMAN_TEXT);
        blackComputerButton = new JRadioButton(COMPUTER_TEXT);
        whiteHumanButton.setActionCommand(HUMAN_TEXT);
        
        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerButton);
        whiteHumanButton.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerButton);
        blackHumanButton.setSelected(true);

        getContentPane().add(myPanel);
        myPanel.add(new JLabel("White"));
        myPanel.add(whiteHumanButton);
        myPanel.add(whiteComputerButton);
        myPanel.add(new JLabel("Black"));
        myPanel.add(blackHumanButton);
        myPanel.add(blackComputerButton);

        this.searchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth ", new SpinnerNumberModel(4, 0, 12, 1));

        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Table.get().getBoardPanel().drawBoard(Board.createStandardBoard());
                whitePlayerType = whiteComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
                blackPlayerType = blackComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
                GameSetup.this.setVisible(false);
            }
        });
        
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
                GameSetup.this.setVisible(false);
            }
        });

        myPanel.add(okButton);
        myPanel.add(cancelButton);
        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    void promptUser() {
        setVisible(true);
        repaint();
    }

    boolean isAIPlayer(final Player player) {
        if(player.getAlliance() == Alliance.WHITE) {
            return getWhitePlayerType() == PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == PlayerType.COMPUTER;
    }

    public PlayerType getWhitePlayerType() {
        return whitePlayerType;
    }

    public PlayerType getBlackPlayerType() {
        return blackPlayerType;
    }

    private static JSpinner addLabeledSpinner(final Container c,
                                              final String label,
                                              final SpinnerModel model) {
        final JLabel l = new JLabel(label);
        c.add(l);
        final JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);
        return spinner;
    }

    public int getSearchDepth() {
        return (Integer)this.searchDepthSpinner.getValue();
    }
    
    public void resetGamePlayer(){
        whitePlayerType = PlayerType.HUMAN;
        blackPlayerType = PlayerType.HUMAN;
        this.whiteHumanButton.setSelected(true);
        this.blackHumanButton.setSelected(true);
    }
}
