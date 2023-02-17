package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.EnumMap;


public class SlideControl {
    public static double SLIDE_REST = 0.05;
    public static double SLIDE_REST_SLIDING = 0.001;
    public final Integer SLIDE_POWER = 1;
    private final EnumMap<States, Integer> mp = new EnumMap<States, Integer>(States.class);
    public DcMotor slideDc;
    public Servo intakeServo;
    private States currentState = States.REST;

    public void controlSlide(double power) {
        slideDc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideDc.setPower(power);
    }

    public void resetSlideEncoder() {
        slideDc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void controlIntake(double power) {
        intakeServo.setPosition(power);
    }

    public double intakePosition() {
        return intakeServo.getPosition();
    }

    public void initSlide(HardwareMap hardwareMap) {
        slideDc = hardwareMap.dcMotor.get("slide");
        slideDc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeServo = hardwareMap.servo.get("intake");
        slideDc.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        mp.put(States.GROUND, 100);
        mp.put(States.LOW, 1500);
        mp.put(States.MID, 2250);
        mp.put(States.HIGH, 3475);
    }

    public Integer getStateCoordinate(States state) {
        if (mp.containsKey(state))
            return mp.get(state);
        return 0;
    }

    public States getCurrentState() {
        return currentState;
    }

    // TODO fix it
    public synchronized void setCurrentState(States currentState) {
        if (this.currentState != States.REST)
            slideDc.setPower(SLIDE_REST); //TODO set it to the correct values
        this.currentState = currentState;
        if (currentState == States.REST)
            return;

        slideDc.setTargetPosition(getStateCoordinate(currentState));
        slideDc.setPower(SLIDE_POWER);
        if (slideDc.getCurrentPosition() > getStateCoordinate(currentState))
            slideDc.setPower(-SLIDE_POWER);
        slideDc.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    }
    // TODO fix it as well

    public synchronized void setCurrentStateToCustom(Integer coordinate) {
        if (this.currentState != States.REST)
            slideDc.setPower(SLIDE_REST);
        this.currentState = States.CUSTOM;

        slideDc.setTargetPosition(coordinate);
        slideDc.setPower(SLIDE_POWER);
        if (slideDc.getCurrentPosition() > coordinate)
            slideDc.setPower(-SLIDE_POWER);
        slideDc.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private enum States {
        GROUND,
        LOW,
        MID,
        HIGH,
        REST,
        CUSTOM,
    }


}