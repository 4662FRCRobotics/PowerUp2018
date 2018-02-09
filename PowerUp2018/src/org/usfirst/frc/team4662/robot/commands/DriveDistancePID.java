package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveDistancePID extends Command {

	private double m_dDistance;
	private boolean m_bIsDashboard;
	
    public DriveDistancePID(double distance) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_driveSubsystem);
    	m_dDistance = distance;
    	m_bIsDashboard = false;
    } 
    
    public DriveDistancePID() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_driveSubsystem);
    	SmartDashboard.putNumber("DriveDistance", 0);
    	m_bIsDashboard = true;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	if ( m_bIsDashboard ) {
    		SmartDashboard.getNumber("DriveDistance", m_dDistance);
    	}
    	Robot.m_driveSubsystem.setDriveDistance(m_dDistance);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.m_driveSubsystem.driveDistanceOnTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.m_driveSubsystem.disableDriveDistance();
    	Robot.m_driveSubsystem.arcadeDrive(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
