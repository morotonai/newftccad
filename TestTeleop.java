package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp
public class TestTeleop extends LinearOpMode {
    private Servo wrist;
    private Servo leftClaw;
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        DcMotor extension = hardwareMap.dcMotor.get("extension");
        DcMotor pitchLeft = hardwareMap.dcMotor.get("pitchLeft");
        DcMotor pitchRight = hardwareMap.dcMotor.get("pitchRight");
        wrist = hardwareMap.get(Servo.class, "wrist");
        leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        pitchRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // Reset the motor encoder
        pitchRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pitchLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        pitchRight.setDirection(DcMotorSimple.Direction.FORWARD);
        double pitchTarget = 0;
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;
            double pitchPosition = pitchRight.getCurrentPosition();
            double kP = 0;
            //gravity feedforward?
            double angleFromTicks = ((-360 * pitchPosition)/751.8);
            double kG = 0.5;
            double G = kG * Math.cos(Math.toRadians(angleFromTicks + 130));
            if (gamepad2.dpad_up){
                pitchTarget = 40;
            }
            if (gamepad2.dpad_down){
                pitchTarget = 160;
            }
            double pitchPower = (kP * (pitchTarget - pitchPosition)) + G;
            pitchLeft.setPower(pitchPower);
            pitchRight.setPower(pitchPower);
            wrist.setPosition(0.4);
            leftClaw.setPosition(0.1);
            if (gamepad2.left_bumper){
                leftClaw.setPosition(0.3);
                sleep(200);
            }
            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
            extension.setPower(gamepad2.left_stick_y-0.2);
            telemetry.addData("target", pitchTarget);
            telemetry.addData("currentPos",pitchRight.getCurrentPosition());
            telemetry.update();
            }
    }
}
