package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;
import org.usfirst.frc.team4662.robot.commands.MoveLift;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
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
	private double m_dLiftPIDP;
	private double m_dLiftPIDI;
	private double m_dLiftPIDD;
	private double m_dLiftPIDTolerance;
	private double m_dLiftPIDSpeed;
	private PIDController m_liftPID;
	
	public LiftSubsystem() {
		m_leftLiftController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("leftLift1"));
		m_leftLiftController1.setNeutralMode(NeutralMode.Brake);
		m_leftLiftController1.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		m_leftLiftController1.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		//m_rightLiftController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("rightLift1"));
		m_liftControlGroup = new SpeedControllerGroup(m_leftLiftController1);
		kdLiftUpSpeed = 1.0;
		kdLiftDownSpeed = 0.6;
		kdLiftTop = 9000.0;
		kdLiftBottom = -2500;	
		kdSpeedHold = 0.1;
		m_dLiftSpeed = 0.0;
		m_dLiftPIDP = Robot.m_robotMap.getPIDPVal("Lift", 0.2);
		m_dLiftPIDI = Robot.m_robotMap.getPIDIVal("Lift", 0.0);
		m_dLiftPIDD = Robot.m_robotMap.getPIDDVal("Lift", 0.0);
		m_dLiftPIDTolerance = Robot.m_robotMap.getPIDToleranceVal("Lift", 100);
		m_dLiftPIDSpeed = 1;
		m_leftLiftController1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		m_leftLiftController1.setSensorPhase(true);
		m_liftPID = new PIDController(0.2, 0.0, 0.0, new getLiftEncoder(), new putLiftSpeed()); 
	}
    
    public void initDefaultCommand() {
        setDefaultCommand(new MoveLift());
    }
    
    public void moveLift( double speed ) {
    	if ( ( m_leftLiftController1.getSelectedSensorPosition(0) >= kdLiftTop 
    			&& speed > 0 ) ) {
    		m_liftControlGroup.set(kdSpeedHold);
    		m_dLiftSpeed = 0.0;
    	} else { 
    		m_liftControlGroup.set(speed + kdSpeedHold);
    		m_dLiftSpeed = speed;
    	}
    	if ( m_leftLiftController1.getSensorCollection().isRevLimitSwitchClosed() ) {
    		setEncoderZero();
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
    
    public void enableLiftPID( double target) {
    	m_liftPID.reset();
    	m_liftPID.setInputRange(-Math.abs(1.1 * kdLiftTop), Math.abs(1.1 * kdLiftTop));
    	m_liftPID.setOutputRange(-Math.abs(m_dLiftPIDSpeed), Math.abs(m_dLiftPIDSpeed));
    	m_liftPID.setContinuous(true);
    	m_liftPID.setSetpoint(target);
    	m_liftPID.setAbsoluteTolerance(m_dLiftPIDTolerance);
    	m_liftPID.enable();
    }
    
    public void disableLiftPID() {
    	m_liftPID.disable();
    }
    
    public boolean isLiftOnTarget() {
    	return m_liftPID.onTarget();
    }
    
    private class getLiftEncoder implements PIDSource {

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			// TODO Auto-generated method stub
			return PIDSourceType.kDisplacement;
		}

		@Override
		public double pidGet() {
			// TODO Auto-generated method stub
			return m_leftLiftController1.getSelectedSensorPosition(0);
		}
    	
    }
    
    private class putLiftSpeed implements PIDOutput {

		@Override
		public void pidWrite(double output) {
			// TODO Auto-generated method stub
			moveLift(output);
		}
    	
    }
    
    
}

