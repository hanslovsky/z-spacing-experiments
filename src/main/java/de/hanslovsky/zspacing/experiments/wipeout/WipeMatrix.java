package de.hanslovsky.zspacing.experiments.wipeout;

import ij.process.FloatProcessor;

public class WipeMatrix
{

	public static void wipe( final FloatProcessor matrix, final int range, final int wipeStart, final int wipeStop )
	{
		final int w = matrix.getWidth();
		final int h = matrix.getHeight();

		for ( int z = wipeStart; z < wipeStop; ++z )
			for ( int i = 1; i <= range; ++i )
				if ( z - i >= 0 ) {
					matrix.setf( z, z - i, Float.NaN );
					matrix.setf( z - i, z, Float.NaN );
				}

		for ( int z = wipeStop - range, r = 1; z < wipeStop - 1; ++z, ++r )
			for ( int i = range - r; i <= range; ++i )
				if ( z + i < w )
				{
					matrix.setf( z, z + i, Float.NaN );
					matrix.setf( z + i, z, Float.NaN );
				}


	}

}
