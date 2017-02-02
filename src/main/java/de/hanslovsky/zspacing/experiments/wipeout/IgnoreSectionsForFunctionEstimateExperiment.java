package de.hanslovsky.zspacing.experiments.wipeout;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.janelia.thickness.inference.InferFromMatrix;
import org.janelia.thickness.inference.Options;
import org.janelia.thickness.inference.fits.GlobalCorrelationFitAverage;
import org.janelia.thickness.inference.visitor.LazyVisitor;
import org.janelia.thickness.lut.LUTRealTransform;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class IgnoreSectionsForFunctionEstimateExperiment
{

	public static void main( final String[] args ) throws Exception
	{
		new ImageJ();

		final Options o = Options.generateDefaultOptions();
		o.comparisonRange = 50;
		o.nIterations = 100;
		o.withReorder = false;
		o.shiftProportion = 0.6;
		o.minimumSectionThickness = 1e-9;
		o.regularizationType = InferFromMatrix.RegularizationType.BORDER;
		o.scalingFactorRegularizerWeight = 0.1;
		o.coordinateUpdateRegularizerWeight = 0.0;
		o.scalingFactorEstimationIterations = 10;
		o.forceMonotonicity = true; // very important here!

		//		final String matrixPath = System.getProperty( "user.home" ) + "/Dropbox/misc/matrix.tif";
		final String matrixPath = "resources/matrix.tif";
		final int wipeStart = 200;
		final int wipeStop = 300;

		final FloatProcessor fp1 = new ImagePlus( matrixPath ).getProcessor().convertToFloatProcessor();
		final FloatProcessor fp2 = new ImagePlus( matrixPath ).getProcessor().convertToFloatProcessor();
		final int w = fp1.getWidth();
		final int h = fp1.getHeight();
		final int[] indices = IntStream.range( 0, w ).toArray();

		WipeMatrix.wipe( fp2, o.comparisonRange, wipeStart, wipeStop );

		final ArrayImg< FloatType, FloatArray > m1 = ArrayImgs.floats( ( float[] ) fp1.getPixels(), w, h );

		ImageJFunctions.show( m1, "original matrix" );

		final InferFromMatrix inf = new InferFromMatrix( new GlobalCorrelationFitAverage() );

		final double[] startingCoordinates = IntStream.range( 0, w ).mapToDouble( i -> i ).toArray();

		final double[] t1 = inf.estimateZCoordinates( m1, startingCoordinates.clone(), o );
		final double[] t2 = inf.estimateZCoordinates(
				m1,
				startingCoordinates.clone(),
				new double[0],
				Arrays.stream( new double[w] ).map( d -> 1.0 ).toArray(),
				Arrays.stream( indices ).mapToDouble( i -> i < wipeStart || i >= wipeStop ? 1.0 : 0.0 ).toArray(),
				Arrays.stream( new double[w] ).map( d -> 1.0 ).toArray(),
				new LazyVisitor(),
				o );

		final LUTRealTransform tf1 = new LUTRealTransform( t1, 2, 2 );
		final LUTRealTransform tf2 = new LUTRealTransform( t2, 2, 2 );

		final NLinearInterpolatorFactory< FloatType > fac = new NLinearInterpolatorFactory<>();
		final FloatType oob = new FloatType( Float.NaN );

		final IntervalView< FloatType > o1 = Views.interval( Views.raster( RealViews.transform( Views.interpolate( Views.extendValue( m1, oob ), fac ), tf1 ) ), m1 );
		final IntervalView< FloatType > o2 = Views.interval( Views.raster( RealViews.transform( Views.interpolate( Views.extendValue( m1, oob ), fac ), tf2 ) ), m1 );

		ImageJFunctions.show( o1, "warped matrix" );
		ImageJFunctions.show( o2, "warped matrix from wipeout transform" );

	}

}
