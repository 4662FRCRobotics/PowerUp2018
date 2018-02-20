package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class MoveLiftToTarget extends Command {
	
	private double m_dTarget;
	
    public MoveLiftToTarget( double target) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.m_liftSubsystem);
    	m_dTarget = target;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.m_liftSubsystem.moveLiftToTarget(m_dTarget);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.m_liftSubsystem.isLiftAtTarget(m_dTarget);
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.m_liftSubsystem.moveLift(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
