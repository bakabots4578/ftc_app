/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class K9TeleOpTest extends OpMode {

	/*
	 * Note: the configuration of the servos is such that
	 * as the arm servo approaches 0, the arm position moves up (away from the floor).
	 * Also, as the claw servo approaches 0, the claw opens up (drops the game element).
	 */
	// TETRIX VALUES.
	final static double ARM_MIN_RANGE  = 0.20;
	final static double ARM_MAX_RANGE  = 0.90;
	final static double CLAW_MIN_RANGE  = 0.20;
	final static double CLAW_MAX_RANGE  = 0.7;

	// position of the arm servo.
	double claw1Position;

	// amount to change the arm servo position.
	double armDelta = 0.1;

	// position of the claw servo
	double claw2Position;

	// amount to change the claw servo position by
	double clawDelta = 0.1;

	DcMotor motorRight;
	DcMotor motorLeft;
	DcMotor motorRight2;
	DcMotor motorLeft2;
	DcMotor arm;
	Servo claw1;
	Servo claw2;

	/**
	 * Constructor
	 */
	public K9TeleOpTest() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {


		/*
		 * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */
		
		/*
		 * For the demo Tetrix K9 bot we assume the following,
		 *   There are two motors "motor_1" and "motor_2"
		 *   "motor_1" is on the right side of the bot.
		 *   "motor_2" is on the left side of the bot and reversed.
		 *   
		 * We also assume that there are two servos "servo_1" and "servo_6"
		 *    "servo_1" controls the arm joint of the manipulator.
		 *    "servo_6" controls the claw joint of the manipulator.
		 */
		motorRight2 = hardwareMap.dcMotor.get("rightFront");
		motorLeft2 = hardwareMap.dcMotor.get("leftFront");
		arm = hardwareMap.dcMotor.get("arm");
		motorRight = hardwareMap.dcMotor.get("right");
		motorLeft = hardwareMap.dcMotor.get("left");
		motorLeft.setDirection(DcMotor.Direction.REVERSE);
		
		claw1 = hardwareMap.servo.get("servo_1");
		claw2 = hardwareMap.servo.get("servo_2");

		// assign the starting position of the wrist and claw
		//armPosition = 0.2;
		//clawPosition = 0.2;
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		/*
		 * Gamepad 1
		 * 
		 * Gamepad 1 controls the motors via the left stick, and it controls the
		 * wrist/claw via the a,b, x, y buttons
		 */

		// throttle: left_stick_y ranges from -1 to 1, where -1 is full up, and
		// 1 is full down
		// direction: left_stick_x ranges from -1 to 1, where -1 is full left
		// and 1 is full right
		float throttle = -gamepad1.left_stick_y;
		float direction = gamepad1.left_stick_x;
		float right = throttle - direction;
		float left = throttle + direction;

		// clip the right/left values so that the values never exceed +/- 1
		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);

		// scale the joystick value to make it easier to control
		// the robot more precisely at slower speeds.
		right = (float)scaleInput(right);
		left =  (float)scaleInput(left);
		
		// write the values to the motors
		motorRight.setPower(right);
		motorLeft.setPower(left);
		motorRight2.setPower(right/3); // front wheels
		motorLeft2.setPower(left/3);  // /3 because of gear ratio
		/*
		/
		/
		/  GAMEPAD 2
		/
		/
		/
		*/
		float armThrottle = gamepad2.left_stick_y;
		armThrottle = Range.clip(armThrottle, -1, 1);
		armThrottle = (float)scaleInput(armThrottle);
		//down =  (float)scaleInput(down);
		arm.setPower(armThrottle);
		//arm.setPower(down);

		// update the position of the arm.
		if (gamepad2.a) {
			// if the A button is pushed on gamepad1, increment the position of
			// the arm servo.
			claw1Position += armDelta;
		}

		if (gamepad2.b) {
			// if the Y button is pushed on gamepad1, decrease the position of
			// the arm servo.
			claw1Position -= armDelta;
		}







		// update the position of the claw
		if (gamepad2.a) {
			claw2Position -= clawDelta;
		}

		if (gamepad2.b) {
			claw2Position += clawDelta;
		}

        // clip the position values so that they never exceed their allowed range.
        claw1Position = Range.clip(claw1Position, ARM_MIN_RANGE, ARM_MAX_RANGE);
        claw2Position = Range.clip(claw2Position, CLAW_MIN_RANGE, CLAW_MAX_RANGE);

		// write position values to the wrist and claw servo
		claw1.setPosition(claw1Position);
		claw2.setPosition(claw2Position);



		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */



        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("claw1", "claw1:  " + String.format("%.2f",   claw1Position));
        telemetry.addData("claw2", "claw 2:  " + String.format("%.2f", claw2Position));
        telemetry.addData("left tgt pwr",  "left  pwr: " + String.format("%.2f", left));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", right));

	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

    	
	/*
	 * This method scales the joystick input so for low joystick values, the 
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };
		
		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);
		
		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale = 0.0;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}

}
