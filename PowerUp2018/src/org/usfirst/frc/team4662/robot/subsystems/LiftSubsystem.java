package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;
import org.usfirst.frc.team4662.robot.commands.MoveLift;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class LiftSubsystem extends Subsystem {

	private WPI_TalonSRX m_leftLiftController1;
	//private WPI_TalonSRX m_rightLiftController1;
	private SpeedControllerGroup m_liftControlGroup;
	private double kdLiftUpSpeed;
	private double kdLiftDownSpeed;
	private double kdLiftTop;
	private double kdLiftBottom;
	private double kdSpeedHold;
	private double m_dLiftSpeed;
	
	public LiftSubsystem() {
		m_leftLiftController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("leftLift1"));
		m_leftLiftController1.setNeutralMode(NeutralMode.Brake);
		m_leftLiftController1.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_leftLiftController1.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		//m_rightLiftController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("rightLift1"));
		m_liftControlGroup = new SpeedControllerGroup(m_leftLiftController1);
		kdLiftUpSpeed = 0.8;
		kdLiftDownSpeed = 0.6;
		kdLiftTop = 9000.0;
		kdLiftBottom = -2500;	
		kdSpeedHold = 0.1;
		m_dLiftSpeed = 0.0;
		m_leftLiftController1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
	}
    
    public void initDefaultCommand() {
        setDefaultCommand(new MoveLift());
    }
    
    public void moveLift( double speed ) {
    	if ( ( m_leftLiftController1.getSelectedSensorPosition(0) <= kdLiftBottom
    			&& speed < 0)
    			|| ( m_leftLiftController1.getSelectedSensorPosition(0) >= kdLiftTop 
    			&& speed > 0 ) ) {
    		m_liftControlGroup.set(kdSpeedHold);
    		m_dLiftSpeed = 0.0;
    	} else {
    		m_liftControlGroup.set(speed + kdSpeedHold);
    		m_dLiftSpeed = speed;
    	}
    	SmartDashboard.putNumber("Lift Encoder", m_leftLiftController1.getSelectedSensorPosition(0));
    	if (Robot.m_robotMap.isDashboardTest()) {
    		SmartDashboard.putBoolean("LiftUpperLimit", m_leftLiftController1.getSensorCollection().isFwdLimitSwitchClosed());
    		SmartDashboard.putBoolean("LiftBottomLimit", m_leftLiftController1.getSensorCollection().isRevLimitSwitchClosed());
    		SmartDashboard.putNumber("LiftSpeed", speed);
    	}
    }
    
    public void moveLiftUp() {
    	moveLift(kdLiftUpSpeed);
    }
    
    public void moveLiftDown() {
    	moveLift(-kdLiftDownSpeed);
    }
    
    public void setEncoderZero() {
    	m_leftLiftController1.setSelectedSensorPosition(0, 0, 0);
    }
    
    public void moveLiftToTarget(double target) {
    	if ( m_leftLiftController1.getSelectedSensorPosition(0) <= target ) {
			moveLiftUp();
		} else {
			moveLiftDown();
		}
    }
    
    public boolean isLiftAtTarget(double target) {
    	boolean bReturnVal = false;
    	if ( m_dLiftSpeed < 0 ) {
    		if ( m_leftLiftController1.getSelectedSensorPosition(0) <= target ) {
    			bReturnVal = true;
    		}
    	} else { 
    		if ( m_dLiftSpeed > 0 ) {
    			if ( m_leftLiftController1.getSelectedSensorPosition(0) >= target ) {
    				bReturnVal = true;
    			}
    		} else {
    			bReturnVal = true;
    		}
    	}
    	return bReturnVal;
    }
    
}

