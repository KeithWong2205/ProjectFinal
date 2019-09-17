package com.keith.oop;

import com.keith.oop.PlayableBrick.Brick;			//Import Brick form PlayableBrick to add to the GameBoard
import javax.swing.JLabel;									
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class StackingBoard extends JPanel 
{
	/**Version 1.0
	 * Full function - little to none animation
	 */
	private static final long serialVersionUID = 1L;
	private final int Height = 26;							//Setting the Height of the stacking board
	private final int Width = 12;							//Setting the Width of the stacking board
	private final int Duration = 400;						//Speed of the game (Higher number = slower brick and vice versa)
	
	private Timer timer;									//Timer for the Game
	private boolean stopFalling = false;					//Brick falling state
	private boolean Paused = false;							//Game state
	private int ClearedLine = 0;							//Score
	private int BrickCurrentX = 0;							//Brick current X coordinate
	private int BrickCurrentY = 0;							//Brick current Y coordinate
	private JLabel StatsBar;								//StatsBar
	private PlayableBrick CurrentBrick;						//Brick  attributes inherit from PlayableBrick
	private Brick[] gameboard;								//GameBoard array storing Bricks
	
	public StackingBoard(MainGameControl parent)			//Getting Control inheritance from MainGameControl
	{
		initStackingBoard(parent);
	}
	
	private void initStackingBoard(MainGameControl parent)	//Initialize UI
	{
		setFocusable(true);									//Set Focus for Event
		StatsBar = parent.getStatsBar();					//Get StatsBar from MainGameControl
		addKeyListener(new KeyRegcogniser());				//KeyListener for key press
	}
	
	private int BlockWidth() 								//Setting Width of blocks making up Bricks
	{
		return (int) getSize().getWidth() / Width;
	}
	private int BlockHeight()
	{
		return (int) getSize().getHeight() / Height;		//Setting Height of blocks making up Bricks
	}
	
	private Brick CurrentShapeAt(int x, int y)				//Getting the shape of the brick at a certain coordinates
	{
		return gameboard[(y * Width) + x];
	}
	
	void GameStart()										//GameStarting...
	{
		CurrentBrick = new PlayableBrick();					//Setting new Brick
		gameboard = new Brick[Width * Height];				//Setting new GameBoard
		
		CleanSlate();										//Clean the board
		newBrick();											//Generate new Brick
		
		timer = new Timer(Duration, new GamePlay());		//New Timer for GamePlay
		timer.start();										//Start Timer
	}
	
	private void GamePaused()								//GamePause...
	{
		Paused = !Paused;
		
		if (Paused)											//Paused!
		{
			StatsBar.setText("Game is Paused !!");
		} else												//Un-Paused
		{
			var statlabel = String.format("Lines Cleared: %d", ClearedLine);
			StatsBar.setText(statlabel);
		}
		repaint();											//Repaint StatsBar 
	}
	
	@Override 
	public void paintComponent(Graphics g)				//Declare and initialize paintComponent for drawing
	{
		super.paintComponent(g);
		DrawBoard(g);
	}
	
	private void DrawBoard(Graphics g)				//Drawing the Board for the game
	{
		var boardsize = getSize();
		int boardTop = (int) boardsize.getHeight() - Height * BlockHeight();
		
		for (int i = 0; i < Height; i++)
		{
			for (int j = 0; j < Width; j++)
			{
				Brick shape = CurrentShapeAt(j, Height - i - 1);
				
				if (shape != Brick.None)
				{
					DrawBlock(g, j * BlockWidth(), boardTop + i * BlockHeight(), shape);
				}
			}
		}
		if (CurrentBrick.getShape() != Brick.None)
		{
			for (int i = 0; i < 4; i++)
			{
				int x = BrickCurrentX + CurrentBrick.x(i);
				int y = BrickCurrentY - CurrentBrick.y(i);
				
				DrawBlock(g, x * BlockWidth(), boardTop + (Height - y - 1) * BlockHeight(), CurrentBrick.getShape());
			}
		}
	}
	
	private void FullDropDown()				//Drop the Brick straight down to the bottom
	{
		int newY = BrickCurrentY;
		
		while (newY > 0)
		{
			if (!tryMovable(CurrentBrick, BrickCurrentX, newY - 1)) //This is to ensure that you ommit the drop 1 line
			{
				break;
			}
			newY--;
		}
		BrickDropped();
	}
	
	private void MoveDown()					//Move the Brick 1 line down
	{
		if (!tryMovable(CurrentBrick, BrickCurrentX, BrickCurrentY - 1))
		{
			BrickDropped();
		}
	}
	
	private void CleanSlate()				//Clean the board
	{
		for (int i = 0; i < Height * Width; i++)
		{
			gameboard[i] = Brick.None;
		}
	}
			
	private void BrickDropped()				//Function for moving (dropping the brick)
	{
		for (int i=0; i<4; i++)
		{
			int x = BrickCurrentX + CurrentBrick.x(i);
			int y = BrickCurrentY - CurrentBrick.y(i);
			gameboard[(y * Width) + x] = CurrentBrick.getShape();
		}
		ClearOneLine();
		if (!stopFalling) 					//Create new Brick when old one stopped falling
		{
			newBrick();
		}
	}
	
	private void newBrick()					//Generating a new Brick
	{
		CurrentBrick.setRandomShape();
		BrickCurrentX = Width / 2 + 1;
		BrickCurrentY = Height - 1 + CurrentBrick.minY();
		
		if (!tryMovable(CurrentBrick, BrickCurrentX, BrickCurrentY)) //This is when you can't move down the Brick anymore
		{
			CurrentBrick.setShape(Brick.None);
			timer.stop();
			
			var alert = String.format("Game Over !! Total Lines Cleared: %d", ClearedLine);
			StatsBar.setText(alert);
		}
	}
	
	private boolean tryMovable(PlayableBrick newBrick, int newX, int newY)		//Movement of the Brick
	{
		for (int i = 0; i < 4; i++) 
		{
			int x = newX + newBrick.x(i);
			int y = newY - newBrick.y(i);
			
			if (x < 0 || x >= Width || y < 0 || y >= Height)
			{
				return false;
				
			}
			
			if (CurrentShapeAt(x, y) != Brick.None)
			{
				return false;
			}
		}
		CurrentBrick = newBrick;
		BrickCurrentX = newX;
		BrickCurrentY = newY;
		
		repaint();
		
		return true;
	}
	
	private void ClearOneLine()						//Clear full line when it is filled
	{
		int LinesFilled = 0;
		
		for (int i = Height - 1; i >= 0; i--)
		{
			boolean FullLine = true;
			
			for (int j = 0; j < Width; j++)
			{
				if (CurrentShapeAt(j, i) == Brick.None)
				{
					FullLine = false;
					break;
				}
			}
			if (FullLine)
			{
				LinesFilled++;
				
				for (int k = i; k < Height - 1; k++)
				{
					for (int j = 0; j < Width; j++)
					{
						gameboard[(k * Width) + j] = CurrentShapeAt(j, k + 1);
					}
				}
			}
		}
		
		if (LinesFilled > 0)
		{
			ClearedLine += LinesFilled;
			var statlabel2 = String.format("Lines Cleared: %d", ClearedLine);
			StatsBar.setText(statlabel2);
			stopFalling = true;
			CurrentBrick.setShape(Brick.None);
		}
	}
	
	private void DrawBlock(Graphics g, int x, int y, Brick shape)				//Drawing the blocks make up the Bricks
	{
		Color BlockColors[] = 
		{
			new Color(0,0,0),
			new Color(220, 220, 70),
			new Color(220, 150, 70),
			new Color(220, 80, 70),
			new Color(70, 220, 210),
			new Color(120, 70, 220),
			new Color(120, 220, 70),
			new Color(220, 70, 140)
		};
		
		var blockcolor = BlockColors[shape.ordinal()];
		
		g.setColor(blockcolor);
		g.fillRect(x + 1, y + 1, BlockWidth() - 2, BlockHeight() - 2);
		
		g.setColor(blockcolor.brighter());
		g.drawLine(x, y + BlockHeight() - 1, x, y);
		g.drawLine(x, y, x + BlockWidth() - 1, y);
		
		g.setColor(blockcolor.darker());
		g.drawLine(x + 1, y + BlockHeight() - 1, x + BlockWidth() - 1, y + BlockHeight() - 1);
		g.drawLine(x + BlockWidth() -1, y + BlockHeight() - 1, x + BlockWidth() - 1, y + 1);
	}
	
	private class GamePlay implements ActionListener 			//GamePlay class inherits ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			IgniteGamePlay();
		}
	}
	
	private void IgniteGamePlay()								//New GamePlay initialization
	{	
		update();												//Update new board
		repaint();												//Re-painting new board
	}
	
	private void update()										
	{
		if (Paused)												//Paused
		{
			return;
		}
		if (stopFalling)										//When Brick finished falling
		{
			stopFalling = false;
			newBrick();
		} else
		{
			MoveDown();
		}
	}
		
	class KeyRegcogniser extends KeyAdapter				//Key Adapter for the control of the game
	{
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			if (CurrentBrick.getShape() == Brick.None)
			{
				return;
			}
			
			int keycode = e.getKeyCode();
			
			switch (keycode)
			{
				case KeyEvent.VK_DOWN -> MoveDown();
				case KeyEvent.VK_LEFT -> tryMovable(CurrentBrick, BrickCurrentX - 1, BrickCurrentY);
				case KeyEvent.VK_RIGHT -> tryMovable(CurrentBrick, BrickCurrentX + 1, BrickCurrentY);
				case KeyEvent.VK_UP -> GamePaused();
				case KeyEvent.VK_SPACE -> FullDropDown();
				case KeyEvent.VK_PERIOD -> tryMovable(CurrentBrick.RotateRight(), BrickCurrentX, BrickCurrentY);
				case KeyEvent.VK_COMMA -> tryMovable(CurrentBrick.RotateLeft(), BrickCurrentX, BrickCurrentY);
			}
		}
	}
}
