package org.stlpriory.robotics.subsystems;

import org.stlpriory.robotics.RobotMap;
import org.stlpriory.robotics.commands.ExampleCommand;
import org.stlpriory.robotics.utils.Constants;
import org.stlpriory.robotics.utils.Debug;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Elevator extends Subsystem{
	CANTalon elevatorMotor;
	Encoder elevatorEncoder;
	DigitalInput topSwitch,bottomSwitch;
	int elevatorHeight;
	Timer t = new Timer();
	public Elevator() {
		super("Elevator");
		Debug.println("[Elevator Subsystem] Instantiating...");
		Debug.println("[Elevator Subsystem] CANTalon control mode is " + (Constants.TALON_CONTROL_MODE));
		//try initializing limit switches
		try
		{
			Debug.println("[Elevator Subsystem] Initializing top limit switch...");
			topSwitch = new DigitalInput(RobotMap.ELEVATOR_TOP_SWITCH_CHANNEL);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			Debug.println("[Elevator Subsystem] Initializing bottom limit switch...");
			bottomSwitch = new DigitalInput(RobotMap.ELEVATOR_BOTTOM_SWITCH_CHANNEL);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//try initializing elevator talon
		try {
			Debug.println("[Elevator Subsystem] Initializing elevator CAN to CAN bus address" 
					+ RobotMap.ELEVATOR_CAN_TALON_CHANNEL);
			this.elevatorMotor = new CANTalon(RobotMap.ELEVATOR_CAN_TALON_CHANNEL);
			initTalon(this.elevatorMotor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//try initializing encoder
		try {
			Debug.println("[Elevator Subsystem] Initializing elevator Encoder with channels A and B:" 
					+ RobotMap.ELEVATOR_ENCODER_CHANNEL_A + ", " + RobotMap.ELEVATOR_ENCODER_CHANNEL_B);
			this.elevatorEncoder = new Encoder(RobotMap.ELEVATOR_ENCODER_CHANNEL_A, RobotMap.ELEVATOR_ENCODER_CHANNEL_B);
			// initialization?
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}


	//Move elevator certain up distance at certain speed.
	public boolean goUp(double distance, double speed) {
		/**
		 * This method tells the elevator motor to go up a certain distance at a certain speed. The speed must be between 1.0 and -1.0
		 * To go down, please set speed to negative, not distance 
		 */
		boolean success = false;
		boolean atTop = false;
		boolean atBottom = false;
		int distanceToTop = 0;
		int distanceToBottom = 0;
		int distanceTraveled = 0;
		double time = distance/speed; //Time motor should be turned on
		Debug.println("[Elevator Subsystem] Trying to go up " + distance + " at " + speed);
		if (speed > 0 && speed < 1)//splits method into two scenarios:Up or down
			{
			atTop = topSwitch.get();//see if motor is at top, if so then end method
			if (atTop)
			{
				Debug.println("[Elevator Subsystem] Carrige is at top");
				return success;
			}
			elevatorHeight = elevatorEncoder.get();
			Debug.println("[Elevator Subsystem] Carrige is at " + elevatorHeight);
			//Check if there is enough space above to go up for given time at given speed.
			distanceToTop =  Constants.ELEVATOR_SHAFT_HEIGHT - elevatorHeight;
			if (distance>distanceToTop)
			{
				Debug.println("[Elevator Subsystem] Not enough room to travel distance, going to top");
				time = distanceToTop/speed;
			}
			Debug.println("[Elevator Subsystem] Starting timer...");
			t.reset();
			t.start();
			Debug.println("[Elevator Subsystem] Starting motor at "+speed);
			elevatorMotor.set(speed);
			while(t.hasPeriodPassed(time) == false)
			{
				if (topSwitch.get())//check to make sure not at top
				{
					Debug.println("[Elevator Subsystem] Top switch hit, stopping");
					break;
				}
			}
			elevatorMotor.set(0);
			t.stop();
			distanceTraveled = elevatorEncoder.get() - elevatorHeight;// get distance traveled
			Debug.print("[Elevator Subsystem] Traveled "+distanceTraveled);
			success = true;
			return success;
		
		}
		else if (speed<0&&speed>-1)
		{
			atBottom = bottomSwitch.get();
			if (atBottom)
			{
				Debug.println("[Elevator Subsystem] Carrige is at bottom");
				return success;
			}
			elevatorHeight = elevatorEncoder.get();
			Debug.println("[Elevator Subsystem] Carrige is at " + elevatorHeight);
			//Check if there is enough space above to go up for given time at given speed.
			distanceToBottom =  elevatorHeight;
			if (distance>distanceToBottom)
			{
				Debug.println("[Elevator Subsystem] Not enough room to travel distance, going to bottom");
				time = distanceToBottom/speed;
			}
			time = time*-1;//invert time to make it positive
			Debug.println("[Elevator Subsystem] Starting timer...");
			t.reset();
			t.start();
			Debug.println("[Elevator Subsystem] Starting motor at "+speed);
			elevatorMotor.set(speed);
			while(t.hasPeriodPassed(time) == false)
			{
				if (bottomSwitch.get())//check to make sure it is not at bottom
				{
					Debug.println("[Elevator Subsystem] Bottom switch hit, stopping");
					break;
				}
			}
			elevatorMotor.set(0);
			t.stop();
			distanceTraveled = elevatorHeight - elevatorEncoder.get();//get distance traveled
			Debug.print("[Elevator Subsystem] Traveled "+distanceTraveled);
			success = true;
			return success;
		}
		else
		{
			Debug.println("[Elevator Subsystem] Speed value out of range");
		}
		return success;		
	}
	//Code to initialize talon, add one time things here
	private void initTalon(CANTalon c)
	{
		c.setPID(Constants.TALON_PROPORTION, Constants.TALON_INTEGRATION, Constants.TALON_DIFFERENTIAL, Constants.TALON_FEEDFORWARD, 0, 0, 0);
	}
	
	public void initDefaultCommand()
	{
		setDefaultCommand(new ExampleCommand());
	}
	
	
}