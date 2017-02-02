package de.hanslovsky.zspacing.experiments;

import java.util.stream.IntStream;

import org.janelia.thickness.inference.InferFromMatrix;
import org.janelia.utility.MatrixStripConversion;

import ij.ImageJ;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.DoubleType;

public class WeightStripExperiment
{

	public static void main( final String[] args )
	{

		final int n = 20;
		final double[] weights = IntStream.range( 0, n ).mapToDouble( i -> ( i + 1 ) / ( n + 1.0 ) ).toArray();

		final int range = 15;

		final ArrayImg< DoubleType, DoubleArray > strip = ArrayImgs.doubles( 2 * range + 1, n );

		InferFromMatrix.fillWeightStrip( strip, weights, new DoubleType() );

		new ImageJ();
		ImageJFunctions.show( strip, "strip" );
		ImageJFunctions.show( MatrixStripConversion.stripToMatrix( strip, new DoubleType() ) );

	}

}
