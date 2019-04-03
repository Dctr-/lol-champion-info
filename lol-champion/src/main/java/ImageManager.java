import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageManager {
    private static ExecutorService pool = Executors.newFixedThreadPool(20);
    private static java.util.List<Callable<Pair<String, ImageView>>> tasks = new ArrayList<>();

    private static HashMap<String, ImageView> imageMap = new HashMap<>();
    private static String path = "";

    static {
        String findPath = "";
        try {
            findPath = (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath();
            if(!findPath.endsWith("/")) {
                findPath += "/";
            }
            findPath += "lol-champion/images/";
            File pathDir = new File(findPath);
            if(!pathDir.exists()) {
                Files.createDirectories(Paths.get(pathDir.toURI()));
            }
            path = findPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets the ImageView object for a previously downloaded image
     *
     * @param fileName
     *        the fileName matching the fileName from {@link ImageManager#queueImageDownload(java.lang.String, java.lang.String, int, int)}
     *
     * @return the ImageView corresponding to the URL, if the image doesn't exist null will be returned
     */
    public static ImageView getImage(String fileName) {
        return imageMap.getOrDefault(fileName, null);
    }


    /**
     * Queues an image to be later downloaded with {@link ImageManager#startImageDownload()}
     *
     * @param url
     *        the url matching an image to be downloaded
     *
     * @param fileName
     *        the name the file will be saved locally under, without extension
     *
     * @param width
     *        the expected width of the image
     *
     * @param height
     *        the expected height of the image
     */
    public static void queueImageDownload(String url, String fileName, int width, int height) {
        tasks.add(() -> {
            String fileExtension = url.substring(url.lastIndexOf(".") + 1);
            ImageView imageView;
            File icon = new File(path + fileName + "." + fileExtension);
            if(!icon.exists()) {
                Image newImage = new Image(url, width, height, true, false);
                imageView = new ImageView(newImage); //Creates the image of champion, pulled from riot website
                File imageFile = new File(path + fileName + "." + fileExtension);
                if(!imageFile.getParentFile().exists()) {
                    imageFile.getParentFile().mkdirs();
                }
                ImageIO.write(SwingFXUtils.fromFXImage(newImage, null), fileExtension, imageFile);
            } else {
                imageView = new ImageView(new Image(icon.toURI().toString()));
            }
            return new Pair(fileName, imageView);
        });
    }


    /**
     * Starts downloading all the images. Images can be later accessed
     * using {@link ImageManager#getImage(java.lang.String)}
     */
    public static void startImageDownload() {
        try {
            List<Future<Pair<String, ImageView>>> results = pool.invokeAll(tasks);
            for (Future<Pair<String, ImageView>> result : results) {
                imageMap.put(result.get().getKey(), result.get().getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
