package frc.robot.SwerveSim;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;

public class SwerveModuleSim {
    LinearSystem<N1,N1,N1> drivePlant = LinearSystemId.createFlywheelSystem(DCMotor.getKrakenX60(1), 0.09, 6.75);
    FlywheelSim driveSim = new FlywheelSim(drivePlant, DCMotor.getKrakenX60(1), 0);

    LinearSystem<N1,N1,N1> turnPlant = LinearSystemId.createFlywheelSystem(DCMotor.getNEO(1), 0.04, 12.8);
    FlywheelSim turnSim = new FlywheelSim(turnPlant, DCMotor.getNEO(1), 0);

    private SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0.15, 2.4);

    private PIDController drivePIDController = new PIDController(0.001, 0, 0);
    private PIDController turnPIDController = new PIDController(0.06, 0, 0);

    private double drivePos = 0;
    private double driveVel = 0;

    private double turnPosRad = 0;

    public void periodic() {
        driveSim.update(0.02);
        turnSim.update(0.02);

        double angleDiff = turnSim.getAngularVelocityRadPerSec() * 0.02;

        turnPosRad += angleDiff;

        while (turnPosRad < 0) {
            turnPosRad += 2.0 * Math.PI;
        }

        while (turnPosRad > 2*Math.PI) {
            turnPosRad -= 2*Math.PI;
        }

        drivePos = drivePos + getDriveVelocityMetersPerSec() * 0.02;
        driveVel = getDriveVelocityMetersPerSec();
    }

    public SwerveModuleState getState() {
        return new SwerveModuleState(driveVel, new Rotation2d(turnPosRad));
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(drivePos, new Rotation2d(turnPosRad));
    }

    public void setDesiredState(SwerveModuleState state) {
        SwerveModuleState optimizedStates = SwerveModuleSim.optimize(state, turnPosRad);

        driveUsingPID(optimizedStates.speedMetersPerSecond);
        turnUsingPID(optimizedStates.angle.getRadians());
    }

    public void driveUsingPID(double setPoint) {
        double feedforward = this.feedforward.calculate(setPoint);
        double pid = drivePIDController.calculate(driveVel, setPoint);
        setDriveVoltage(pid + feedforward);
    }
    
    public void turnUsingPID(double setPoint) {
        double PID = turnPIDController.calculate(turnPosRad, setPoint);
        setTurnVoltage(PID);
    }
 
    private static SwerveModuleState optimize(SwerveModuleState desiredState, double currentAngle) {
        double angleDiff = (desiredState.angle.getRadians() - currentAngle) % 2*Math.PI;
        double targetAngle = currentAngle + angleDiff;
        double targetSpeed = desiredState.speedMetersPerSecond;

        if (angleDiff <= Math.toRadians(-270)) {
            targetAngle += 2*Math.PI;
        }
        else if (Math.toRadians(-90) > angleDiff && angleDiff > Math.toRadians(-270)) {
            targetAngle += Math.PI;
            targetSpeed = -targetSpeed;
        }
        else if (Math.toRadians(90) < angleDiff && angleDiff < Math.toRadians(270)) {
            targetAngle -= Math.PI;
            targetSpeed = -targetSpeed;
        }
        else if (angleDiff >=270) {
            targetAngle -= 2*Math.PI;
        }

        return new SwerveModuleState(targetSpeed, new Rotation2d(targetAngle));
    }

    public void setDriveVoltage(double volts) 
    {
        driveSim.setInputVoltage(MathUtil.clamp(volts, -12, 12));
    }

    public void setTurnVoltage(double volts) 
    {
        turnSim.setInputVoltage(MathUtil.clamp(volts, -12, 12));
    }

    private double getDriveVelocityMetersPerSec() {
        return driveSim.getAngularVelocityRadPerSec() * 0.0508;
    }


}
