package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveDistancePID extends Command {

	private double m_dDistance;
	private double m_dSpeed;
	private boolean m_bIsDashboard;
	private final double kdFeetPerSecond = Robot.m_robotMap.getDeviceDoubleVal("Drive", "maxvelocity", 10);
	
    public DriveDistancePID(double distance) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_driveSubsystem);
    	m_dSpeed = 0;
    	m_dDistance = distance;
    	m_bIsDashboard = false;
    } 
    
    public DriveDistancePID(double distance, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_driveSubsystem);
    	m_dSpeed = speed;
    	m_dDistance = distance;
    	m_bIsDashboard = false;
    } 
    
    public DriveDistancePID() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_driveSubsystem);
    	SmartDashboard.putNumber("DriveDistance", 10);
    	m_bIsDashboard = true;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	if ( m_bIsDashboard ) {
    		m_dDistance = Robot.m_driveSubsystem.getDashboardDistance();
    	}
    	
    	Robot.m_driveSubsystem.setKeepHeading();
    	if(m_dSpeed == 0) {
    		setTimeout(1 + Math.abs((m_dDistance / (kdFeetPerSecond * Robot.m_driveSubsystem.getDriveDistanceSpeed()))));
    		Robot.m_driveSubsystem.setDriveDistance(m_dDistance);
    	}else {
    		setTimeout(1 + Math.abs((m_dDistance / (kdFeetPerSecond * m_dSpeed))));
    		Robot.m_driveSubsystem.setDriveDistance(m_dDistance, m_dSpeed);
    	}
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
   }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.m_driveSubsystem.driveDistanceOnTarget() || isTimedOut();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.m_driveSubsystem.disableDriveDistance();
    	Robot.m_driveSubsystem.disableKeepHeading();
    	Robot.m_driveSubsystem.arcadeDrive(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
