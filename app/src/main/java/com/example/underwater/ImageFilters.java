package com.example.underwater;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.Random;


public class ImageFilters {

//Gamma Correction Algorithm Testing

    public  Bitmap applyMeanRemovalEffect(Bitmap src) {
        double[][] MeanRemovalConfig = new double[][] {
                { -1 , -1, -1 },
                { -1 ,  9, -1 },
                { -1 , -1, -1 }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(MeanRemovalConfig);
        convMatrix.Factor = 1;
        convMatrix.Offset = 0;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }
    public Bitmap applyUnderwaterEnhancementFilter(Bitmap src) {
        double[][] underwaterEnhancementConfig = new double[][] {
                { -1, -1, -1 },
                { -1,  9, -1 },
                { -1, -1, -1 }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(underwaterEnhancementConfig);
        convMatrix.Factor = 1;
        convMatrix.Offset = 0;

        // Apply contrast adjustment
//        Bitmap contrastAdjustedBitmap = applyContrastAdjustment(src);

        // Apply color correction
        Bitmap colorCorrectedBitmap = applyColorCorrection(src);

        // Apply the underwater enhancement filter
        return ConvolutionMatrix.computeConvolution3x3(colorCorrectedBitmap, convMatrix);
    }
    public Bitmap applyContrastAdjustment(Bitmap src) {
        // Get the dimensions of the source bitmap
        int width = src.getWidth();
        int height = src.getHeight();

        // Create a new bitmap for the adjusted result
        Bitmap adjustedBitmap = Bitmap.createBitmap(width, height, src.getConfig());

        // Iterate over each pixel in the source bitmap
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the color of the current pixel
                int pixel = src.getPixel(x, y);

                // Extract the RGB color components
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Apply contrast adjustment to each color component
                red = adjustContrastComponent(red);
                green = adjustContrastComponent(green);
                blue = adjustContrastComponent(blue);

                // Create the adjusted pixel color
                int adjustedPixel = Color.rgb(red, green, blue);

                // Set the adjusted pixel color to the corresponding pixel in the result bitmap
                adjustedBitmap.setPixel(x, y, adjustedPixel);
            }
        }

        return adjustedBitmap;
    }

    public int adjustContrastComponent(int component) {
        // Adjust the contrast of a single color component
        // Modify this method based on the specific contrast adjustment technique you want to apply

        // Example: Increase contrast by multiplying the component value by a factor
        int adjustedComponent = (int) (component * 1.5);

        // Ensure the component value stays within the valid range (0-255)
        adjustedComponent = Math.max(0, Math.min(255, adjustedComponent));

        return adjustedComponent;
    }

    public Bitmap applyColorCorrection(Bitmap src) {
        // Get the dimensions of the source bitmap
        int width = src.getWidth();
        int height = src.getHeight();

        // Create a new bitmap for the corrected result
        Bitmap correctedBitmap = Bitmap.createBitmap(width, height, src.getConfig());

        // Iterate over each pixel in the source bitmap
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the color of the current pixel
                int pixel = src.getPixel(x, y);

                // Extract the RGB color components
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Apply color correction to each color component
                red = correctColorComponent(red);
                green = correctColorComponent(green);
                blue = correctColorComponent(blue);

                // Create the corrected pixel color
                int correctedPixel = Color.rgb(red, green, blue);

                // Set the corrected pixel color to the corresponding pixel in the result bitmap
                correctedBitmap.setPixel(x, y, correctedPixel);
            }
        }

        return correctedBitmap;
    }

    public int correctColorComponent(int component) {
        // Correct a single color component
        // Modify this method based on the specific color correction technique you want to apply

        // Example: Apply a gamma correction to the component value
        double gamma = 1.8;
        int correctedComponent = (int) (255 * Math.pow(component / 255.0, gamma));

        // Ensure the component value stays within the valid range (0-255)
        correctedComponent = Math.max(0, Math.min(255, correctedComponent));

        return correctedComponent;
    }


    public static final double PI = 3.14159d;
    public static final double FULL_CIRCLE_DEGREE = 360d;
    public static final double HALF_CIRCLE_DEGREE = 180d;
    public static final double RANGE = 256d;

    public  Bitmap applyTintEffect(Bitmap src, int degree) {

        int width = src.getWidth();
        int height = src.getHeight();

        int[] pix = new int[width * height];
        src.getPixels(pix, 0, width, 0, 0, width, height);

        int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
        double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;

        int S = (int)(RANGE * Math.sin(angle));
        int C = (int)(RANGE * Math.cos(angle));

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int r = ( pix[index] >> 16 ) & 0xff;
                int g = ( pix[index] >> 8 ) & 0xff;
                int b = pix[index] & 0xff;
                RY = ( 70 * r - 59 * g - 11 * b ) / 100;
                GY = (-30 * r + 41 * g - 11 * b ) / 100;
                BY = (-30 * r - 59 * g + 89 * b ) / 100;
                Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
                RYY = ( S * BY + C * RY ) / 256;
                BYY = ( C * BY - S * RY ) / 256;
                GYY = (-51 * RYY - 19 * BYY ) / 100;
                R = Y + RYY;
                R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
                G = Y + GYY;
                G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
                B = Y + BYY;
                B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
                pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
            }

        Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());
        outBitmap.setPixels(pix, 0, width, 0, 0, width, height);

        pix = null;

        return outBitmap;
    }

    public static final int COLOR_MIN = 0x00;
    public static final int COLOR_MAX = 0xFF;



}
