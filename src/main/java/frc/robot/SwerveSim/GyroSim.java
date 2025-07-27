// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.SwerveSim;

import edu.wpi.first.math.geometry.Rotation2d;

public class GyroSim {
    private double yaw = 0;

    public void periodic() {
        double angleDiffRad = SwerveSimSubsystem.getInstance().getChassisSpeeds().omegaRadiansPerSecond * 0.02;
        Rotation2d currnetAngleDiff = Rotation2d.fromRadians(angleDiffRad);

        yaw = (yaw + currnetAngleDiff.getDegrees() + 360) % 360;

    }

    public double getAngle() {
        return yaw;
    }

    public Rotation2d getRotation2d() {
        return new Rotation2d(Math.toRadians(yaw));
    }
}
