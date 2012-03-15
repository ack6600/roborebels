/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.ADXL345_I2C;

/**
 *
 * This class has the following components:
 * 
 * - Bridge arm
 * 
 * TODO:  
 * 
 * 
 * 
 * @author dmw
 */
public class RRBridgeArm 
{
    // Reduced the retraction (UP) speed of the arm by half
    private final double    ARM_DOWN_SPEED = -1.0;
    private final double    ARM_UP_SPEED = 0.5;
    private final double    ARM_DEAD_ZONE = 0.1;
    
    private final int       BRIDGE_ARM_LOWER_REQUEST = 0,
                            BRIDGE_ARM_RAISE_REQUEST = 1,
                            BRIDGE_ARM_LOWERING = 2,
                            BRIDGE_ARM_RAISING = 3;
    
    private final int       BRIDGE_ARM_LOWER_BUTTON_PRESSED = 4,
                            BRIDGE_ARM_RAISE_BUTTON_PRESSED = 5;
    
    private     int         bav_channel;
    
    private     double      armSpeed = 0.0;
    
    private     Victor      bridgeArmVictor;
    private     Joystick    js;
    
    private     RRTracker   tracker;                // used for shooter angle information
    
    /**
     * Set's up BridgeArm victor
     * 
     * @param bavc The Victor controlling the BridgeArm
     */
    public RRBridgeArm(int bavc, RRTracker t)
    {
        bav_channel = bavc;
        
        if ( t != null )
            this.tracker = t;
        else
        {
            throw new NullPointerException("RRBridgeArm was passed a null RRTracker object (t)! ");
        }
        
        if ( js != null )
            this.js = js;
        else
        {
            throw new NullPointerException("RRBridgeArm was passed a null Joystick object (js)! ");
        }
        
        bridgeArmVictor = new Victor(bav_channel);
    }
    
    
    /**
     * Joystick Buttons/axis    Action                  This button layout is if the auto ball sensing is not functional
        
8                       Bridge arm down 
9                       Bridge arm up   
     */
    
    private void gatherInputStates()
    {
        /*
         * Old, simplified code which doesn't take into account the shooter state
         * 
        // Get Bridge Arm button states
        if ( js.getRawButton(RRButtonMap.BRIDGE_ARM_DOWN) )
        {
            System.out.println("Bridge lower arm");
            armSpeed = ARM_DOWN_SPEED;
        }
        else if ( js.getRawButton(RRButtonMap.BRIDGE_ARM_UP) )
        {
            System.out.println("Bridge lower arm");
            armSpeed = ARM_UP_SPEED;
        }
        else if ( !js.getRawButton(RRButtonMap.BRIDGE_ARM_DOWN) && !js.getRawButton(RRButtonMap.BRIDGE_ARM_UP) )
        {
            armSpeed = 0.0;
        }
        * 
        */
        
        RRAction    aoBD =  RRButtonMap.getActionObject(RRButtonMap.BRIDGE_ARM_DOWN),
                    aoBU =  RRButtonMap.getActionObject(RRButtonMap.BRIDGE_ARM_UP);
        
        double      armAxis = aoBD.getAxisState();
        
        if ( armAxis > ARM_DEAD_ZONE && armAxis < (-1.0 * ARM_DEAD_ZONE) )
        {
            armSpeed = armAxis;
        }
        
    }
    
    
    public void arm()
    {
        gatherInputStates();
        
        setBridgeArmSpeeds();
    }
    
    
    private void setBridgeArmSpeeds()
    {
        System.out.println("Shooter is not in a valid position yet!");
        bridgeArmVictor.set(armSpeed);
    }
    
    /**
     * Lowers the BridgeArm
     */
    public void LowerBridgeArm()
    {
        System.out.println("LowerBridgeArm has not been implemented yet!");
    }
    
    /**
     * Raises the BridgeArm
     */
    public void RaiseBridgeArm()
    {
        System.out.println("RaiseBridgeArm has not been implemented yet!");
    }
}