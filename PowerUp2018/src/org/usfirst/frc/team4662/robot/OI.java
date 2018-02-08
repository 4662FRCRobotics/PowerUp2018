/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.   vamos                     */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4662.robot;

import org.usfirst.frc.team4662.robot.commands.MoveLiftDown;
import org.usfirst.frc.team4662.robot.commands.MoveLiftUp;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	
	public Joystick m_driveStick;
	public Joystick m_operatorPad;
	public JoystickButton m_liftUp;
	public JoystickButton m_liftDown;
	
	public OI() {
		
		m_driveStick = new Joystick(0);
		m_operatorPad = new Joystick(1);
		m_liftUp = new JoystickButton(m_operatorPad, 5);
		m_liftUp.whileHeld(new MoveLiftUp());
		m_liftDown = new JoystickButton(m_operatorPad, 7);
		m_liftDown.whileHeld(new MoveLiftDown());

	}
}
