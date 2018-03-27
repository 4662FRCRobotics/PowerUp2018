package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TiltToVertical extends Command {

	private boolean m_bIsTimeOut;
	private double m_dTimeOut;
	
    public TiltToVertical() {
      
    	requires(Robot.m_grabSubsystem);
    	m_bIsTimeOut = false;
    	m_dTimeOut = 2.00;
    }
    
    public TiltToVertical(double timeOut) {
        
    	requires(Robot.m_grabSubsystem);
    	m_bIsTimeOut = true;
    	m_dTimeOut = timeOut;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	setTimeout(m_dTimeOut);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.m_grabSubsystem.tiltUp();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return isTimedOut() || Robot.m_grabSubsystem.isTiltAtTop();
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
