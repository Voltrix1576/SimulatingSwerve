// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.SwerveSim.SwerveSimSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class SwerveDriveCommand extends Command {
  private SwerveSimSubsystem swerveSim;
  public SwerveDriveCommand() {
    swerveSim = SwerveSimSubsystem.getInstance();
    addRequirements(swerveSim);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double xv = RobotContainer.driverController.getLeftX() * swerveSim.maxV;
    double yv = RobotContainer.driverController.getLeftY() * swerveSim.maxV;
    double omega = RobotContainer.driverController.getRightX() * swerveSim.maxAV;

    if (Math.abs(RobotContainer.driverController.getLeftX()) < 0.1) {
      xv = 0;
    }
    if (Math.abs(RobotContainer.driverController.getLeftY()) < 0.1) {
      yv = 0;
    }
    if (Math.abs(RobotContainer.driverController.getRightX()) < 0.1) {
      omega = 0;
    }

    if (RobotContainer.driverController.a().getAsBoolean()) {
      //swerveSim.periodic();
      xv = swerveSim.xController.calculate(swerveSim.getPose().getY());
      yv = swerveSim.yController.calculate(swerveSim.getPose().getX());
      omega = swerveSim.thetaController.calculate(swerveSim.getPose().getRotation().getRadians());
      System.out.println(swerveSim.getPose());
      swerveSim.drive(xv, -yv, omega, true);
      return;
    }

    swerveSim.drive(-xv, yv, -omega, true);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
