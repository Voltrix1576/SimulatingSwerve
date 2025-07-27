// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.SwerveSim;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveSimSubsystem extends SubsystemBase {

  private static SwerveSimSubsystem instance;

  private Field2d field = new Field2d();

  public double maxV = 4.57;
  public double maxAV = 9.23;

  SwerveModuleSim frontLeft = new SwerveModuleSim();
  SwerveModuleSim frontRight = new SwerveModuleSim();
  SwerveModuleSim rearLeft = new SwerveModuleSim();
  SwerveModuleSim rearRight = new SwerveModuleSim();

  GyroSim gyro = new GyroSim();

  private final Translation2d frontLeftLocation = new Translation2d(0.7 / 2, 0.7 / 2);
  private final Translation2d frontRightLocation = new Translation2d(0.7 / 2, -(0.7 / 2));
  private final Translation2d rearLeftLocation = new Translation2d(-(0.7 / 2), 0.7 / 2);
  private final Translation2d rearRightLocation = new Translation2d(-(0.7 / 2), -(0.7 / 2));

  SwerveDriveKinematics kinematics = new SwerveDriveKinematics(frontLeftLocation, frontRightLocation, rearLeftLocation, rearRightLocation);

  SwerveDrivePoseEstimator poseEstimator = new SwerveDrivePoseEstimator(kinematics, gyro.getRotation2d(), getPositions(), new Pose2d(0, 0, new Rotation2d(0)));
  
  StructArrayPublisher<SwerveModuleState> publisherStates = NetworkTableInstance.getDefault()
    .getStructArrayTopic("MyStates", SwerveModuleState.struct).publish();

  
  StructPublisher<Pose2d> publisherPose2d = NetworkTableInstance.getDefault()
    .getStructTopic("MyPose2d", Pose2d.struct).publish();

  StructPublisher<Pose3d> publisherPose3d = NetworkTableInstance.getDefault()
    .getStructTopic("MyPose3d", Pose3d.struct).publish();


  public SwerveSimSubsystem() {
    SmartDashboard.putData("Field", field);
    
  }

  
  public void setVelocityFactor(double factor) {
    maxV = 4.57 * factor;
    maxAV = 9.23 * factor;
  }

  private SwerveModuleState[] getStates() {
    return new SwerveModuleState[] {
      frontLeft.getState(),
      frontRight.getState(),
      rearLeft.getState(),
      rearRight.getState() };
  }

  private SwerveModulePosition[] getPositions() {
    return new SwerveModulePosition[] {
      frontLeft.getPosition(),
      frontRight.getPosition(),
      rearLeft.getPosition(),
      rearRight.getPosition() };
  }

  public ChassisSpeeds getChassisSpeeds() {
    return kinematics.toChassisSpeeds(getStates());
  }


  public void setSwerveState(SwerveModuleState[] states) {
    SwerveDriveKinematics.desaturateWheelSpeeds(states, 4.57);
    frontLeft.setDesiredState(states[0]);
    frontRight.setDesiredState(states[1]);
    rearLeft.setDesiredState(states[2]);
    rearRight.setDesiredState(states[3]);
  }

  public void drive(double xv, double yv, double omega, boolean isFieldRelative) {
    SwerveModuleState[] states = kinematics.toSwerveModuleStates(
      isFieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(xv, yv, omega, new Rotation2d(Math.toRadians(gyro.getAngle()))) 
      : new ChassisSpeeds(xv,yv,omega));
    
    setSwerveState(states);
  }



  public static SwerveSimSubsystem getInstance() {
    if (instance == null) {
      instance = new SwerveSimSubsystem();
    }
    return instance;
  }

  @Override
  public void periodic() {
    frontLeft.periodic();
    frontRight.periodic();
    rearLeft.periodic();
    rearRight.periodic();

    gyro.periodic();

    poseEstimator.update(Rotation2d.fromDegrees(gyro.getAngle()), getPositions());
    
    field.setRobotPose(poseEstimator.getEstimatedPosition());

    publisherStates.set(getStates());
    publisherPose2d.set(poseEstimator.getEstimatedPosition());
    
    publisherPose3d.set(new Pose3d(poseEstimator.getEstimatedPosition()));

  }
}
