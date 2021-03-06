
import com.leapmotion.leap.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeapMotionLogger {

       public static void readValues() {
           
        // Create a sample listener and controller
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);
        
        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException ex) {
            //e.printStackTrace();
            Logger.getLogger(LeapMotionLogger.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}

class SampleListener extends Listener {

    @Override
    public void onConnect(Controller controller) {
        System.out.println("Connected");
        String header = "Palm_Position_X,Palm_Position_Y,Palm_Position_Z,Palm_Velocity_X,Palm_Velocity_Y,Palm_Velocity_Z";
        CreateCSVFile(header);
    }
    
    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        
        Vector palmPosition = frame.hands().frontmost().palmPosition();
        Vector palmVelocity = frame.hands().frontmost().palmVelocity();
        
        Hand hand = frame.hands().get(0);
        
        int thumbFingerExtended = hand.fingers().fingerType(Finger.Type.TYPE_THUMB).extended().count();
        int indexFingerExtended = hand.fingers().fingerType(Finger.Type.TYPE_INDEX).extended().count();
        int middleFingerExtended = hand.fingers().fingerType(Finger.Type.TYPE_MIDDLE).extended().count();
        int ringFingerExtended = hand.fingers().fingerType(Finger.Type.TYPE_RING).extended().count();
        int pinkyFingerExtended = hand.fingers().fingerType(Finger.Type.TYPE_PINKY).extended().count();
        
        
        String data = palmPosition.getX() + "," + palmPosition.getY() + "," + palmPosition.getZ() + "," +
        			  palmVelocity.getX() + "," + palmVelocity.getY() + "," + palmVelocity.getZ() + "," +
        			  thumbFingerExtended + "," + indexFingerExtended + "," + middleFingerExtended+ "," + ringFingerExtended + "," + pinkyFingerExtended
        			    + "\r\n";
        
        System.out.println("Palm X, Y, Z Position: " + data);
        
        AppendRowIntoCSVFile(data);
    }
    
    
    @Override
    public void onExit(Controller controller) {
        System.out.println("Exit");
        CloseCSVFile();
    }
    
    // Helper Functions To Save The Data Into CSV File
    static FileWriter fw;
    static File file;
    static Boolean CreateCSVFile(String header)
        {
            String dirName = System.getProperty("user.dir");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");  
            file = new File( dirName + "\\"+ df.format(new Date()) +"_Similarity.csv");  
            
            if ( !file.exists() )
            try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(LeapMotionLogger.class.getName()).log(Level.SEVERE, null, ex);
                    //ex.printStackTrace();
                    return false;
                }
            try {
                fw = new FileWriter(file,true);
                //writer = new BufferedWriter( fw );
                fw.write(header + "\r\n");
                //fw.flush();
                //fw.close();
                return true;
            } catch (IOException ex) {
                Logger.getLogger(LeapMotionLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
    static Boolean AppendRowIntoCSVFile(String row)
    {
        try {
                fw.write(row);
                return true;
            } catch (IOException ex) {
                Logger.getLogger(LeapMotionLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
    }
    static boolean CloseCSVFile()
    {
        try {
            fw.flush();
            fw.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(SampleListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
