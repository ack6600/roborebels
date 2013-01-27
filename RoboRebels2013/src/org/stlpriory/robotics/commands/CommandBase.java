package org.stlpriory.robotics.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.stlpriory.robotics.OI;
import org.stlpriory.robotics.misc.Debug;
import org.stlpriory.robotics.subsystems.DriveTrain;
import org.stlpriory.robotics.subsystems.Shooter;

/**
 * The base for all commands. All atomic commands should subclass CommandBase.
 * CommandBase stores creates and stores each control system. To access a
 * subsystem elsewhere in your code in your code use CommandBase.exampleSubsystem
 * @author Author
 */
public abstract class CommandBase extends Command {

    public static OI oi;
    // Create a single static instance of all of your subsystems
    public static DriveTrain drivetrain = new DriveTrain();
    public static Shooter shooter = new Shooter();

    public static void init() {
        Debug.println("[CommandBase.init()] Initializing...");
        // This MUST be here. If the OI creates Commands (which it very likely
        // will), constructing it during the construction of CommandBase (from
        // which commands extend), subsystems are not guaranteed to be
        // yet. Thus, their requires() statements may grab null pointers. Bad
        // news. Don't move it.
        oi = new OI();

        // Show what command your subsystem is running on the SmartDashboard
        SmartDashboard.putData(drivetrain);
        
        Debug.println("[CommandBase.init()] Done.");
    }

    public CommandBase(String name) {
        super(name);
    }

    public CommandBase() {
        super();
    }
}
