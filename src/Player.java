import javazoom.jl.decoder.JavaLayerException;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/** Класс Player реализующий функции плеера
 * @author Владислав Шадрин
 * @author Иван Клюшин
 * @version 1.1
 */

public class Player {
    public static boolean flag = false;

    /** Поле директории папки с музыкой */
    private static File dir = new File("music\\");

    /** Поле считывающее данный момент в музыке */
    private static int currentFrame;

    /** Поле объекта clip для работы с аудио в Java */
    private static Clip clip;

    /** Поле для отображения функции старта */
    private static String position = "Старт";

    /** Поле состояния плеера */
    private static boolean isStart = false;

    /** Поле создания входного потока */
    private static AudioInputStream audioInputStream;

    /** Поле пути до файла */
    private static String filePath;

    /** Поле списка всех песен из папки с музыкой */
    public static List<String> lst = new ArrayList<>();

    /** Поле номера музыки, которая играет в данный момент */
    private static int numberOfMusic;
    
    /** Поле для проверки создания списка музыки */
    private static boolean listOfMusic = false;

    /** Поле для поддержки отрисовки главного меню */
    private static boolean openApp = true;

    private static boolean songFromInternet = false;

    private static Scanner scMain = new Scanner(System.in);

    public Player() throws IOException {
    }

    /** Метод создания списка всех композиций из папки с музыкой
     * @author Иван Клюшин
     * @return возвращает список всех композиций в папке с музыкой
     */

    public static List<String> createListOfMusic(){
        for (File file : dir.listFiles()) {
            if (file.isFile())
                lst.add(file.getName());
        }
        listOfMusic = true;
        return lst;
    }

    /** Метод открытия нового файла в плеере
     * @author Владислав Шадрин
     */
    public void createPlayer() throws UnsupportedAudioFileException, IOException, LineUnavailableException, JavaLayerException {
        position = "Старт";
        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        menu();
    }

    /** Метод, реализующий выбор композиции из списка
     * @author Иван Клюшин
     */
    public void choiceOfMusic() {
        try {
            if (listOfMusic == false) {
                createListOfMusic();
            }

            System.out.println("Выберите песню: ");
            for (int i = 0; i < lst.toArray().length; i++) {
                System.out.println((i + 1) + ". " + lst.get(i));
            }

            while (!scMain.hasNextInt()) {
                scMain.next();
                System.out.println("некорректный ввод! попробуйте ещё раз");
            }
            numberOfMusic = scMain.nextInt();

            filePath = "music\\" + lst.get(numberOfMusic-1);
            System.out.println();
            createPlayer();
        } catch (Exception e) {
            System.out.println("ПРОБЛЕМА С ВОСПРОИЗВЕДЕНИЕМ АУДИО");
            e.printStackTrace();
        }
    }

    /** Метод отрисовки консольного меню
     * @author Владислав Шадрин
     */
    public void menu() throws UnsupportedAudioFileException, LineUnavailableException, IOException, JavaLayerException {
            while (openApp) {
                System.out.println("1. " + position);
                System.out.println("2. Заново");
                System.out.println("3. Выбрать время");
                System.out.println("4. Следующая композиция");
                System.out.println("5. Предыдущая композиция");
                System.out.println("6. Выбор песни");
                System.out.println("7. Поиск музыки в интернете");
                System.out.println("8. Выход");

                while (!scMain.hasNextInt()) {
                    scMain.next();
                    System.out.println("некорректный ввод! попробуйте ещё раз");
                }

                int number = scMain.nextInt();
                choice(number);
                if (number == 8) {
                    System.out.println("ЗАКРЫВАЕМ ПЛЕЕР...");
                    openApp = false;
                }
            }
        }

    /** Метод реализующий возобновление и паузу (в зависимости от начального состояния)
     * @author Владислав Шадрин
     */
    public void play() {
        clip.start();
        isStart = true;

        if (position.equals("Пауза")) {
            isStart = false;
            this.currentFrame = (int) this.clip.getMicrosecondPosition();
            clip.stop();
            position = "Возобновить";
        } else if (isStart) {
            position = "Пауза";
        } else if (position.equals("Возобновить")){
            clip.stop();
        }
    }

    /** Метод запуска текущей композиции сначала
     * @author Владислав Шадрин
     */
    public void restart() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        clip.stop();
        clip.close();
        rewriteAudioStream();
        currentFrame = 0;
        clip.setMicrosecondPosition(0);
        position = "Возобновить";
        this.play();
    }

    /** Метод реализующий переход в конкретный момент времени
     * @author Владислав Шадрин
     */
    public static void jump(int c) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        if (c >= 0 && c < clip.getMicrosecondLength()) {
            clip.stop();
            clip.close();
            rewriteAudioStream();
            currentFrame = c;
            clip.setMicrosecondPosition(c);
            if (position.equals("Пауза")) {
                clip.start();
            } else {
                clip.stop();
            }
        }
    }

    /** Метод перезаписи потокового файла (подача на поток нового файла)
     * @author Владислав Шадрин
     */
    public static void rewriteAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        clip.open(audioInputStream);
    }

    /** Метод, реализующий закрытие текущей композиции (закрытие объекта clip, чтобы не было наложения)
     * @author Владислав Шадрин
     */
    public static void stop() {
        currentFrame = 0;
        clip.stop();
        clip.close();
    }

    /** Метод запуска следующей композиции из списка
     * @author Иван Клюшин
     */
    public void nextMusic() throws UnsupportedAudioFileException, LineUnavailableException, IOException, JavaLayerException {
        stop();
        if (numberOfMusic > lst.toArray().length - 1){
            numberOfMusic = 0;
        }
        filePath = "music\\" + lst.get(numberOfMusic);
        numberOfMusic++;
        createPlayer();
    }

    /** Метод запуска предыдущей композиции из списка
     * @author Иван Клюшин
     */
    public void previousMusic() throws UnsupportedAudioFileException, LineUnavailableException, IOException, JavaLayerException {
        stop();
        if (numberOfMusic - 2 < 0){
            numberOfMusic = lst.toArray().length + 1;
        }
        filePath = "music\\" + lst.get(numberOfMusic - 2);
        numberOfMusic--;
        createPlayer();
    }

    /** Метод выполнения выбранного действия из метода menu
     * @author Владислав Шадрин
     * @see Player menu
     */
    public void choice(int number) throws UnsupportedAudioFileException, LineUnavailableException, IOException, JavaLayerException {
        switch (number)
        {
            case 1:
                play();
                break;
            case 2:
                restart();
                break;
            case 3:
                int minutes = (int) (clip.getMicrosecondLength() / 1000000 / 60);
                int seconds = (int) (clip.getMicrosecondLength() / 1000000 % 60);
                System.out.println("Введите время через пробел (" + 0 + " " + 0 +
//                        ", " + minutes + " " + seconds + " " + clip.getMicrosecondLength() + " " + (minutes * 1000000 * 60 + seconds * 1000000) +  ")");
                        ", " + minutes + " " + seconds + ")");
                int c1 = scMain.nextInt();
                int c2 = scMain.nextInt();
                int c3 = (c1 * 1000000 * 60 + c2 * 1000000);
                jump(c3);
                break;
            case 4:
                nextMusic();
                break;
            case 5:
                previousMusic();
                break;
            case 6:
                stop();
                choiceOfMusic();
                break;
            case 7:
                System.out.println("Введите название песни или исполнителя: ");
                new SearchMusicFromInternet();
                flag = true;
                break;
            case 8:
                stop();
                break;
        }
    }
}