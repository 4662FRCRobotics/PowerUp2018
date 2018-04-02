package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TurnLeftTimed extends Command {
	
	double m_dTimeout;
	
    public TurnLeftTimed(double dTimeout) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_driveSubsystem);
    	
    	m_dTimeout = dTimeout;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	setTimeout(m_dTimeout);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.m_driveSubsystem.arcadeDrive(0, -0.75);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return isTimedOut();
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
