package com.ensicaen.facialdetectionapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import java.nio.ByteBuffer;

import kotlin.jvm.Synchronized;

public class YuvToRgbConverter {
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB scriptYuvToRgb;
    private int pixelCount = -1;
    private byte[] yuvBuffer = null;
    private Allocation inputAllocation = null;
    private Allocation outputAllocation = null;

    public YuvToRgbConverter(Context context) {
        rs = RenderScript.create(context);
        scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }

    @Synchronized
    public void yuvToRgb(Image image, Bitmap output) {
        // Ensure that the intermediate output byte buffer is allocated
        if (yuvBuffer == null) {
            pixelCount = image.getCropRect().width() * image.getCropRect().height();
            // Bits per pixel is an average for the whole image, so it's useful to compute the size
            // of the full buffer but should not be used to determine pixel offsets
            int pixelSizeBits = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888);
            yuvBuffer = new byte[pixelCount * pixelSizeBits / 8];
        }

        // Get the YUV data in byte array form using NV21 format
        imageToByteArray(image, yuvBuffer);

        // Ensure that the RenderScript inputs and outputs are allocated
        if (inputAllocation == null) {
            // Explicitly create an element with type NV21, since that's the pixel format we use
            Type elemType = new Type.Builder(rs, Element.YUV(rs)).setYuvFormat(ImageFormat.NV21).create();
            inputAllocation = Allocation.createSized(rs, elemType.getElement(), yuvBuffer.length);
        }
        if (outputAllocation == null) {
            outputAllocation = Allocation.createFromBitmap(rs, output);
        }

        // Convert NV21 format YUV to RGB
        inputAllocation.copyFrom(yuvBuffer);
        scriptYuvToRgb.setInput(inputAllocation);
        scriptYuvToRgb.forEach(outputAllocation);
        outputAllocation.copyTo(output);
    }


    private void imageToByteArray(Image image, byte[] outputBuffer) {
        assert (image.getFormat() == ImageFormat.YUV_420_888);

        Rect imageCrop = image.getCropRect();
        Image.Plane[] imagePlanes = image.getPlanes();

        for (int planeIndex = 0; planeIndex < imagePlanes.length; ++planeIndex) {
            Image.Plane plane = imagePlanes[planeIndex];
            int outputStride = 0;
            int outputOffset = 0;
            switch (planeIndex) {
                case 0:
                    outputStride = 1;
                    outputOffset = 0;
                    break;
                case 1:
                    outputStride = 2;
                    outputOffset = pixelCount + 1;
                    break;
                case 2:
                    outputStride = 2;
                    outputOffset = pixelCount;
                    break;
                default:
                    return;

            }

            ByteBuffer planeBuffer = plane.getBuffer();
            int rowStride = plane.getRowStride();
            int pixelStride = plane.getPixelStride();
            Rect planeCrop = imageCrop;
            if (planeIndex != 0) {
                planeCrop = new Rect(imageCrop.left / 2, imageCrop.top / 2, imageCrop.right / 2, imageCrop.bottom / 2);
            }

            int planeWidth = planeCrop.width();
            int planeHeight = planeCrop.height();

            byte[] rowBuffer = new byte[plane.getRowStride()];

            int rowLength = planeWidth;
            if (pixelStride != 1 || outputStride != 1) {
                rowLength = (planeWidth - 1) * pixelStride + 1;
            }
            for (int row = 0; row < planeHeight; row++) {
                planeBuffer.position((row + planeCrop.top) * rowStride + planeCrop.left * pixelStride);
                if (pixelStride == 1 && outputStride == 1) {
                    planeBuffer.get(outputBuffer, outputOffset, rowLength);
                    outputOffset += rowLength;
                } else {
                    planeBuffer.get(rowBuffer, 0, rowLength);
                    for (int col = 0; col < planeWidth; col++) {
                        outputBuffer[outputOffset] = rowBuffer[col * pixelStride];
                        outputOffset += outputStride;
                    }
                }
            }
        }
    }

}
