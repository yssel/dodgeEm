import org.lwjgl.LWJGLUtil;
import java.io.File;

public class Main {
    public static void main(String[] args){
        System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());


    }
}
