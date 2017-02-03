package de.hanslovsky.zspacing.experiments;

import java.util.Arrays;

import org.janelia.utility.OuterProductView;

import ij.ImageJ;
import net.imglib2.converter.RealDoubleConverter;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;

public class OPVTest
{

	public static void main( final String[] args )
	{

		final double[] arr1 = new double[] { 1, 2, 3 };

		final double[] arr2 = new double[] { 1, 2 };

		final ArrayImg< DoubleType, DoubleArray > a1 = ArrayImgs.doubles( arr1, arr1.length );

		final ArrayImg< DoubleType, DoubleArray > a2 = ArrayImgs.doubles( arr2, arr2.length );

		final OuterProductView< DoubleType, DoubleType, DoubleType > opv = new OuterProductView<>( a1, a2, new RealDoubleConverter<>(), new RealDoubleConverter<>(), new DoubleType() );

		System.out.println( Arrays.toString( Intervals.dimensionsAsLongArray( opv ) ) );

		final long[] min = new long[ opv.numDimensions() ];
		opv.min( min );

		new ImageJ();
		ImageJFunctions.show( opv, "opv" );

	}

}
