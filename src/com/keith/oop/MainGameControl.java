package com.keith.oop;

import java.awt.BorderLayout;					//BorderLayout for StatusBar
import java.awt.EventQueue;					//EventQueue for running Game
import javax.swing.JFrame;					//JFrame for UI
import javax.swing.JLabel;					//JLabel for StatusBar content

public class MainGameControl extends JFrame
{
	/**Version 1
	 * No Sound - Simple Animation
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel StatsBar;
	
	public MainGameControl()				//Constructor for MainGameControl
	{
		initUI();
	}
	
	private void initUI()					//UI initializations
	{
		StatsBar = new JLabel("Game On !! Lines Cleared: 0");			//Label of the StatsBar
		add(StatsBar, BorderLayout.NORTH);					//Position of the StatsBar-TOP
		StatsBar.setHorizontalAlignment(JLabel.CENTER);				//Alignment for the content of StatsBar
		
		var gameboard = new StackingBoard(this);				//Set up new GameBoard
		add(gameboard);								//Add GameBoard to set of Objects
		gameboard.GameStart();							//Starting the Game
		
		setTitle("This is the new Tetris");					//Window title
		setSize(400, 800);							//Window size
		setDefaultCloseOperation(EXIT_ON_CLOSE);				//Exit Protocol
		setLocationRelativeTo(null);
	}
	
	JLabel getStatsBar()								//Getting the StatsBar
	{
		return StatsBar;
	}
	
	public static void main(String[] args)						//Main function for JVM
	{
		EventQueue.invokeLater(() ->						//Put the Game in an EventQueue
		{
			var game = new MainGameControl();				//New Game
			game.setVisible(true);						//Set Game Visible
		});
	}

}
