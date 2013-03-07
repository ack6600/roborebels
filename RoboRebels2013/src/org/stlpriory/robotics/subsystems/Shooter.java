/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.stlpriory.robotics.subsystems;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.stlpriory.robotics.RobotMap;
import org.stlpriory.robotics.misc.Constants;
import org.stlpriory.robotics.misc.Debug;

/**
 *
 */
public class Shooter extends Subsystem {

    private static SpeedController shooterVictor = null;
    private static SpeedController loaderVictor = null;
    // Quadrature encoder used to measure shoot motor speed
    private static Encoder shooterEncoder;

    private static DigitalInput shooterProximitySensor;

    private static DigitalInput startLimitSwitch;
    private static DigitalInput stopLimitSwitch;

    private static final Timer loadDiscTimer    = new Timer();
    private static final Timer resetLoaderTimer = new Timer();
    private static final double loadDiscTimeOut    = Constants.LOAD_DISC_TIMEOUT_IN_SECS;
    private static final double resetLoaderTimeOut = Constants.RESET_LOADER_TIMEOUT_IN_SECS;


    public Shooter() {
        super("Shooter");
        Debug.println("[Shooter] Instantiating...");

        Debug.println("[Shooter] Initializing shooter wheel motor speed controller to PWM channel "
                + RobotMap.SHOOTER_WHEEL_MOTOR_PWM_CHANNEL + " on the digital module.");
        shooterVictor = new Victor(RobotMap.SHOOTER_WHEEL_MOTOR_PWM_CHANNEL);


        Debug.println("[Shooter] Initializing loader motor speed controller to PWM channel "
                + RobotMap.LOADER_MOTOR_PWM_CHANNEL + " on the digital module.");
        loaderVictor = new Victor(RobotMap.LOADER_MOTOR_PWM_CHANNEL);


        Debug.println("[Shooter] Initializing loader start position limit switch to I/O channel "
                + RobotMap.LOADER_START_POSITION_LIMIT_SWITCH_DIGITAL_IO_CHANNEL);
        startLimitSwitch = new DigitalInput(1, RobotMap.LOADER_START_POSITION_LIMIT_SWITCH_DIGITAL_IO_CHANNEL);
        Debug.println("[Shooter] Initializing loader stop position limit switch to I/O channel "
                + RobotMap.LOADER_STOP_POSITION_LIMIT_SWITCH_DIGITAL_IO_CHANNEL);
        stopLimitSwitch = new DigitalInput(1, RobotMap.LOADER_STOP_POSITION_LIMIT_SWITCH_DIGITAL_IO_CHANNEL);


        Debug.println("[PIDShooter] Initializing shooter motor encoder to channels "
                + RobotMap.SHOOTER_ENCODER_DIGITAL_IO_CHANNEL_A
                + " and " + RobotMap.SHOOTER_ENCODER_DIGITAL_IO_CHANNEL_B);
        shooterEncoder = new Encoder(1, RobotMap.SHOOTER_ENCODER_DIGITAL_IO_CHANNEL_A,
                                     1, RobotMap.SHOOTER_ENCODER_DIGITAL_IO_CHANNEL_B, true,
                                     CounterBase.EncodingType.k1X);
        shooterEncoder.setDistancePerPulse(1);
        shooterEncoder.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        shooterEncoder.start();

        Debug.println("[Shooter] Instantiation complete.");
    }

    private boolean canLoadDisc() {
        // If the start position limit switch is triggered then
        // the loader arm is retracted and ready to load another disc
        return true;
        //return startLimitSwitch.get();
    }

    public void loadDisc(double speed) {
        // Until the stop position limit switch is triggered
        // the value returned will be false
        while (!stopLimitSwitch.get()) {
            loaderVictor.set(-speed);
        }
        loaderVictor.set(0);
        printLimitSwitchValues();
    }

    public void resetLoader(double speed) {
        // Until the start position limit switch is triggered
        // the value returned will be false
        while (!startLimitSwitch.get()) {
            loaderVictor.set(speed);
        }
        loaderVictor.set(0);
        printLimitSwitchValues();
    }

    public void loadDisc2(double speed) {
        // Create a timer to measure the execution time
        // for attempting to load the disc.  If we exceed
        // the timeout value then stop.
        double elapsedTime = 0;
        loadDiscTimer.reset();
        loadDiscTimer.start();

        // Until the stop position limit switch is triggered
        // the value returned will be false
        while (!stopLimitSwitch.get()) {
            elapsedTime = loadDiscTimer.get();
            if (elapsedTime > loadDiscTimeOut) {
                Debug.println("Load disc action timed out");
                break;
            }
            loaderVictor.set(-speed);
        }
        loaderVictor.set(0);
        loadDiscTimer.stop();
    }

    public void resetLoader2(double speed) {
        // Create a timer to measure the execution time
        // for attempting to reset the loader arm.  If we
        // exceed the timeout value then stop.
        double elapsedTime = 0;
        resetLoaderTimer.reset();
        resetLoaderTimer.start();

        // Until the start position limit switch is triggered
        // the value returned will be false
        while (!startLimitSwitch.get()) {
            elapsedTime = resetLoaderTimer.get();
            if (elapsedTime > resetLoaderTimeOut) {
                Debug.println("Reset loader action timed out");
                break;
            }
            loaderVictor.set(speed);
        }
        loaderVictor.set(0);
        resetLoaderTimer.stop();
    }

    public boolean isLoadDiscFinished() {
        boolean isFinished = stopLimitSwitch.get();
        if (isFinished) Debug.println("Load disc is finished!");
        return isFinished;
    }

    public boolean isResetLoaderFinished() {
        boolean isFinished = startLimitSwitch.get();
        if (isFinished) Debug.println("Reset loader is finished!");
        return isFinished;
    }

    public void startShooter(double speed) {
//      shooterEncoder.start();
        shooterVictor.set(speed);
//        printEncoderValues();
    }

    public void stopShooter() {
        shooterVictor.set(0);
        resetLoader(Constants.LOADER_MOTOR_SPEED);
//      shooterEncoder.stop();
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

    public void printLimitSwitchValues() {
        Debug.println("start switch = " + startLimitSwitch.get() + ", stop switch = "+ stopLimitSwitch.get());
    }

    public void printEncoderValues() {
        Debug.println("encoder raw = " + shooterEncoder.getRaw() + ", rate = " + shooterEncoder.getRate() + ", pidGet = " + shooterEncoder.pidGet());
    }

}
