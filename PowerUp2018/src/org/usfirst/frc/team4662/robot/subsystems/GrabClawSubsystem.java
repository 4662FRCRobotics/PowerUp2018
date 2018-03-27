package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;
import org.usfirst.frc.team4662.robot.commands.Tilt;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *,
 */
public class GrabClawSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from , Commands.
	
	private WPI_TalonSRX m_grabController;
	private WPI_TalonSRX m_tiltController;
	private double m_dGrabSpeed;
	//private double m_dTiltSpeed;
	private boolean m_bIsGrabOpen;
	private double m_dReleaseSpeed;
	private double m_dMaxOpenCount;
	private double m_dCurrentOpenCount;
	private double m_dOpenIncrement;
	private double m_dCloseIncrement;
	private final int m_iMaxOpenIterations = 15;
	private Counter m_cntTiltEncoder;
	private double m_dTiltMoveSpeed;
	private double m_dTiltCurrentSpeed;
	private double m_dTiltPreviousSpeed;
	private int m_iTiltRefPosition;
	private boolean m_bSafetyEnable;
	private double m_dTiltMotorDirection;
	
	public GrabClawSubsystem() {
		m_grabController = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("GrabController"));
		//m_grabController.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		//m_grabController.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, 0);
		m_grabController.overrideLimitSwitchesEnable(true);
		m_tiltController = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("TiltController"));
		m_tiltController.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_tiltController.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_tiltController.setNeutralMode(NeutralMode.Brake);
		m_dGrabSpeed = 0.7; 
		m_dReleaseSpeed = 1.0;
		//m_dTiltSpeed = 0.5;
		m_dTiltCurrentSpeed = 0;
		m_bIsGrabOpen = false;
		m_dCurrentOpenCount = 0.0;
		m_dOpenIncrement = 10.0;
		m_dCloseIncrement = m_dOpenIncrement/2;
		m_dMaxOpenCount = m_dOpenIncrement * m_iMaxOpenIterations;
		m_cntTiltEncoder = new Counter();
		m_cntTiltEncoder.setUpSource(Robot.m_robotMap.getPortNumber("TiltEncoder"));
		//m_cntTiltEncoder.setUpDownCounterMode();
		m_cntTiltEncoder.setUpSourceEdge(true,false);
		m_bSafetyEnable = true;
		m_dTiltPreviousSpeed = 0;
		m_iTiltRefPosition = 0;
		m_dTiltMotorDirection = -1;
		
		
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
    
    public void displayLimitSwitches() {
    	System.out.println("In Dashboard Display Limits" + Robot.m_robotMap.isDashboardTest());
    	if ( Robot.m_robotMap.isDashboardTest()) {
    		//SmartDashboard.putBoolean("Grab Limit Forward", m_grabController.getSensorCollection().isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Grab Limit Reverse", m_grabController.getSensorCollection().isRevLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tilt Limit Forward", m_tiltController.getSensorCollection().isFwdLimitSwitchClosed());
        	SmartDashboard.putBoolean("Tilt Limit Reverse", m_tiltController.getSensorCollection().isRevLimitSwitchClosed());
        	SmartDashboard.putNumber("Tilt Encoder", getTiltEncoder(m_dTiltCurrentSpeed));
        	SmartDashboard.putNumber("Raw Tilt Encoder", m_cntTiltEncoder.get());
        	SmartDashboard.putNumber("Tilt Current Speed", m_dTiltCurrentSpeed);
        	SmartDashboard.putBoolean("Is Tilt At Bottom", isTiltAtBottom());
        	SmartDashboard.putBoolean("Is Tilt At Top", isTiltAtTop());
        	SmartDashboard.putBoolean("Is Tilt Near Lift", isTiltNearLift());
        	SmartDashboard.putBoolean("Is Grab Closed", isGrabClosed());
        	SmartDashboard.putBoolean("Is Grab Open", isGrabOpen());
        	SmartDashboard.putBoolean("Is Grab Max", isGrabMax());
        	
    	}
    }
   
    public void setTiltSpeed(double speed) {
    	m_dTiltCurrentSpeed = speed;
    	double dDeadbandRange = 0.04;
    	
    	if (Math.abs(m_dTiltCurrentSpeed) < dDeadbandRange) {
    		m_dTiltCurrentSpeed = 0;
    		//m_iTiltRefPosition = getTiltEncoder(m_dTiltPreviousSpeed);
			//m_cntTiltEncoder.reset();
    	} else {
    		if (m_dTiltCurrentSpeed > 0) {
    			m_dTiltCurrentSpeed = 1;
    		} else {
    			m_dTiltCurrentSpeed = -1;
    		}
    	}
    	if(m_bSafetyEnable) {
    		if ( (isTiltAtBottom() && speed < 0) || (isTiltAtTop() && speed > 0) ) 
        	{
    			m_dTiltCurrentSpeed = 0.0;
        	}
        
    	}
     	checkTiltDirectionChange();
    	m_tiltController.set(m_dTiltCurrentSpeed * m_dTiltMotorDirection );
    	displayLimitSwitches();
    }
    
 private void checkTiltDirectionChange() {
    	
    	if (( m_dTiltCurrentSpeed <= 0 && m_dTiltPreviousSpeed > 0) || (m_dTiltCurrentSpeed > 0 && m_dTiltPreviousSpeed <= 0) || (m_dTiltCurrentSpeed == 0 && m_dTiltPreviousSpeed < 0)
    			) {
			m_iTiltRefPosition = getTiltEncoder(m_dTiltPreviousSpeed);
			m_cntTiltEncoder.reset();
			m_dTiltPreviousSpeed = m_dTiltCurrentSpeed;
    	}
    }
    
    private int getTiltEncoder(double dSpeed) {
    	int iReturnValue = 0;
    	if (m_dTiltPreviousSpeed <= 0) {
    		iReturnValue = m_iTiltRefPosition +  m_cntTiltEncoder.get();
    	} else {
    		iReturnValue = m_iTiltRefPosition -  m_cntTiltEncoder.get();
    	}
    	return iReturnValue;
    }
    
    public void tiltUp() {
    	setTiltSpeed(m_dTiltMoveSpeed);
    }
    
    public void tiltDown() {
    	setTiltSpeed(-m_dTiltMoveSpeed);
    }
    
    public void tiltStop() {
    	m_tiltController.set(0.0);
    }
    
    public void setTiltVertVal() {
    	m_iTiltRefPosition = 0;
		m_cntTiltEncoder.reset();
    	/*m_dTiltVertVal = m_tiltPot.get();
    	m_tiltController.setSelectedSensorPosition(0, 0, 0);*/
    }
    
    public boolean isTiltAtBottom() {
    	boolean bReturnVal = false;
    	//if ( m_tiltPot.get() <= (m_dTiltVertVal - m_dTiltFwdLim)) {
    	//	bReturnVal = true;
    	//} else {
    		bReturnVal = m_tiltController.getSensorCollection().isFwdLimitSwitchClosed();
    	//}
    	return bReturnVal;
    }
    
    public boolean isTiltAtTop() {
    	boolean bReturnVal = false;
    	//if ( m_tiltPot.get() >= (m_dTiltVertVal + m_dTiltRevLim)) {
    		//bReturnVal = true;
    	//} else {
    		bReturnVal = m_tiltController.getSensorCollection().isRevLimitSwitchClosed();
    		if (bReturnVal) {
    			setTiltVertVal();
    		}
    	//}
    	return bReturnVal;
    }
    
    public boolean isTiltVertical() {
    	return false;
    }
    
    public boolean isTiltNearLift() {
    	return m_tiltController.getSensorCollection().isRevLimitSwitchClosed();
    	//if ( m_tiltPot.get() >= (m_dTiltVertVal - m_dTiltFwdLiftLim)) {
    		//bReturnVal = true;
    	//}
    }
    
   /* public boolean isTiltForward() {
    	boolean bReturnVal = false;
    	if ( m_tiltPot.get() < m_dTiltVertVal) {
    		bReturnVal = true;
    	}
    	return bReturnVal;
    }*/
    
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
    	
    	displayLimitSwitches();
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
    public boolean isGrabMax() {
    	//SmartDashboard.putNumber("CurrentOpenCount", m_dCurrentOpenCount);
    	//SmartDashboard.putNumber("MaxOpenCount", m_dMaxOpenCount);
    	//return (m_dCurrentOpenCount > m_dMaxOpenCount);
    	return false;
    }
}

