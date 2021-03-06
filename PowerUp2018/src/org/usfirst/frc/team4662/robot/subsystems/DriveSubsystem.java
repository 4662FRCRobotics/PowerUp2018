package org.usfirst.frc.team4662.robot.subsystems;

import org.usfirst.frc.team4662.robot.Robot;
import org.usfirst.frc.team4662.robot.commands.ArcadeDrive;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveSubsystem extends Subsystem {
	
	//declare section for speed controllers
	private WPI_TalonSRX m_leftController1;
	private WPI_TalonSRX m_leftController2;
	private WPI_TalonSRX m_rightController1;
	private WPI_TalonSRX m_rightController2;
	private SpeedControllerGroup m_leftControlGroup;
	private SpeedControllerGroup m_rightControlGroup;
	private DifferentialDrive m_robotDrive;
	private PIDController m_DriveDistance;
	private double m_dDriveDistanceP;
	private double m_dDriveDistanceI;
	private double m_dDriveDistanceD;
	private double m_dDriveDistanceTolerance;
	private double m_dDistance;
	private double m_dDriveDistanceSpeed;
	private double m_dMotorToAxleReduction;
	private double m_dWheelDiameter;
	private double m_dEncoderPulseCnt;
	//declare for turn to angle
	//private AHRS m_AHRSnavX;
	private ADXRS450_Gyro m_AHRSnavX;
	private PIDController m_turnAngle;
	private double m_dTurnAngleP;
	private double m_dTurnAngleI;
	private double m_dTurnAngleD;
	private double m_dTurnAngleTolerance;
	private double m_dAngle;
	
	//declare for keepheading 
	private PIDController m_keepHeading;
	private double m_dkeepHeadingP;
	private double m_dkeepHeadingI;
	private double m_dkeepHeadingD;
	private double m_dkeepHeadingTolerance;
	private volatile double m_dSteeringHeading;
	
	
	public DriveSubsystem() {
		
		//instantiation for speed controller
		m_leftController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("leftController1"));
		m_leftController2 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("leftController2"));
		m_rightController1 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("rightController1"));
		m_rightController2 = new WPI_TalonSRX(Robot.m_robotMap.getPortNumber("rightController2"));
		m_leftControlGroup = new SpeedControllerGroup(m_leftController1, m_leftController2);
		m_rightControlGroup = new SpeedControllerGroup(m_rightController1, m_rightController2);
		m_leftControlGroup.setInverted(false);
		m_rightControlGroup.setInverted(false);
		m_robotDrive = new DifferentialDrive(m_leftControlGroup, m_rightControlGroup);
		
		m_leftController1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		m_rightController2.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		m_leftController1.setSensorPhase(true);
		m_rightController2.setSensorPhase(false);		
		
		//instantiation for drive a distance	
		m_dDistance = 0;
		m_dDriveDistanceP = Robot.m_robotMap.getPIDPVal("DriveDistance", 0.2);
		m_dDriveDistanceI = Robot.m_robotMap.getPIDIVal("DriveDistance", 0.0);
		m_dDriveDistanceD = Robot.m_robotMap.getPIDDVal("DriveDistance", 0.4);
		m_DriveDistance = new PIDController(m_dDriveDistanceP, m_dDriveDistanceI, m_dDriveDistanceD, new getRightEncoder(), new putDriveDistance() );
		m_dDriveDistanceTolerance = Robot.m_robotMap.getPIDToleranceVal("DriveDistance", 2);
		m_dDriveDistanceSpeed = 0.5;
		m_dEncoderPulseCnt = 20 * 2 * 2;
		// 2 channel quadrature output with 20 pulses per channel per revolution for sensing speed and direction.
		//times 2 for rise and fall for pulse
		m_dMotorToAxleReduction = ( 50/12 ) * ( 40/34 );
		m_dWheelDiameter = 6.0;
		m_dDistance = getDashboardDistance();
		
		//instantiation for turn to angle
		//m_AHRSnavX = new AHRS(SPI.Port.kMXP);
		m_AHRSnavX = new ADXRS450_Gyro();
		m_dTurnAngleP = Robot.m_robotMap.getPIDPVal("TurnAngle", 0.2);
		m_dTurnAngleI = Robot.m_robotMap.getPIDIVal("TurnAngle", 0.4);
		m_dTurnAngleD = Robot.m_robotMap.getPIDDVal("TurnAngle", 0.4);
		m_turnAngle = new PIDController(m_dTurnAngleP, m_dTurnAngleI, m_dTurnAngleD, new getSourceAngle(), new putOutputTurn() );
		m_dTurnAngleTolerance = Robot.m_robotMap.getPIDToleranceVal("TurnAngle", 2);
		m_dAngle = 0;
		m_dAngle = getDashboardAngle();
		
		//instantiation for keepheading
		m_dkeepHeadingP = Robot.m_robotMap.getPIDPVal("keepHeading", 0.2);
		m_dkeepHeadingI = Robot.m_robotMap.getPIDIVal("keepHeading", 0.0);
		m_dkeepHeadingD = Robot.m_robotMap.getPIDDVal("keepHeading", 0.4);
		m_keepHeading = new PIDController(m_dkeepHeadingP, m_dkeepHeadingI, m_dkeepHeadingD, new getSourceAngle(), new putSteeringHeading() );
		m_dkeepHeadingTolerance = Robot.m_robotMap.getPIDToleranceVal("keepHeading", 2);
		m_dSteeringHeading = 0;
	}

    public void initDefaultCommand() {
    	setDefaultCommand(new ArcadeDrive());
    }
    
    //default command for basic arcade drive
    public void arcadeDrive(double throttle, double turn) {
    	//for an even number of gear stages put 1, for an odd number of gear stages put -1
    	double dDriveInvert = 1;
    	m_robotDrive.arcadeDrive(throttle * dDriveInvert, turn);
    	smartDashBoardDiplay();
    	
    }
    
    private void smartDashBoardDiplay() {
    	SmartDashboard.putNumber("navxGyro", m_AHRSnavX.getAngle() );
    	SmartDashboard.putNumber("leftencoder", m_leftController1.getSelectedSensorPosition(0));
    	SmartDashboard.putNumber("rightencoder", m_rightController2.getSelectedSensorPosition(0));
    }
    
    public void setDriveAuto() {
    	m_robotDrive.setSafetyEnabled(false);
    }
    
    public void setDriveTeleop() {
    	m_robotDrive.setSafetyEnabled(true); 	
    }
    
    //get gyroscope angle 
    private double getGyroAngle() {
    	return m_AHRSnavX.getAngle();
    }
    
    //******************************************************************************
    //this block is for the drive distance pid control
    //******************************************************************************
    public double getDashboardDistance() {
    	if ( Robot.m_robotMap.isDashboardTest()) {
    		m_dDistance = SmartDashboard.getNumber("DriveDistance", m_dDistance);
        	m_dDriveDistanceP = SmartDashboard.getNumber("DriveDistanceP", m_dDriveDistanceP);
    		m_dDriveDistanceI = SmartDashboard.getNumber("DriveDistanceI", m_dDriveDistanceI);
    		m_dDriveDistanceD = SmartDashboard.getNumber("DriveDistanceD", m_dDriveDistanceD);
    		m_dDriveDistanceTolerance = SmartDashboard.getNumber("DriveDistanceTolerance", m_dDriveDistanceTolerance);
    		m_dDriveDistanceSpeed = SmartDashboard.getNumber("DriveDistanceSpeed", m_dDriveDistanceSpeed);
    		SmartDashboard.putNumber("DriveDistance", m_dDistance);
        	SmartDashboard.putNumber("DriveDistanceP", m_dDriveDistanceP);
    		SmartDashboard.putNumber("DriveDistanceI", m_dDriveDistanceI);
    		SmartDashboard.putNumber("DriveDistanceD", m_dDriveDistanceD);
    		SmartDashboard.putNumber("DriveDistanceTolerance", m_dDriveDistanceTolerance);
    		SmartDashboard.putNumber("DriveDistanceSpeed", m_dDriveDistanceSpeed);
    	}
    	
    	return m_dDistance;
    }
    
    public void disableDriveDistance() {
    	m_DriveDistance.disable();
    }
    
    public double getDriveDistanceSpeed() {
    	return m_dDriveDistanceSpeed;
    }
    
    public void setDriveDistance(double distance) {
    	setDriveDistance(distance, m_dDriveDistanceSpeed);
    }
    
    public void setDriveDistance(double distance, double speed) {
    	
		double pidEncoderTarget = 12 * distance * m_dEncoderPulseCnt * m_dMotorToAxleReduction / (m_dWheelDiameter * Math.PI);
		SmartDashboard.putNumber("Encoder target", pidEncoderTarget);
		m_DriveDistance.reset();
		m_leftController1.setSelectedSensorPosition(0, 0, 0);
		m_rightController2.setSelectedSensorPosition(0, 0, 0);
		//0 encoders
		m_DriveDistance.setInputRange(-Math.abs(pidEncoderTarget), Math.abs(pidEncoderTarget));
		m_DriveDistance.setOutputRange(-speed , speed);
		m_DriveDistance.setPID(m_dDriveDistanceP, m_dDriveDistanceI, m_dDriveDistanceD);
		m_DriveDistance.setAbsoluteTolerance(m_dDriveDistanceTolerance);
		m_DriveDistance.setContinuous(false);
		m_DriveDistance.setSetpoint(pidEncoderTarget);
		m_DriveDistance.enable();
    }
    
    public boolean driveDistanceOnTarget() {
    	return m_DriveDistance.onTarget();
    }
    
    //******************************************************************************
    //this block is for the turn angle pid control
    //******************************************************************************
    
    //disable the turn angle pid
    public void disableTurnAngle() {
    	m_turnAngle.disable();
    }
    
    //sets values and enables pid
    public void setTurnAngle(double angle) {
    	setTurnAngle(angle, 0.75);
    }
    public void setTurnAngle(double angle, double throttle) {
    	m_turnAngle.reset();
    	m_AHRSnavX.reset();
    	//m_AHRSnavX.zeroYaw();
    	m_turnAngle.setInputRange(-180.0f, 180.0f);
    	m_turnAngle.setOutputRange(-throttle, throttle);
    	m_turnAngle.setPID(m_dTurnAngleP, m_dTurnAngleI, m_dTurnAngleD);
    	m_turnAngle.setAbsoluteTolerance(m_dTurnAngleTolerance);
    	m_turnAngle.setContinuous(false);
    	m_turnAngle.setSetpoint(angle);
    	m_turnAngle.enable();
    }
    
    //returning the boolean for onTarget
    public boolean turnAngleOnTarget() {
    	return m_turnAngle.onTarget();
    }
    
    //gets the turn angle from the dashboard
    public double getDashboardAngle() {
    	if (Robot.m_robotMap.isDashboardTest()) {
    		m_dTurnAngleP = SmartDashboard.getNumber("TurnAngleP", m_dTurnAngleP);
    		m_dTurnAngleI = SmartDashboard.getNumber("TurnAngleI", m_dTurnAngleI);
    		m_dTurnAngleD = SmartDashboard.getNumber("TurnAngleD", m_dTurnAngleD);
    		m_dTurnAngleTolerance = SmartDashboard.getNumber("TurnAngleTolerance", m_dTurnAngleTolerance);
    		m_dAngle = SmartDashboard.getNumber("TurnAngleTest", m_dAngle);
    		SmartDashboard.putNumber("TurnAngleP", m_dTurnAngleP);
    		SmartDashboard.putNumber("TurnAngleI", m_dTurnAngleI);
    		SmartDashboard.putNumber("TurnAngleD", m_dTurnAngleD);
    		SmartDashboard.putNumber("TurnAngleTolerance", m_dTurnAngleTolerance);
    		SmartDashboard.putNumber("TurnAngleTest", m_dAngle);
    	}
    	return m_dAngle;
    }
    
    //******************************************************************************
    //this block is for the keep heading pid control
    //******************************************************************************
    
    //Invoking arcadedrive but letting the gyro keep the heading
    public void driveKeepHeading(double throttle) {
    	arcadeDrive(throttle, m_dSteeringHeading);
    }
    
    //disable the keep heading pid
    public void disableKeepHeading() {
    	m_keepHeading.disable();
    }
    
    //sets values and enables keep heading pid
    public void setKeepHeading() {
    	if (m_AHRSnavX != null) {
    		System.out.println("In SetKeepHeading; 1: m_AHRSnavX = " + m_AHRSnavX);
    		m_keepHeading.reset();
    		System.out.println("In SetKeepHeading; 2");
    		m_AHRSnavX.reset();
    		System.out.println("In SetKeepHeading; 3");
    		//m_AHRSnavX.zeroYaw();
    		m_keepHeading.setInputRange(-180.0f, 180.0f);
    		System.out.println("In SetKeepHeading; 4");
    		m_keepHeading.setOutputRange(-.75, .75);
    		System.out.println("In SetKeepHeading; 5");
    		m_keepHeading.setPID(m_dkeepHeadingP, m_dkeepHeadingI, m_dkeepHeadingD);
    		System.out.println("In SetKeepHeading; 6");
    		m_keepHeading.setAbsoluteTolerance(m_dkeepHeadingTolerance);
    		System.out.println("In SetKeepHeading; 7");
    		m_keepHeading.setContinuous(true);
    		System.out.println("In SetKeepHeading; 8");
    		m_keepHeading.setSetpoint(0.0);
    		System.out.println("In SetKeepHeading; 9");
    		m_keepHeading.enable();
    		System.out.println("In SetKeepHeading; 10");
    	}
    }
    //defines the pidsource for the gyro turn
    private class getSourceAngle implements PIDSource {

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
			
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		@Override
		public double pidGet() {
			return getGyroAngle();
		}
    	
    }
    
    //pid output for the gyro turn
    private class putOutputTurn implements PIDOutput {

		@Override
		public void pidWrite(double output) {
			arcadeDrive(0, output);
		}
    	
    }
    
    private class putSteeringHeading implements PIDOutput {

		@Override
		public void pidWrite(double output) {
			m_dSteeringHeading = output;
		}
    	
    }
    
    //Distance ride PIDSource
    private class getLeftEncoder implements PIDSource {

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
			return m_leftController1.getSelectedSensorPosition(0);
		}
    	
    	
    }
    
    private class getRightEncoder implements PIDSource {

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
			return m_rightController2.getSelectedSensorPosition(0);
		}
    	
    	
    }
    
    private class putDriveDistance implements PIDOutput {

		@Override
		public void pidWrite(double output) {
			arcadeDrive(-output, m_dSteeringHeading);
			// TODO Auto-generated method stub
			
		}
    	
    }

}

