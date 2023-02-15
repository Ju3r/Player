import javazoom.jl.decoder.JavaLayerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

class SearchMusicFromInternet {
    private static String songName;
    private static String songAuthor;
    private static String songHref;
    private static int choicedMusic;
    private static String query;
    private static Document doc;
    private static Scanner sc = new Scanner(System.in);
    private static Map<String,String> dictionaryMusics = new HashMap<String,String>();
    private static List <String> songsName = new ArrayList();
    private static List <String> songsAuthor = new ArrayList();
    private static List <String> songsHref = new ArrayList();
    private static boolean flag;

    SearchMusicFromInternet() throws IOException, JavaLayerException, UnsupportedAudioFileException, LineUnavailableException {
        String nameOfMusic = sc.nextLine();
        if (Player.flag == true) {
            nameOfMusic = sc.nextLine();
            flag = false;
        }

        this.query = nameOfMusic;

        searchMusic();
        choiceMusic();
    }

    public static void searchMusic() throws IOException {
//парсим страницу запроса
        try {
            doc = Jsoup.connect("https://ru.hitmotop.com/search?q=" + query).get();
        } catch (IOException ex) {
            System.out.println("Капут");
        }

        Elements nameOfSongs = doc.select("div.track__title");
        Elements authorOfSongs = doc.select("div.track__desc");
        Elements hrefOfSongs = doc.select("a.track__download-btn");

        for (Element headline : nameOfSongs) {
            songName = headline.ownText();
            songsName.add(songName);
        }

        for (Element headline : authorOfSongs) {
            songAuthor = headline.ownText();
            songsAuthor.add(songAuthor);
        }

        for (Element headline : hrefOfSongs) {
            songHref = headline.absUrl("href");
            songsHref.add(songHref);
        }
    }

    public static void choiceMusic() throws IOException, JavaLayerException, UnsupportedAudioFileException, LineUnavailableException {
        if (songsName.size() == 0){
            System.out.println("По данному запросу композиции не найдены");
        }
        else{
            System.out.println("Найденные композиции: ");
            for (int i = 0; i < songsName.size(); i++) {
                System.out.println((i + 1) + ". " + songsName.get(i) + " Автор: " + songsAuthor.get(i));
            }
            System.out.println("Выберите песню для скачивания: ");
            choicedMusic = sc.nextInt()-1;
            download(choicedMusic);
        }
    }

    private static void download(int x) throws IOException, JavaLayerException {
        System.out.println("НАЧИНАЕМ ЗАГРУЗКУ");
        BufferedInputStream inputStream = new BufferedInputStream(new URL(songsHref.get(x)).openStream());
        Files.copy(inputStream, Paths.get("music/" + songsName.get(x) + ".mp3"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("ПРОЦЕСС КОНВЕРТАЦИИ...");
        Mp3ToWavConvert myConverter = new Mp3ToWavConvert();
        myConverter.convert("music/" + songsName.get(x) + ".mp3");

        Files.delete(Path.of("music/" + songsName.get(x) + ".mp3"));

        Player.lst.add(songsName.get(x) + ".wav");
        System.out.println("Композиция добавлена в список музыки\n");

        songsName.clear();
        songsAuthor.clear();
        songsHref.clear();

        inputStream.close();
    }
}