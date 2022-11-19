package team6.server.handler;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import team6.server.socket.SocketHandler;

/**
 *
 * @author Administrator
 */
public class Monitor extends AbstractHandler {
    private SocketHandler socketHandler;
    private Robot robot;
    private Rectangle rectangle;

    public Monitor(SocketHandler socketHandler) throws AWTException, IOException {
        this.socketHandler = socketHandler;
        initialize();
        getMonitor();
    }
   
    private void initialize() throws AWTException {
        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screen = gEnv.getDefaultScreenDevice();
        rectangle = screen.getDefaultConfiguration().getBounds();
        robot = new Robot(screen);
    }

    @Override
    public void executeCommand(String command) {
        while (true) {
            try {
                if (command.equals("<GET>$<>")) {
                    getMonitor();
                    break;
                }
            } catch (Exception e) {
            }
        }
    }

    private void getMonitor() throws IOException {
        BufferedImage image = robot.createScreenCapture(rectangle);
        //ImageIO.write(image, "png", new File("D:/a.png"));
        System.out.println("Captured screen!");
        socketHandler.send(image);
    }
    
}

