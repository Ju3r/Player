import javax.swing.*;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import java.util.Scanner;

public class Mp3ToWavConvert extends JFrame {
    public static void convert(String filePath) throws JavaLayerException {
        Converter myConverter = new Converter();
        myConverter.convert(filePath, filePath.replace(".mp3", ".wav"));
    }
}