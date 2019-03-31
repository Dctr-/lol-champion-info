import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        long before = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(20);
        java.util.List<Callable<Image>> tasks = new ArrayList<Callable<Image>>(args.length);
        for (int i = 0; i < 1000; i++) {
            tasks.add(new Callable<Image>() {
                @Override
                public Image call() throws Exception {
                    URL url = new URL("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Ahri_0.jpg");
                    Image image = ImageIO.read(url);
                    return image;
                }
            });
        }
        try {
            pool.invokeAll(tasks);
        } catch (Exception e) {
            System.out.println();
        }

        long time = System.nanoTime() - before;
        System.out.println("Duration in nanoseconds: " + time);
    }
}
