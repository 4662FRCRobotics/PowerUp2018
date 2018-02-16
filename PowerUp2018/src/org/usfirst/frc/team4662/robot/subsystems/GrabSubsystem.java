package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;
import org.usfirst.frc.team4662.robot.commands.Display;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *,
 */
public class GrabSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from , Commands.
	
	private WPI_TalonSRX m_grabController;
	private WPI_TalonSRX m_tiltController;
	private double m_dGrabSpeed;
	private double m_dTiltSpeed;
	private boolean m_bIsGrabOpen;
	private double m_dReleaseSpeed;
	
	public GrabSubsystem() {
		m_grabController = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("GrabController"));
		m_grabController.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_grabController.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_grabController.overrideLimitSwitchesEnable(true);
		m_tiltController = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("TiltController"));
		m_tiltController.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_tiltController.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_dGrabSpeed = 0.3; 
		m_dReleaseSpeed = 0.5;
		m_dTiltSpeed = 0.3; 
		m_bIsGrabOpen = false;
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new Display());
    }
    
    
    public void displayLimitSwitches() {
    	if ( Robot.m_robotMap.isDashboardTest()) {
    		SmartDashboard.putBoolean("Grab Limit Forward", m_grabController.getSensorCollection().isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Grab Limit Reverse", m_grabController.getSensorCollection().isRevLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tilt Limit Forward", m_tiltController.getSensorCollection().isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tilt Limit Reverse", m_tiltController.getSensorCollection().isRevLimitSwitchClosed());
    	}
    }
    
    public void tiltUp() {
    	m_tiltController.set(m_dTiltSpeed);
    	displayLimitSwitches();
    
   }
    public void tiltDown() {
    	m_tiltController.set(-m_dTiltSpeed);
    	displayLimitSwitches();
    }
    
    public void tiltStop() {
    	m_tiltController.set(0.0);
    }
    public boolean isTiltAtBottom() {
    	displayLimitSwitches();
    	return m_tiltController.getSensorCollection().isFwdLimitSwitchClosed();
    	
    }
    public void grabClose() {
    	m_grabController.set(m_dGrabSpeed);
    	displayLimitSwitches();
    }
    public void grabOpen() {
    	m_grabController.set(-m_dReleaseSpeed);
    	displayLimitSwitches();
    }
    public void grabStop() {
    	m_grabController.set(0.0);
    }
    public boolean isGrabClosed() {
    	return m_grabController.getSensorCollection().isRevLimitSwitchClosed();  	
    }
    public boolean isGrabOpen() {
    	return m_bIsGrabOpen;
    }
    public void setGrabOpenTrue() {
    	m_bIsGrabOpen = true;
    }
    public void setGrabOpenFalse() {
    	m_bIsGrabOpen = false;
    }
}

