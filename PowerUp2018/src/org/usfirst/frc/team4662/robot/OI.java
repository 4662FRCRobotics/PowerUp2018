/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.   vamos                     */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4662.robot;

import org.usfirst.frc.team4662.robot.commands.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	
	public Joystick m_driveStick;
	public JoystickButton m_keepHeading;
	public Joystick m_operatorPad;
	public JoystickButton m_liftUp;
	public JoystickButton m_liftDown;
	public JoystickButton m_tiltUp;
	public JoystickButton m_tiltDown;
	public JoystickButton m_grabClose;
	public JoystickButton m_grabOpen;
	public JoystickButton m_climbUp;
	public JoystickButton m_climbDown;
	public JoystickButton m_putPCubeDown;
	public JoystickButton toggleDriveCamera;
	
	public OI() {
		
		m_driveStick = new Joystick(0);
		m_keepHeading = new JoystickButton(m_driveStick,2);
		m_keepHeading.whileHeld(new KeepHeadingPID());
		m_climbUp = new JoystickButton(m_driveStick, 3);
		m_climbUp.whileHeld(new ClimbUp());
		toggleDriveCamera = new JoystickButton(m_driveStick, 5);
		toggleDriveCamera.whenPressed(new ToggleDriveCamera());
		
		m_operatorPad = new Joystick(1);
		m_liftUp = new JoystickButton(m_operatorPad, 5);
		m_liftUp.whileHeld(new MoveLiftUp());
		m_liftDown = new JoystickButton(m_operatorPad, 7);
		m_liftDown.whileHeld(new MoveLiftDown());
		m_tiltUp = new JoystickButton(m_operatorPad, 6);
		m_tiltUp.whileHeld(new TiltUp());
		m_tiltDown = new JoystickButton(m_operatorPad, 8);
		m_tiltDown.whileHeld(new TiltDown());
		m_grabClose = new JoystickButton(m_operatorPad, 1);
		m_grabClose.whileHeld(new GrabClose());
		m_grabOpen = new JoystickButton(m_operatorPad, 3);
		m_grabOpen.whileHeld(new GrabOpen());
		m_putPCubeDown = new JoystickButton(m_operatorPad, 2);
		m_putPCubeDown.whenPressed(new PutPCubeDown());
		
		SmartDashboard.putData("setLiftEncoderBottom", new SetLiftEncoderBottom());
		SmartDashboard.putData("SetTiltVertical", new SetTiltPotVert());
		
		if( Robot.m_robotMap.isDashboardTest()) {
			SmartDashboard.putData("PIDTest", new TurnAnglePID());
			SmartDashboard.putData("DriveDistancePID", new DriveDistancePID());
			
		}

	}
}
