package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class GrabWheelOpen extends Command {
	private double m_dSlowGrabSpeed;
	private double m_dFastGrabSpeed;
	
    public GrabWheelOpen() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_grabSubsystem);
    	setInterruptible(true);
    	m_dSlowGrabSpeed = .3;
    	m_dFastGrabSpeed = 1;
    	SmartDashboard.putNumber("SlowGrabSpeed",m_dSlowGrabSpeed);
    	SmartDashboard.putNumber("FastGrabSpeed",m_dFastGrabSpeed);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	m_dSlowGrabSpeed = SmartDashboard.getNumber("SlowGrabSpeed",m_dSlowGrabSpeed);
    	m_dFastGrabSpeed = SmartDashboard.getNumber("FastGrabSpeed",m_dFastGrabSpeed);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.m_grabSubsystem.grabOpen(getGrabSpeed());
    }
    
    private double getGrabSpeed() {
    	double dReturnValue = 0;
    	if ( Robot.m_oi.m_operatorPad.getPOV() == -1) {
    		dReturnValue = m_dSlowGrabSpeed;
    	} else {
    		dReturnValue = m_dFastGrabSpeed;
    	}
    	return dReturnValue;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.m_grabSubsystem.grabStop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
