package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Timer;

/**
 * 
 * This class uses the DriverStationLCD class to post messages to the driver station
 * It uses an array of strings to make the driver station lcd more like a console
 * @author Michael
 */
public class Messager {

    private DriverStationLCD driverLCD;
    private String msg[]; 

    public Messager() {
        driverLCD = DriverStationLCD.getInstance();
        msg = new String[7];
      
        for(int i=0;i<7;i++)
        {
            msg[i] = " ";
        }
        
    }

    private void moveUp()
    {
        msg[5] = msg[4];
        msg[4] = msg[3];
        msg[3] = msg[2];
        msg[2] = msg[1];
        msg[1] = msg[0];

        
    }
    private void push(DriverStationLCD.Line line, String _msg)
    {

        if(_msg.length() > 20)
        {
            msg[0] = _msg.substring(0, 20);
            driverLCD.println(line, 1, msg[0]);
            _msg = _msg.substring(20);
            
            post(_msg);


        }
        else
        {
          for(int i=0;i<_msg.length();i++)
          {
              String s = "" + _msg.toCharArray()[i];
              driverLCD.println(line, i+1, s);
          }
       
        }
       
    }

    private void post(String s) {
        clearConsole();
        moveUp();
        msg[0] = s;
        
        push(DriverStationLCD.Line.kMain6, msg[5]);
        push(DriverStationLCD.Line.kUser2, msg[4]);
        push(DriverStationLCD.Line.kUser3, msg[3]);
        push(DriverStationLCD.Line.kUser4, msg[2]);
        push(DriverStationLCD.Line.kUser5, msg[1]);
        push(DriverStationLCD.Line.kUser6, msg[0]);
    }

    /**
     * Clears the DriverStation LCD
     */
    public final void clearConsole() {
        driverLCD.println(DriverStationLCD.Line.kMain6, 1, "                             ");
        driverLCD.println(DriverStationLCD.Line.kUser2, 1, "                             ");
        driverLCD.println(DriverStationLCD.Line.kUser3, 1, "                             ");
        driverLCD.println(DriverStationLCD.Line.kUser4, 1, "                             ");
        driverLCD.println(DriverStationLCD.Line.kUser5, 1, "                             ");
        driverLCD.println(DriverStationLCD.Line.kUser6, 1, "                             ");
    }

    /**
     * Prints a message on the Driver Station LCD
     * @param s The String to be printed on the Driver Station
     */
    public void printLn(String s) {
        String time = "" + (int) Timer.getFPGATimestamp();
        post("[" + time + "]: " + s);
        
        driverLCD.updateLCD();
    }
}
