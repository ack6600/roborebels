
/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/*
 * Joystick (xbox) button map
 * 0
 * 1 A
 * 2 B
 * 3 X
 * 4 Y
 * 5 L Bumper
 * 6 R Bumper
 * 7 Back
 * 8 Start
 * 9 L Stick Click
 * 10 R Stick Click
 * 
 * Axis
 * 
 *  �1: Left Stick X Axis
        -Left:Negative ; Right: Positive
    �2: Left Stick Y Axis
        -Up: Negative ; Down: Positive
    �3: Triggers
        -Left: Positive ; Right: Negative
    �4: Right Stick X Axis
        -Left: Negative ; Right: Positive
    �5: Right Stick Y Axis
        -Up: Negative ; Down: Positive
    �6: Directional Pad (Not recommended, buggy)

 * 
 */

/*
 * 


 * NOTES:
 *
 *
 * - I have decided to clean up our code base a bit. - Derek W.
 * 
 * 
 * TODO:
 * 
 * - Migrate code out of RoboRebels.java into their respective classes
 * 
 * - All classees that depend on a Joystick should be passed joystick object(s)
 *   and handled within their classes
 * 
 */
package edu.wpi.first.wpilibj.templates;



import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.PWM;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RoboRebels extends IterativeRobot {

    // Declare a variable to use to access the driver station object
    DriverStation       m_ds;                   // driver station object
    DriverStationLCD    m_dsLCD;                // driver station LCD object
    Joystick            m_rightStick;		// joystick 1 (arcade stick or right tank stick)
    Joystick            m_leftStick;		// joystick 2 (tank left stick)
    //Joystick            m_xboxStick;
    PWM                 currentPWM;
    RRDrive             drive;
    RRShooter           shooter;
    RRGatherer          gatherer;
    RRBridgeArm         arm;
    ADXL345_I2C         accel;
    RobotDrive          m_robotDrive;
    RRTracker           tracker;
    //RRTracker tracker = new RRTracker();   // New objects shouldn't be created outside of a method.
    
    double              lastZValue;                         // last Z value for the dial on the joystick
    double              autonomousStartTime;    // holds the start time for autonomous mode
    double              robotDriveSensitivity = 0.25;       // sensitivity of the RobotDrive object
    boolean             tankDrive = false;
    
    // PWM Channel constants
    final static int    LEFT_DRIVE_CHANNEL = 1;
    final static int    RIGHT_DRIVE_CHANNEL = 2;
    final static int    SHOOTER_CHANNEL = 3;
    final static int    TILT_CHANNEL = 7;
    final static int    LAZY_SUSAN_CHANNEL = 8;
    final static int    LOADER_CHANNEL = 5;
    final static int    SPINNER_CHANNEL = 4;
    final static int    BRIDGE_ARM_CHANNEL = 6;
    
    // Digital I/O constants
    final static int    BOTTOM_BALL_SENSOR_CHANNEL = 1;
    final static int    MIDDLE_BALL_SENSOR_CHANNEL = 2;
    final static int    TOP_BALL_SENSOR_CHANNEL = 3;
    final static int    TILT_LIMIT_SWITCH_CHANNEL = 4;
    
    
    static final int    NUM_JOYSTICK_BUTTONS = 16;  // how many joystick buttons exist?
    static boolean      disabledStateBroadcasted = false;
    static boolean      teleopStateBroadcasted = false;
    static boolean      autonomousStateBroadcasted = false;
    
    int                 pwmTest = 0;
    boolean             btnPressed = false;
    double              launcher_speed = 0.0;
    boolean             launcher_button_pressed = false;
    static int          target_direction = -1;  // -1 if target is to left, 0 if on target, 1 if target is the right
    static double       muzzle_velocity = 7.1; //muzzle velocity in meters per second

    /*
     *          (\_/)
     *          (O.0)
     *           =o=
     *         (    ) <--- bunny ;)
     *          (  )
     *
     */


    /**
     * Constructor
     */
    public void RoboRebels() {
        System.out.println("RoboRebels()");

    }

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        
        System.out.println("robotInit()");

        m_leftStick = new Joystick(3);
        m_rightStick = new Joystick(2);
        //m_xboxStick = new Joystick(1);
        System.out.println("Joysticks set");

        RRButtonMap.setController("joystick");
        System.out.println("Button map");        
        accel = new ADXL345_I2C(1, ADXL345_I2C.DataFormat_Range.k2G); // slot number is actually module number
        System.out.println("accel");
        
        drive = new RRDrive(m_rightStick, 2, 1);
        System.out.println("Drive");
        
        gatherer = new RRGatherer(SPINNER_CHANNEL, LOADER_CHANNEL, BOTTOM_BALL_SENSOR_CHANNEL, MIDDLE_BALL_SENSOR_CHANNEL, TOP_BALL_SENSOR_CHANNEL, m_rightStick);
        System.out.println("Gatherer");
        arm = new RRBridgeArm(BRIDGE_ARM_CHANNEL, m_rightStick);
        System.out.println("Arm");
        
        tracker = new RRTracker(accel);
        System.out.println("Tracker");
        
        shooter = new RRShooter(SHOOTER_CHANNEL, LAZY_SUSAN_CHANNEL, TILT_CHANNEL, TILT_LIMIT_SWITCH_CHANNEL, m_rightStick, tracker);
        
        System.out.println("Robot Ready");
    }

    public void disabledInit() {
        teleopStateBroadcasted = false;
        autonomousStateBroadcasted = false;
    }

    public void autonomousInit() {
        System.out.println("autonomousInit()");

        disabledStateBroadcasted = false;
        teleopStateBroadcasted = false;

        // Get the time that the autonomous mode starts
        autonomousStartTime = Timer.getFPGATimestamp();
    }

    public void teleopInit() {
        System.out.println("teleopInit()");

        disabledStateBroadcasted = false;
        autonomousStateBroadcasted = false;
        tankDrive = false;

        /* Drive station code */
        //m_ds = DriverStation.getInstance();
        //m_dsLCD = DriverStationLCD.getInstance();

    }

    /**
     * This function is called periodically during autonomous
     *
     * Notes:
     *
     *
     *
     */
    public void autonomousPeriodic() {
        tracker.trackTarget();
        //System.out.println(getAngle());
    }

    /**
     * This function is called periodically during operator control
     *
     * ---------------------
     * This is the most important method in this class
     * ---------------------
     */
    public void teleopPeriodic() 
    {
        
       
        if ( teleopStateBroadcasted == true )
        {
            System.out.println( "Teleop State" );
            teleopStateBroadcasted = false;
        }
        
        if ( tankDrive == true ) {
            drive.drive(true);
            //System.out.println("Tank Drive");
        }
        else{
            drive.drive(false);
            //System.out.println("Arcade Drive");
        }

      shooter.shoot();
      gatherer.gather();
      arm.arm();
        


    }

    /**
     * This function is called periodically during the disabled state
     *
     * What it needs to do:
     *
     *
     */
    public void disabledPeriodic() {
        //nothing right now
    }

    /**
     * The VM will try to call this function as often as possible during the autonomous state
     *
     */
    public void autonomousContinuous() {
        //nothing right now
    }

    /**
     * The VM will try to call this function as often as possible during the teleop state
     *
     */
    public void teleopContinuous() {

    }

    /**
     * The VM will try to call this function as often as possible during the disbabled state
     */
    public void disabledContinuous() {
    }

    /*
     * This method checks buttons and sets states accordingly
     * 
     * NOTE:  Input checking should be put into their respective classes.  For 
     * reference, see RRShooter.
     */
    public void checkButtons() {
        //System.out.println( "checkButtons()" );
        /*
        if (m_rightStick.getZ() <= 0)
        {    // Logitech Attack3 has z-polarity reversed; up is negative
            // arcade mode
            tankDrive = false;
        }
        else
        {
            // tank drive
            tankDrive = true;
        }
        * */
        /*
        if (m_leftStick.getRawButton(1)) {
            launcher.set(launcher_speed);
            if (!launcher_button_pressed)  // if the shooter button is pressed then this adds .2 to its speed
            
           {
                launcher_speed += -0.2;     
                launcher_button_pressed = true;
                if (launcher_speed < -1.0)  // once it gets past speed of -1
                {
                    launcher_speed  = 0.0; // it turns itself off
                }
     
            System.out.println("Increasing launcher_speed to "+ launcher_speed);
            }
        }
        else
            launcher_button_pressed = false;
      // else {
      //      launcher.set(0);
      //      System.out.println("Launch cim off");
      //  }
        
        if(m_leftStick.getRawButton(3)) { //when button 3 is presssssssed the up/down aiming increases
            elevation.set(.3);
        System.out.println("elevation increase");
        }
        else if (m_leftStick.getRawButton(2)) { //when button 2 is pressed the up/down aiming decreases
            elevation.set(-.3);
        System.out.println("elevation decrease");
        }
        else {
            elevation.set(0);
        System.out.println("elevation standstill");//otherwise it stops moving
        }
        

        if (m_leftStick.getRawButton(4)) { //when button 4, move susan left
            lazySusan.set(-.3);
            System.out.println("Lazysusan Left");
         }
       else if (m_leftStick.getRawButton(5)) {// when but 5, move suzie right
            lazySusan.set(.3);
           System.out.println("Lazysusan Right");
          }
        else { //otherwise dont do anything
             lazySusan.set(0);
            System.out.println("Lazysusan STOPPPPP!!!!");
            }

         if (m_leftStick.getRawButton(6)) { //if 6, suck ball in
            loader.set(-.75);
            System.out.println("loader up =D");
         }
       else if (m_leftStick.getRawButton(7)) { //if 7, drop (or de-suck) ball
            loader.set(.75);
           System.out.println("loader down :?");
          }
        else {
             loader.set(0); //otherwise, dont move(aka stoop) at all
            System.out.println("loader STOOP");
            }
            * 
            */

/*
        if (m_leftStick.getRawButton(6) && btnPressed == false) {
            btnPressed = true;
            pwmTest++;

            if (currentPWM != null)
                currentPWM.setRaw(0);

            if (pwmTest == 8)
                pwmTest = 1;

            System.out.println("PWM #" + pwmTest);

            currentPWM = new PWM(pwmTest);
            currentPWM.setRaw(128);
            System.out.println("Pwm Test done with channel");
        }

        if (!m_leftStick.getRawButton(6) && btnPressed == true) {
            btnPressed = false;
        }
 *
 */



        /*
        System.out.println( "LX: " + m_xboxStick.getRawAxis(1));
        System.out.flush();
        System.out.println( "LY: " + m_xboxStick.getRawAxis(2));
        System.out.flush();
        System.out.println( "RX: " + m_xboxStick.getRawAxis(4));
        System.out.flush();
        System.out.println( "RY: " + m_xboxStick.getRawAxis(5));
        System.out.flush();
         */
    }
    
    

    public double getAngle() {
        ADXL345_I2C.AllAxes axes = accel.getAccelerations();
        System.out.println("X Accel: " + axes.XAxis);
        System.out.println("Y Accel: " + axes.YAxis);
        double yAxis = Math.min(1, axes.YAxis);
        yAxis = Math.max(-1, yAxis);
        return 180.0 * MathUtils.asin(yAxis) / 3.14159;
    }
}