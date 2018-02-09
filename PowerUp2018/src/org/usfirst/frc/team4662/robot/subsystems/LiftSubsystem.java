package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;
import org.usfirst.frc.team4662.robot.commands.MoveLift;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LiftSubsystem extends Subsystem {

	private WPI_TalonSRX m_leftLiftController1;
	//private WPI_TalonSRX m_rightLiftController1;
	private SpeedControllerGroup m_liftControlGroup;
	private double kdLiftSpeed;
	
	public LiftSubsystem() {
		m_leftLiftController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("leftLift1"));
		m_leftLiftController1.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_leftLiftController1.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		//m_rightLiftController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("rightLift1"));
		m_liftControlGroup = new SpeedControllerGroup(m_leftLiftController1);
		kdLiftSpeed = 0.5;
		
	}
    
    public void initDefaultCommand() {
        setDefaultCommand(new MoveLift());
    }
    
    public void moveLift( double speed ) {
    	m_liftControlGroup.set(speed);
    }
    
    public void moveLiftUp() {
    	moveLift(kdLiftSpeed);
    }
    
    public void moveLiftDown() {
    	moveLift(-kdLiftSpeed);
    }
}
