package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class GrabSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	private WPI_TalonSRX m_grabController;
	private WPI_TalonSRX m_tiltController;
	private double m_dGrabSpeed;
	private double m_dTiltSpeed;
	
	public GrabSubsystem() {
		m_grabController = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("GrabController"));
		m_tiltController = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("TiltController"));
		m_dGrabSpeed = 0.3; 
		m_dTiltSpeed = 0.3; 
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void tiltUp() {
    	m_tiltController.set(m_dTiltSpeed);
    
   }
    public void tiltDown() {
    	m_tiltController.set(-m_dTiltSpeed);
    }
    
    public void tiltStop() {
    	m_tiltController.set(0.0);
    }
    public void grabClose() {
    	m_grabController.set(m_dGrabSpeed);
    }
    public void grabOpen() {
    	m_grabController.set(-m_dGrabSpeed);
    }
    public void grabStop() {
    	m_grabController.set(0.0);
    }
}

