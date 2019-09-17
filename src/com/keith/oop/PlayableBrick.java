package com.keith.oop;

import java.util.Random;										//Java Random Generator

public class PlayableBrick 
{
	protected enum Brick										//Enumerators for shapes of Bricks
	{
		None, Ziczac, SLine, Sticky, TPipe, Block, InvertedLNinety, LNinety
	}
	
	private Brick styles;										//Shape variable
	
	private int [][] coordinates;								//Coordinates variable for shapes

	public PlayableBrick()										//Constructor of PlayableBrick
	{
		coordinates = new int [4][2];
		setShape(Brick.None);
	}
	
	void setShape (Brick shape)									//Setting Shape of Bricks
	{
		int [][][] coordinatesTable = new int [][][] 
		{
			{{0, 0}, {0, 0}, {0, 0}, {0, 0}},					//root point (No shape)
			{{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},				//upright S-shape
			{{0, -1}, {0, 0}, {1, 0}, {1, 1}},					//upright Z-shape
			{{0, -1}, {0, 0}, {0, 1}, {0, 2}},					//upright I-shape
			{{-1 ,0}, {0, 0}, {1, 0}, {0, 1}},					//flat T-shape
			{{0, 0}, {1, 0}, {0, 1}, {1, 1}},					//square shape
			{{-1, -1}, {0, -1}, {0, 0}, {0, 1}},				//upright inverted L-shape
			{{1, -1}, {0, -1}, {0, 0}, {0, 1}}					//upright L-shape
		};
		 
		for (int i = 0; i < 4; i++)								//Correspond CoordinateTable to Enumerators
		{
			System.arraycopy(coordinatesTable[shape.ordinal()], 0, coordinates, 0, 4);
		}
		styles = shape;	
	}

	private void setX(int index, int x)							//Setting new X for rotated Bricks
	{
		coordinates[index][0] = x; 
	}
	private void setY(int index, int y)							//Setting new Y for rotated Bricks
	{
		coordinates[index][1] = y;
	}
	int x(int index)											//Getting new X of rotated Bricks
	{
		return coordinates[index][0];
	}	
	int y(int index)											//Getting new Y f rotated Bricks
	{
		return coordinates[index][1];
	}

	Brick getShape()											//Get Shape of Bricks
	{
		return styles;
	}

	void setRandomShape()										//Setting Random Shape for Bricks
	{
		var rand = new Random();
		int x = Math.abs(rand.nextInt()) % 7 + 1;
		
		Brick[] values = Brick.values();
		setShape(values[x]);				
	}

	int minY() 													//Minimum Y to check Brick finished falling
	{
		int m = coordinates[0][1];
		
		for (int i = 0; i < 4; i++)
		{
			m = Math.min(m, coordinates[i][1]);
		}
		return m;
	}
	
	PlayableBrick RotateLeft()									//Brick Rotation to the Left
	{
		if ( styles == Brick.Block)								//If Shape is Block, ignore
		{
			return this;
		}
		var nextShape = new PlayableBrick();					//If otherwise, create new rotated Shape
		nextShape.styles = styles;
		
		for (int i = 0; i < 4; i++)
		{
			nextShape.setX(i, y(i));
			nextShape.setY(i, -x(i));
		}
		return nextShape;
	}

	PlayableBrick RotateRight()									//Brick Rotation to the Right
	{
		if ( styles == Brick.Block)								//If Shape is Block, ignore
		{
			return this;
		}
		var nextShape = new PlayableBrick();					//If otherwise, create new rotated Shape
		nextShape.styles = styles;
		
		for (int i = 0; i < 4; i++)
		{
			nextShape.setX(i, -y(i));
			nextShape.setY(i, x(i));
		}
		return nextShape;
	}
}
