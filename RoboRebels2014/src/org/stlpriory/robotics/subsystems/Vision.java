/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.stlpriory.robotics.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.stlpriory.robotics.misc.Debug;

/**
 */
public class Vision extends Subsystem {
    
    // will be null if not known, else will be a double
    // in the range of -1 to 1 with -1=far left of image,
    // 0=center of the image, and 1=far right of image
    private Double hotGoalNormalizedX;
    
    public Vision () {
        super("Vision");
        Debug.println("Vision subsystem constructor");
    }

    protected void initDefaultCommand() {
        Debug.println("Vision initDefaultCommand called");
    }
    
    /**
     * 
     * @return null if the hot goal normalized X value is not known
     * else a double in the range of -1 to 1.  Note that this is
     * the normalized X value of the center of the horizontal
     * retroreflective tape above the low goal and is not the center
     * of the high or low goals themselves.
     */
    public synchronized Double getHotGoalNormalizedX ( ) {
        return hotGoalNormalizedX;
    }
    
    public synchronized void setHotGoalNormalizedX ( Double value ) {
        hotGoalNormalizedX = value;
    }
    
}