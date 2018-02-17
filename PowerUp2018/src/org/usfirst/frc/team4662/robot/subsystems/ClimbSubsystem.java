package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ClimbSubsystem extends Subsystem {

	private WPI_TalonSRX m_ClimbController1;
	private WPI_TalonSRX m_ClimbController2;
	private SpeedControllerGroup m_ClimbControlGroup;
	private double kdClimbSpeed;
	private final double kdClimbDirection = 1;
	
	public ClimbSubsystem() {
		m_ClimbController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("Climb1"));
		m_ClimbController2 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("Climb2"));
		m_ClimbControlGroup = new SpeedControllerGroup(m_ClimbController1, m_ClimbController2);
		kdClimbSpeed = 0.5;
	}
    
    public void initDefaultCommand() {
    }
    
    public void moveClimb( double speed ) {
    	m_ClimbControlGroup.set(speed);
    }
    
    public void climbUp() {
    	moveClimb(kdClimbSpeed * kdClimbDirection);
    }
}

