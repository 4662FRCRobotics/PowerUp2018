package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class KeepHeadingPID extends Command {

    public KeepHeadingPID() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_driveSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.m_driveSubsystem.setKeepHeading();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double throttle = 2 / (Robot.m_oi.m_driveStick.getThrottle() + 3.0);
    	Robot.m_driveSubsystem.driveKeepHeading(Robot.m_oi.m_driveStick.getY() * throttle);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
