package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TiltToBottom extends Command {

	private boolean m_bIsTimeOut;
	
    public TiltToBottom() {
      
    	requires(Robot.m_grabSubsystem);
    	m_bIsTimeOut = false;
    }
    
    public TiltToBottom(double timeOut) {
        
    	requires(Robot.m_grabSubsystem);
    	m_bIsTimeOut = true;
    	setTimeout(timeOut);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.m_grabSubsystem.tiltDown();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean bReturnValue = false;
    	if ( m_bIsTimeOut) {
    		bReturnValue = isTimedOut();
    	} else {
    		bReturnValue = Robot.m_grabSubsystem.isTiltAtBottom();
    	}
        return bReturnValue;
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
