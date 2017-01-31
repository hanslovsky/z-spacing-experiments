package de.hanslovsky.zspacing.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.janelia.thickness.plugin.RealSumFloatNCC;

import ij.ImageStack;
import ij.process.FloatProcessor;

public class MatrixFromStack
{

	public static FloatProcessor generate( final ImageStack stack, final int range, final int nThreads ) throws InterruptedException, ExecutionException
	{
		final int height = stack.getSize();
		final ArrayList< Callable< Void > > callables = new ArrayList< >();
		final FloatProcessor matrix = new FloatProcessor( height, height );
		for ( int i = 0; i < height; ++i )
		{
			final int finalI = i;
			callables.add( new Callable< Void >()
			{

				@Override
				public Void call() throws Exception
				{
					for ( int k = finalI + 1; k - finalI <= range && k < height; ++k )
					{
						final float val = new RealSumFloatNCC( ( float[] ) stack.getProcessor( finalI + 1 ).getPixels(), ( float[] ) stack.getProcessor( k + 1 ).getPixels() ).call().floatValue();
						matrix.setf( finalI, k, val );
						matrix.setf( k, finalI, val );
					}
					return null;
				}
			} );
		}
		final ExecutorService es = Executors.newFixedThreadPool( nThreads );
		final List< Future< Void > > futures = es.invokeAll( callables );
		for ( final Future< Void > f : futures )
			f.get();

		return matrix;

	}

}
