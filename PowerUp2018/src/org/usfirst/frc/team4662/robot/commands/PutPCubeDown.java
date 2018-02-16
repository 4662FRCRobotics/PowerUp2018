package org.usfirst.frc.team4662.robot.commands;

import org.usfirst.frc.team4662.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class PutPCubeDown extends CommandGroup {

    public PutPCubeDown() {
    	requires(Robot.m_grabSubsystem);
    	addSequential(new TiltToBottom(1));
    	addSequential(new GrabOpen());
    }
}
