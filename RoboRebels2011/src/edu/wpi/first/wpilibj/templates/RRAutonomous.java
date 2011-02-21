/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 */



public class RRAutonomous {
    Gyro gyro;
    RRElevator elevator;
    double angle;

    double xmov = 0;
    double ymov = 0;
    double rot = 0;
    double speed = -.3;
    double large_correction = .1;
    double small_correction = .05;
    double lost_line_timer = 100;
    double hunt_time = .2/speed;
    boolean     isDriving;
    boolean     yIsDetected = false;
    boolean     stopCondition = false;
    boolean     strafeToTheEnd = false;


    RRMecanumDrive mecanumDrive;
    RRLineTracker lineTracker;
    Timer timer;
    Timer yCounter;
    boolean all3;
    boolean YLINE;
    int seconds;



    public RRAutonomous(RRMecanumDrive m_drive, RRElevator elevator){
        gyro = new Gyro(1);
        gyro.setSensitivity(.007);
        mecanumDrive = m_drive;
        this.elevator = elevator;
        lineTracker = new RRLineTracker(4,5,6);
        timer = new Timer();
        YLINE = true;
        yCounter = new Timer();
        all3 = false;
        //seconds = 1000;
        isDriving = false;

        timer.start();
    }

    /*
     *
     * One idea for dealing with "Y" line is that if we are traveling at a
     * constant speed then if we don't encounter all three line sensors
     * activating after a certain amount of time then we must be on a straight
     * line.
     *
     */

    public void lineAuton(boolean right, boolean mid, boolean left) {
        //Sees if the sensors are over the tape and moves if they are, stops if they aren't
        if ((left) && (mid) && (right)) {       // off the line
             
            if (lost_line_timer == 100){
                lost_line_timer = timer.get();
                System.out.println("Lost_Line_Timer = " + lost_line_timer );
            }
            if(timer.get() < lost_line_timer + hunt_time) {
                                                //keeps the program going for 20 seconds
                //System.out.println("Still going Lost_Line_Timer = " + lost_line_timer );
                ymov = speed;
                xmov = 0;                            //look for the line for 1 second then stop
                rot = 0;
            }
             else{
                
                System.out.println("Stopping Lost_Line_Timer = " + lost_line_timer );
                 ymov = 0;
                 xmov = 0;
                 rot = 0;
                 
             }
        }
        else if((left) && (mid) && (!right)) {  // right sensor on the line
            System.out.println("Moving right");
            if ( strafeToTheEnd )
            {
                ymov = speed * 0.5;
                xmov = speed * 0.5;
                rot = 0;
                lost_line_timer = 100;              // strafe to the forward/right
            }
            else
            {
               ymov = speed;
               xmov = 0;                            // move right
               rot = - large_correction;
               lost_line_timer = 100;
            }
        }
        else if((left) && (!mid) && (right)) {  // middle sensor on the line
            System.out.println("Moving forward");
           ymov = speed;
           xmov = 0;                            // move forward
           rot = 0;
           lost_line_timer = 100;
        }
        else if((left) && (!mid) && (!right)) { // middle and right sensors on the line
            System.out.println("Moving slightly to the right");

            if ( strafeToTheEnd )
            {
                ymov = speed * 0.5;
                xmov = speed * 0.25;
                rot = 0;
                lost_line_timer = 100;
            }
            else
            {
               ymov = speed;
               xmov = 0;                            // move forward and slightly right
               rot = - small_correction;
               lost_line_timer = 100;
            }
        }
        else if((!left) && (mid) && (right)) {  // left sensor on the line
            System.out.println("Moving to the left");
            if ( strafeToTheEnd )
            {
                ymov = speed * 0.5;
                xmov = speed * 0.5;
                rot = 0;
                lost_line_timer = 100;
            }
            else
            {
               ymov = speed;
               xmov = 0;                            // move left
               rot = large_correction;
               lost_line_timer = 100;
            }
        }
        else if ((!left) && (mid) && (!right)) {// right and left sensors on the line (split)
          System.out.println("Found a split before a Y, ?!?!?");
          if (all3){
               System.out.println("Found a split");
              yIsDetected = true;
          }
        }
        else if ((!left) && (!mid) && (right)) {// left and right sensors on the line
            System.out.println("Moving slightly to the left");
            if ( strafeToTheEnd )
            {
                ymov = speed * 0.5;
                xmov = speed * 0.25;
                rot = 0;
                lost_line_timer = 100;
            }
            else
            {
               ymov = speed;
               xmov = 0;                            // move forward and slightly left
               rot = small_correction;
               lost_line_timer = 100;
            }
        }
        else if((!left) && (!mid) && (!right)) {   // end of line (all three tripped)
            System.out.println("End of line detected, could be split though");
            if (!all3){
                all3 = true;
            }
            else{
                /*
                if (stopCondition){
                    System.out.println("Stopping!");
                    ymov = 0;
                    xmov = 0;                            // stop
                    rot = 0;
                }
                 *
                 */
                if (yIsDetected)
                {
                    System.out.println("Stopping!");
                    ymov = 0;
                    xmov = 0;                            // stop
                    rot = 0;
                }
            }
        }

        if (yIsDetected){
            /*
            ymov = speed * 0.4;
            xmov = 0;
            rot = 0.3;


            System.out.println("Y detected, hunting!");
            */

            System.out.println("Y detected, enabling strafe mode");

            strafeToTheEnd = true;
            yIsDetected = false;

            ymov = speed * 0.5;
            xmov = speed * 0.25;
            rot = 0;
            

            /*
            if((left) && (!mid) && (right)) {  // middle sensor on the line
                System.out.println("Found a line!  Going back to normal");
                stopCondition = true;
                yIsDetected = false;
            }
             *
             */
        }
    }

    public void drive(){
        //gets the values of the line sensors
        boolean testL = lineTracker.getL();
        boolean testM = lineTracker.getM();
        boolean testR = lineTracker.getR();
        //sets the value of ymov and xmov
        lineAuton(testR,testM,testL);

        if(timer.get() < 20) {
            //keeps the program going for 20 seconds

            //System.out.println("Timer = " + timer.get() );

            // negative is forward!
            mecanumDrive.drive(xmov, ymov, rot);
        
        }
        else
        {
            mecanumDrive.drive(0, 0, 0);
        }
    }

/*    public void printGyro(){

        if (lineTracker.activeSensor().equals("none")){
            mecanumDrive.drive(0,.04);
        }
        else {
            if (lineTracker.activeSensor().equals("middle")){
                mecanumDrive.drive(0,.04);
            }
        }

            angle = gyro.pidGet();
            System.out.println(angle%(360));

    }*/
    
    
}
