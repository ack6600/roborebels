/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * Class which holds the autonomous code for
 * working with the gyro
 *
 * @author Derek Ward
 */
public class RRAutonGyro
{
    private     Timer               m_timer;
    private     Gyro                m_gyro;
    private     int                 m_gyroChannel;
    private     RRMecanumDrive      m_drive;

    private     final int           START = 0,
                                    STOP = 1;

    private     int                 DriveState;

    private     double              m_startDriveTime = 0;

    /**
     * A constructor which takes a RRMecanumDrive object
     * and uses the default gyro channel.
     * @param d The already created RRMecanumDrive object
     */
    public RRAutonGyro(RRMecanumDrive d)
    {
        // Default Gyro channel is 1
        m_gyroChannel = 1;

        // Only collect the MecanumDrive object
        // if it's not null
        if ( d != null )
            m_drive = d;

        // Set up necessary objects and vars
        Setup();
    }

    /**
     * Same as the constructor above, except you may pass in your
     * own gyro channel.
     * @param d Already created RRMecanumDrive object
     * @param channel Gyro channel
     */
    public RRAutonGyro( RRMecanumDrive d, int channel )
    {
        if ( channel <= 0 )
            m_gyroChannel = 1;
        else
            m_gyroChannel = channel;

        if ( d != null )
            m_drive = d;

        Setup();
    }

    /**
     * Calls other setup methods and reset()
     */
    private void Setup()
    {
        SetupTimer();
        SetupGyro();
        reset();
    }

    /**
     * Creates a new Timer object for our class
     */
    private void SetupTimer()
    {
        m_timer = new Timer();
    }

    /**
     * Creates a new Gyro object for our class
     */
    private void SetupGyro()
    {
        m_gyro = new Gyro( m_gyroChannel );
    }

    /**
     * Initializes necessary objects.
     */
    public void init()
    {
        // start the timer!
        m_timer.start();
    }

    /**
     * This method should be called continuously to properly
     * run the robot in autonomous code.  This is also where
     * autonomous logic should be placed into.
     *
     * You should keep the START and STOP case states, however,
     * to add usefull logic make your states additional number
     * cases.  It is good practice to encapsulate your logic into
     * additional private methods (see driveFor or rotate methods).
     */
    public void run()
    {
        switch ( DriveState )
        {
            case START:

                reset();
                DriveState = 2;
                break;

            case 2:

                collectDriveStartTime();
                DriveState = 3;
                break;

            case 3:

                if ( driveFor(0.25, 3.0) == true )
                {
                    m_drive.stop();
                    DriveState = 4;
                }

                break;

            case 4:

                if ( rotate(180, 5, 0.20, true) == true )
                {
                    //System.out.println("Stopping");
                    m_drive.stop();
                    DriveState = STOP;
                }

                break;

            case STOP:

                m_drive.stop();
                break;

            default:

                m_drive.stop();
                break;
        }
    }

    /**
     * This method resets the state of the autonomous
     * class.  Sets the DriveState to START, resets
     * the gyro and resets the timer and startDriveTime
     * variable.
     */
    public void reset()
    {
        // default drive state
        DriveState = START;

        // reset gyro heading!
        m_gyro.reset();

        // reset timer!
        m_timer.reset();

        // reset drive variables
        m_startDriveTime = 0;
    }

    /**
     * Returns the current angle of the gyro
     * @return Returns the angle of the gyro in degrees
     */
    public double getAngle()
    {
        if ( m_gyro != null )
            return m_gyro.getAngle();
        else
            return 0.0;
    }

    /**
     * Returns the current time of the timer
     * @return Returns the current timer time in seconds
     */
    public double getTime()
    {
        if ( m_timer != null )
            return m_timer.get();
        else
            return 0.0;
    }

    /**
     * Called periodically, this method rotates the robot
     * until it hits the passed angle (degrees) within the
     * range at the passed rotation speed and in a clockwise
     * or counter-clockwise direction.  Returns true when the
     * criteria is met, otherwise it returns false.
     * @param degrees The degree in which the rotation will stop
     * @param range What is a good range to stop within (+/-)
     * @param rotateSpeed What rotation speed should we roate in (0.0 - 1.0)
     * @param clockwise True = clockwise, False = counter-clockwise
     * @return True = criteria met, False = criteria not met
     */
    public boolean rotate( double degrees, double range, double rotateSpeed, boolean clockwise )
    {
        double  currentAngle = m_gyro.getAngle();

        //System.out.println("cA: " + currentAngle + " | d: " + degrees + " | r: " + range);

        if ( currentAngle <= (degrees + range) && currentAngle >= (degrees - range) )
        {
            //System.out.println("rotate criteria met!");
            return true;
        }
        else
        {
            if ( clockwise == true )
            {
                m_drive.drive(0, 0, rotateSpeed);
            }
            else
            {
                m_drive.drive(0, 0, -rotateSpeed);
            }

            return false;
        }
    }

    /**
     * Drives the robot forward in a certain speed, and counting
     * from the start time for the passed duration.  Returns true
     * when the criteria is met, false otherwise.  Make sure you call
     * collectDriveStartTime method before you call this method!!!
     * @param forwardSpeed What speed do you want to drive at (0.0 - 1.0)
     * @param duration How long in seconds do you want the robot to drive for?
     * @return True = criteria met, False = criteria not met
     */
    public boolean driveFor( double forwardSpeed, double duration )
    {
        double  currentTime = m_timer.get();

        if ( currentTime - m_startDriveTime < duration )
        {
            m_drive.drive(0.0, forwardSpeed, 0.0);
            return false;
        }
        else
            return true;
    }

    /**
     * Collects the current time into the variable m_startDriveTime.  Call this
     * before you use the driveFor method!
     */
    public void collectDriveStartTime()
    {
        m_startDriveTime = m_timer.get();
    }
}
