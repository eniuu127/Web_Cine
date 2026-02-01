package com.example.cinebooking.Util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QrCodeUtil {

    public static byte[] toPngBytes(String text, int size) {
        try {
            if (text == null) text = "";
            var hints = Map.of(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());

            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.QR_CODE, size, size, hints);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Generate QR failed", e);
        }
    }
}
