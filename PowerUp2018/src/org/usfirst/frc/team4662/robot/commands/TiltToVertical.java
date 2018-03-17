package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TiltToVertical extends Command {

    public TiltToVertical() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_grabSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	/*if ( !Robot.m_grabSubsystem.isTiltVertical()) {
	    	if ( Robot.m_grabSubsystem.isTiltForward()) {
	    		Robot.m_grabSubsystem.tiltUp();
	    	} else {
	    		Robot.m_grabSubsystem.tiltDown();
	    	}
    	}*/
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
       // return Robot.m_grabSubsystem.isTiltVertical();
    	return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.m_grabSubsystem.tiltStop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	 end();
    }
}
