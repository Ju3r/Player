import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static Scanner scMain = new Scanner(System.in);
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException, JavaLayerException {
        Player app = new Player();
        System.out.println("*** Приветствую в консольном плеере! ***");
        System.out.println("Что хотите сделать?\n\n1. Выбрать песню из библиотеки\n2. Найти музыку в интернете");
        System.out.print("Ваш ответ: ");
        int number = scMain.nextInt();
        switch (number) {
            case 1:
                app.choiceOfMusic();
                break;
            case 2:
                app.searchMusicFromInternet();
                break;
        }
    }
}