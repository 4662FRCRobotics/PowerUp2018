package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class TurnAnglePID extends Command {

	private double m_dAngle;
	private boolean m_bIsDashboard;
	
    public TurnAnglePID(double angle) {
    	requires(Robot.m_driveSubsystem);
    	m_dAngle = angle;
    	m_bIsDashboard = false;
    }
    
    public TurnAnglePID() {
    	requires(Robot.m_driveSubsystem);
    	SmartDashboard.putNumber("TurnAngleTest", 0);
    	m_bIsDashboard = true;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	if ( m_bIsDashboard ) {
    		m_dAngle = Robot.m_driveSubsystem.getDashboardAngle();
    	}
    	Robot.m_driveSubsystem.setTurnAngle(m_dAngle);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return Robot.m_driveSubsystem.turnAngleOnTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.m_driveSubsystem.arcadeDrive(0, 0);
    	Robot.m_driveSubsystem.disableTurnAngle();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
