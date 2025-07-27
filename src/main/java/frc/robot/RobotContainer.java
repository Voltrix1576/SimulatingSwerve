// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.SwerveSim.SwerveSimSubsystem;

public class RobotContainer {
  public static CommandXboxController driverController = new CommandXboxController(0);

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    driverController.rightTrigger().whileTrue(new InstantCommand(() -> SwerveSimSubsystem.getInstance().setVelocityFactor(0.5)))
    .whileFalse(new InstantCommand(() -> SwerveSimSubsystem.getInstance().setVelocityFactor(1)));
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
