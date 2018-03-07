package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;
import org.usfirst.frc.team4662.robot.commands.Tilt;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
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
	private double m_dMaxOpenCount;
	private double m_dCurrentOpenCount;
	private double m_dOpenIncrement;
	private double m_dCloseIncrement;
	private final int m_iMaxOpenIterations = 15;
	private AnalogPotentiometer m_tiltPot;
	private DigitalInput m_switchTiltVertical;
	private double m_dTiltPotVal;
	private double m_dTiltVertVal;
	private double m_dTiltFwdLiftLim;
	private double m_dTiltRevLiftLim;
	private double m_dTiltFwdLim;
	private double m_dTiltRevLim;
	private boolean m_bSafetyEnable;
	
	public GrabSubsystem() {
		m_grabController = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("GrabController"));
		//m_grabController.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		//m_grabController.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, 0);
		m_grabController.overrideLimitSwitchesEnable(true);
		m_tiltController = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("TiltController"));
		//m_tiltController.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		//m_tiltController.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_tiltController.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		m_tiltController.setNeutralMode(NeutralMode.Brake);
		m_dGrabSpeed = 0.7; 
		m_dReleaseSpeed = 1.0;
		m_dTiltSpeed = 0.5;
		m_bIsGrabOpen = false;
		m_dCurrentOpenCount = 0.0;
		m_dOpenIncrement = 10.0;
		m_dCloseIncrement = m_dOpenIncrement/2;
		m_dMaxOpenCount = m_dOpenIncrement * m_iMaxOpenIterations;
		m_dTiltVertVal = Robot.m_robotMap.getDeviceDoubleVal("TiltPot", "VertVal", .485);
		m_dTiltFwdLiftLim = 0.05;
		m_dTiltRevLiftLim = 0.05;
		m_dTiltFwdLim = 0.45;
		m_dTiltRevLim = 0.17;
		m_tiltPot = new AnalogPotentiometer(Robot.m_robotMap.getPortNumber("TiltPot"));
		m_switchTiltVertical = new DigitalInput(0);
		m_bSafetyEnable = true;
		
		
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new Tilt());
    }
    
    public void disableSafety() {
    	m_bSafetyEnable = false;
    }
    
    public void enableSafety() {
    	m_bSafetyEnable = true;
    }
    
    public void displayLimitSwitches(double speed) {
    	System.out.println("In Dashboard Display Limits" + Robot.m_robotMap.isDashboardTest());
    	if ( Robot.m_robotMap.isDashboardTest()) {
    		//SmartDashboard.putBoolean("Grab Limit Forward", m_grabController.getSensorCollection().isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Grab Limit Reverse", m_grabController.getSensorCollection().isRevLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tilt Limit Forward", m_tiltController.getSensorCollection().isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tilt Limit Reverse", m_tiltController.getSensorCollection().isRevLimitSwitchClosed());
        	SmartDashboard.putNumber("Tilt Encoder", m_tiltController.getSelectedSensorPosition(0));
        	SmartDashboard.putNumber("TiltPotValue", m_tiltPot.get());
        	SmartDashboard.putBoolean("Is Tilt Falling", isTiltFalling(speed));
        	SmartDashboard.putBoolean("Is Tilt At Bottom", isTiltAtBottom());
        	SmartDashboard.putBoolean("Is Tilt At Top", isTiltAtTop());
        	SmartDashboard.putBoolean("Is Tilt Near Lift", isTiltNearLift());
        	SmartDashboard.putBoolean("Is Grab Closed", isGrabClosed());
        	SmartDashboard.putBoolean("Is Grab Open", isGrabOpen());
        	SmartDashboard.putBoolean("Is Grab Max", isGrabMax());
        	SmartDashboard.putBoolean("Is Tilt Vertical", isTiltVertical());
        	SmartDashboard.putNumber("Tilt Speed", speed);
        	
    	}
    }
   
    public void setTiltSpeed(double speed) {
    	double dSpeed = speed;
    	if(m_bSafetyEnable) {
    		if ( (isTiltAtBottom() && speed < 0) || (isTiltAtTop() && speed > 0) ) 
        	{
        		dSpeed = 0.0;
        	} else {
        		if ( isTiltFalling(speed) ) {
        			dSpeed = dSpeed * 0.5;
        		}
        	}
        
    	}
    	m_tiltController.set(dSpeed);
    	displayLimitSwitches(dSpeed);
    }
    
    private boolean isTiltFalling( double speed ) {
    	boolean bReturnVal = false;
    	if ( (speed < 0 && m_tiltPot.get() <= m_dTiltVertVal) 
    			|| (speed > 0 && m_tiltPot.get() >= m_dTiltVertVal) ) {
    		bReturnVal = true;
    	}
    	return bReturnVal;
    }
    
    public void tiltUp() {
    	setTiltSpeed(m_dTiltSpeed);
    }
    
    public void tiltDown() {
    	setTiltSpeed(-m_dTiltSpeed);
    }
    
    public void tiltStop() {
    	m_tiltController.set(0.0);
    }
    
    public void setTiltVertVal() {
    	m_dTiltVertVal = m_tiltPot.get();
    	m_tiltController.setSelectedSensorPosition(0, 0, 0);
    }
    
    public boolean isTiltAtBottom() {
    	boolean bReturnVal = false;
    	if ( m_tiltPot.get() <= (m_dTiltVertVal - m_dTiltFwdLim)) {
    		bReturnVal = true;
    	} else {
    		bReturnVal = m_tiltController.getSensorCollection().isRevLimitSwitchClosed();
    	}
    	return bReturnVal;
    }
    
    public boolean isTiltAtTop() {
    	boolean bReturnVal = false;
    	if ( m_tiltPot.get() >= (m_dTiltVertVal + m_dTiltRevLim)) {
    		bReturnVal = true;
    	} else {
    		bReturnVal = m_tiltController.getSensorCollection().isFwdLimitSwitchClosed();
    	}
    	return bReturnVal;
    }
    
    public boolean isTiltVertical() {
    	return m_switchTiltVertical.get();
    }
    
    public boolean isTiltNearLift() {
    	boolean bReturnVal = false;
    	if ( m_tiltPot.get() >= (m_dTiltVertVal - m_dTiltFwdLiftLim)) {
    		bReturnVal = true;
    	}
    	return bReturnVal;
    }
    
    public boolean isTiltForward() {
    	boolean bReturnVal = false;
    	if ( m_tiltPot.get() < m_dTiltVertVal) {
    		bReturnVal = true;
    	}
    	return bReturnVal;
    }
    
    public void grabClose() {
 
    	if(m_bSafetyEnable) {
    		if (isGrabClosed()) {
        		setGrabOpenFalse();
            	grabStop();
            	m_dCurrentOpenCount = 0.0;
        	} else {
        		m_grabController.set(-m_dGrabSpeed);
            	m_dCurrentOpenCount = m_dCurrentOpenCount - m_dCloseIncrement;
        	}
    	} else {
    		m_grabController.set(-m_dGrabSpeed);
    	}
    	
    	displayLimitSwitches(0);
    }
    public void grabOpen() {
    	//m_grabController.set(1);
    	if(m_bSafetyEnable) {
    		if (isGrabMax()) {
        		setGrabOpenTrue();
        		grabStop();
        	} else {
        		if(isTiltNearLift()) {
        			grabStop();
        		} else {
        		
        			m_grabController.set(m_dReleaseSpeed);
        			m_dCurrentOpenCount = m_dCurrentOpenCount + m_dOpenIncrement;
        		}
        	}
    	
    	} else {
    		m_grabController.set(m_dReleaseSpeed);
    	}
    	displayLimitSwitches(0);
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
    public boolean isGrabMax() {
    	//SmartDashboard.putNumber("CurrentOpenCount", m_dCurrentOpenCount);
    	//SmartDashboard.putNumber("MaxOpenCount", m_dMaxOpenCount);
    	//return (m_dCurrentOpenCount > m_dMaxOpenCount);
    	return false;
    }
}

