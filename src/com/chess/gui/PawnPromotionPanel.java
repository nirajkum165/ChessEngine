package com.chess.gui;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
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

/**
 * 
 * @author Niraj Kr
 */
public class PawnPromotionPanel extends JDialog {
	/**
	* 
	*/
	private static final long serialVersionUID = 5498934180453292566L;
	private String pieceName;
	private final JRadioButton queenPieceButton;
	private final JRadioButton bishopPieceButton;
	private final JRadioButton knightPieceButton;
	private final JRadioButton rookPieceButton;

	public PawnPromotionPanel(final JFrame frame, final boolean modal) {
		super(frame, modal);
		final JPanel myPanel = new JPanel(new GridLayout(0, 1));
		queenPieceButton = new JRadioButton("Queen");
		bishopPieceButton = new JRadioButton("Bishop");
		knightPieceButton = new JRadioButton("Knight");
		rookPieceButton = new JRadioButton("Rook");

		final ButtonGroup pieceGroup = new ButtonGroup();
		pieceGroup.add(queenPieceButton);
		pieceGroup.add(bishopPieceButton);
		pieceGroup.add(knightPieceButton);
		pieceGroup.add(rookPieceButton);
		bishopPieceButton.setSelected(true);
		this.pieceName = "Bishop";

		final JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (queenPieceButton.isSelected())
					pieceName = "Queen";
				if (bishopPieceButton.isSelected())
					pieceName = "Bishop";
				if (knightPieceButton.isSelected())
					pieceName = "Knight";
				if (rookPieceButton.isSelected())
					pieceName = "Rook";
				PawnPromotionPanel.this.setVisible(false);
			}
		});

		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Cancel");
				PawnPromotionPanel.this.setVisible(false);
			}
		});

		getContentPane().add(myPanel);
		myPanel.add(new JLabel("Pawn Promotion"));
		myPanel.add(queenPieceButton);
		myPanel.add(bishopPieceButton);
		myPanel.add(knightPieceButton);
		myPanel.add(rookPieceButton);
		myPanel.add(okButton);
		myPanel.add(cancelButton);
		pack();
		setLocationRelativeTo(null);
		setVisible(false);
	}

	public void promptUser() {
		setVisible(true);
		repaint();
	}

	public Piece getPromotedPiece(final int piecePosition, final Alliance pieceAlliance) {
		switch (pieceName) {
		case "Queen":
			return new Queen(piecePosition, pieceAlliance, false);
		case "Bishop":
			return new Bishop(piecePosition, pieceAlliance, false);
		case "Knight":
			return new Knight(piecePosition, pieceAlliance, false);
		case "Rook":
			return new Rook(piecePosition, pieceAlliance, false);
		}
		throw new RuntimeException("Should not have reached here");
	}
}
